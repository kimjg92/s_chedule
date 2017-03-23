package com.shinhan.httpexam;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View view){

        EditText editText = (EditText)findViewById(R.id.input01);
        String urlString = editText.getText().toString();
        if(urlString.indexOf("http")!=-1) {
            new LoadHTML().execute(urlString); // execute에 여러 파라미터 넣을 수 있음. 여기선 EditText로 입력된 url에 접속
        }
    }

    class LoadHTML extends AsyncTask<String, String, String>{
        /*
        * 대부분의 통신은 비동기로 이루어지며, 쓰레드를 이용한다.
        * 그리고 시작과 끝을 알수 있다.
        * 이를 통해 로딩중에 똥글뱅이 도는걸 할 수 있음.
        * 이건 그냥 외우는게 좋음
        * */
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            /*
             * doInBackground 수행 전에 시작되는 함수.
             */
            super.onPreExecute();
            dialog.setMessage("HTML 요청 중...");
            dialog.show(); // Progress Dialog 출력
        }

        @Override
        protected void onPostExecute(String s) {
            /*
             * doInBackground 수행 후에 시작되는 함수.
             */
            super.onPostExecute(s);
            dialog.dismiss(); // Progress Dialog 종료
            TextView textView = (TextView)findViewById(R.id.txtMsg);
            textView.setText(s); // request에 대한 respons 결과 값이 string s에 저장됨
        }

        @Override
        protected String doInBackground(String... params) {
            /**
             * 스레드에서 백그라운드로 돌릴 내용
             * 즉, 실제 통신이 처리되는 부분
             */
            StringBuilder output = new StringBuilder();
            //통신 부분은 반드시 try-catch로 예외처리 해주어야 한다.
            try{
                URL url = new URL(params[0]);//전달받은 urlString으로 URL 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    int resCode = conn.getResponseCode();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); //한줄씩 읽기 위해 버퍼리더를 사용
                    String line;//웹 서버로부터 수신되는 데이터를 처리
                    while ((line = reader.readLine()) != null) {
                        output.append(line);//한줄 씩 읽어서
                    }
                    reader.close();
                    conn.disconnect();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return output.toString();//여기서 return 하는 값이 onPostExecute의 인자로 넘어감
        }
    }
}
