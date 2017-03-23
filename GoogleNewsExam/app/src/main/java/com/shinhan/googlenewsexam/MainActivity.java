package com.shinhan.googlenewsexam;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.shinhan.googlenewsexam.R.id.dataItem04;


public class MainActivity extends AppCompatActivity {

    private static String rssUrl = "http://api.sbs.co.kr/xml/news/rss.jsp?pmDiv=entertainment";
    class RSSNewsItem{
        String title;
        String link;
        String description;
        String pubDate;
        String author;
        String category;
        RSSNewsItem(String title, String link, String description, String pubDate, String author, String category){
            this.title = title;
            this.link = link;
            this.description = description;
            this.pubDate = pubDate;
            this.author = author;
            this.category = category;
        }
    }
    class RSSListAdapter extends ArrayAdapter{
        public RSSListAdapter(Context context) {
            super(context, R.layout.listitem, newsItemList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;

            if(convertView != null){
                view = convertView; // 기존에 만들어진 view가 있으면 그것을 활용하겠다.
            } else {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem,null,true); //setContentView와 같은 기능
            }
            TextView dataItem01 = (TextView)view.findViewById(R.id.dataItem01);
            TextView dataItem02 = (TextView)view.findViewById(R.id.dataItem02);
            TextView dataItem03 = (TextView)view.findViewById(R.id.dataItem03);
            WebView dataItem04 = (WebView) view.findViewById(R.id.dataItem04);
            //findViewById는 layout에서 찾음 근데 이건 view에서 찾는것이니 view.findViewById해야함

            dataItem01.setText(newsItemList.get(position).title);
            dataItem02.setText(newsItemList.get(position).pubDate);
            dataItem03.setText(newsItemList.get(position).category);
            dataItem03.setTextColor(Color.RED);
            //dataItem04.loadUrl(newsItemList.get(position).link);
            dataItem04.loadData(newsItemList.get(position).description, "text/html; charset=UTF-8", null);

            return view;
        }
    }


    ArrayList<RSSNewsItem> newsItemList = new ArrayList<RSSNewsItem>();
    RSSListAdapter listAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsItemList.add(new RSSNewsItem("제목1","https://m.naver.com","설명데쓰요1","날짜1","작성자는 나","카테고리1"));
        newsItemList.add(new RSSNewsItem("제목2","https://m.google.com","설명데쓰요2","날짜2","작성자는 너","카테고리2"));
        newsItemList.add(new RSSNewsItem("제목3","https://m.youtube.com","설명데쓰요3","날짜3","작성자는 우리","카테고리3"));

        ListView listView = (ListView)findViewById(R.id.listView);
        listAdaptor = new RSSListAdapter(MainActivity.this);
        listView.setAdapter(listAdaptor);
        EditText editText = (EditText) findViewById(R.id.input01);
        editText.setText(rssUrl);
    }

    public void onButtonClicked(View view){
        EditText editText=(EditText)findViewById(R.id.input01);
        String urlString = editText.getText().toString();
        if(urlString.indexOf("http") != -1){   // http라는 문자열이 포함되어 있는지 확인
            new LoadXML().execute(urlString);         // 입력한 url에 접속
        }
    }



    class LoadXML extends AsyncTask<String, String, String> {
        /*
        * 대부분의 통신은 비동기로 이루어지며, 쓰레드를 이용한다.
        * 그리고 시작과 끝을 알수 있다.
        * 이를 통해 로딩중에 똥글뱅이 도는걸 할 수 있음.
        * 이건 그냥 외우는게 좋음
        * */
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private int processDocument(Document document){
            int count = 0;
            Element documentElement = document.getDocumentElement();
            NodeList nodeList = documentElement.getElementsByTagName("item");
            if((nodeList != null )&& (nodeList.getLength() > 0)){ // 아이템이 존재하면,
                for(int i = 0; i < nodeList.getLength(); i++){ // 아이템 개수만큼 반복
                    RSSNewsItem newsItem = dissectNode(nodeList, i); // 아이템 정보 추출
                        if(newsItem != null){ //정상적인 아이템 객체이면 동적배열에 추가
                            newsItemList.add(newsItem);
                            count++;
                        }
                }
            }

            return count;

        }

        private RSSNewsItem dissectNode(NodeList nodeList, int index){//아이템정보 추출
            RSSNewsItem newsItem = null;
            try{
                Element entry = (Element)nodeList.item(index); //아이템 객체 추출
                Element title = (Element)entry.getElementsByTagName("title").item(0);
                Element link = (Element)entry.getElementsByTagName("link").item(0);
                Element description = (Element)entry.getElementsByTagName("description").item(0);
                Element pubData = (Element)entry.getElementsByTagName("pubData").item(0);
                Element author = (Element)entry.getElementsByTagName("author").item(0);
                Element category = (Element)entry.getElementsByTagName("category").item(0);
                //DOM 파서이며, XML 태그 사이에 있는 데이터들을 추출하는 코드

                String titleValue = getElementString(title);
                String linkValue = getElementString(link);
                String descriptionValue = getElementString(description);
                String pubDateValue = getElementString(pubData);
                String authorValue = getElementString(author);
                String categoryValue = getElementString(category);

                newsItem = new RSSNewsItem(titleValue,linkValue,descriptionValue,pubDateValue,authorValue,categoryValue);

            }catch (Exception e){

            }
            return newsItem;
        }

        private String getElementString(Element element){//Element 객체에서 문자열 추출
            String value = null;
            if(element != null){
                Node firstChild = element.getFirstChild();
                if(firstChild != null){
                    value = firstChild.getNodeValue();
                }
            }
            return value;
        }

        @Override
        protected void onPreExecute() {
            /*
             * doInBackground 수행 전에 시작되는 함수.
             */
            super.onPreExecute();
            dialog.setMessage("news rss 요청 중...");
            dialog.show(); // Progress Dialog  출력
        }

        @Override
        protected void onPostExecute(String s) {
            /*
             * doInBackground 수행 후에 시작되는 함수.
             */
            super.onPostExecute(s);
            dialog.dismiss(); // Progress Dialog 종료
            //XML 파싱---------------------------------------------
            listAdaptor.notifyDataSetChanged();
            //ListView 출력----------------------------------------

        }

        @Override
        protected String doInBackground(String... params) {
            /**
             * 스레드에서 백그라운드로 돌릴 내용
             * 즉, 실제 통신이 처리되는 부분
             */
            StringBuilder output = new StringBuilder();
            //통신 부분은 반드시 try-catch로 예외처리 해주어야 한다.
            try {
                URL url = new URL(params[0]);//전달받은 urlString으로 URL 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    int resCode = conn.getResponseCode();


                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = builderFactory.newDocumentBuilder();
                    InputStream inputStream = conn.getInputStream(); //웹서버 결과값 수신
                    Document document = builder.parse(inputStream); //도큐먼트객체 생성
                    int count = processDocument(document); //XML 파싱 (DOM 방식)
                    Log.i("count@@",count+"");
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return output.toString();//여기서 return 하는 값이 onPostExecute의 인자로 넘어감
        }
    }
}
