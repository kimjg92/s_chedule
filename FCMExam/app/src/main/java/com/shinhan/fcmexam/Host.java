package com.shinhan.fcmexam;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.data;
import static android.R.attr.key;

public class Host extends AppCompatActivity {
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public static String info;
    public OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new OkHttpClient();


        setContentView(R.layout.activity_host);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d("dataSnapshot Added",prevChildKey+" / "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d("dataSnapshot chned",prevChildKey+" / "+dataSnapshot.getValue().toString());

                Toast.makeText(getApplicationContext(),"tg",Toast.LENGTH_LONG).show();

               /* Post changedPost = dataSnapshot.getValue(Post.class);
                System.out.println("The updated post title is: " + changedPost.title);*/
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot rmd",dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d("dataSnapshot mvd",prevChildKey+" / "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        myRef.child("T").setValue("1");
        myRef.child("T").setValue("2");

        RemoteMessage message = new RemoteMessage.Builder("150136257212@gcm.googleapis.com")
                .setMessageId(Integer.toString(123))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build();
        FirebaseMessaging.getInstance().send(message);


        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder("150136257212@gcm.googleapis.com")
                .setMessageId(Integer.toString(123))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());

    }
    public void onClickgetAccountInfo(View v){

    }

    public void onClickSendMessage(View v) {
        /*FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder("150136257212@gcm.googleapis.com")
                .setMessageId(Integer.toString(1245555))
                .addData("my_message", "Hello World")
                .addData("my_action", "SAY_HELLO")
                .build());*/
        EditText Account = (EditText)findViewById(R.id.Account);
        EditText Money = (EditText)findViewById(R.id.Money);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts/"+Account.getText().toString()+"/money");


        myRef.setValue(Money.getText().toString());
    }
    public void onClickSendPushMessage(View v){
        try {

            String httpUrl_ = "https://fcm.googleapis.com/fcm/send";
            POST(httpUrl_);//창균주임님
            sendPost();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendPost(){

    }
//
//    public void sendPost()throws IOException{
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        String url = "https://fcm.googleapis.com/fcm/send";
//        String json = "{ \"data\": {"+
//                "\"score\": \"5x1\","+
//                "\"time\": \"15:10\""+
//                "},"+
//                "\"to\" : \"fS4IfAcFq08:APA91bG5eVQAIW8Q4eUE2Qh0JD9zccSz4Qlatn8DiHrFUSAKxT7hJeJRmZ-S0j4HdmfdmZ_X5DDLaIopA1SUPSnp8S_w-kisE-_PtnZrP2N8lal_Ag8mrr_WgBiUwOy5yn7fgZHs6meP\"}";
//
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder().url(url).post(body).build();
//        Response response = client.newCall(request).execute();
//        System.out.println("@@@@@ : " + response.body().string());
//    }

//    { "data": {
//        "score": "5x1",
//                "time": "15:10"
//    },
//        "to" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
//    }
//    private String SendHttpPost()
//    {
//        JSONArray arrJSON = new JSONArray();
//        JSONObject total = new JSONObject();
//        JSONObject data = new JSONObject();
//        JSONObject to = new JSONObject();
//        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//        try {
//            // JSON Object로 JSON Array 조합
//
//            data.put("score", "5x1");
//            data.put("time", "15:10");
//
//            to.put("to","dqdC6AAAUdI:APA91bG5RWl-LJRysvdj3bPqxxg3p6cpiB9JO0ba2fA9nfrVKAD7hKZMBZ4H8rqqsk62t4Rb5BJch5QaCo9SGJQfN4A-ZgXzNS6UTJQPP4IIUKJq4jizCgxqepViMHcveW9qWgPlFlFW");
//
//
//
//            arrJSON.put(data);
//            arrJSON.put(to);
//
//            Log.e("park",arrJSON.toString());
//            // NameValuePair 구조로 변환
//
////            nameValuePairs.add(new BasicNameValuePair("Key", "데이터값"));
//            nameValuePairs.add(new BasicNameValuePair("request",arrJSON.toString()));
//
//            Log.e("park",nameValuePairs.toString());
//
//        } catch (Exception e) {
//            // TODO 자동 생성된 catch 블록
//            e.printStackTrace();
//        }
//
//        HttpResponse response;
//        // 연결 HttpClient 객체 생성
//        HttpClient client = new DefaultHttpClient();
//        // 객체 연결 설정 부분, 연결 최대시간 등등
//        HttpParams params = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(params, 5000);
//        HttpConnectionParams.setSoTimeout(params, 5000);
//        // Post객체 생성
//        String httpUrl = "https://fcm.googleapis.com/fcm/send";
//        HttpPost httpPost = new HttpPost(httpUrl);
//        String sinput = "";
//        try {
//            // 컨텐츠타입
//            httpPost.setHeader("Authorization","key=AIzaSyBg573ZXsZ1xLKW3jqD4v3gd1JLyBQf7-w");
//            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
//
//
//            // 데이터
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//            // 전송
//            response = client.execute(httpPost);
//
//            // 결과확인
//            if (response != null)
//            {
//                sinput =  EntityUtils.toString(response.getEntity());
//
//                Log.e("park_result",sinput);
//                return sinput;
//            }
//            else
//                return null;
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static String POST(String url){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("score", "5x1");
            jsonObject.accumulate("time", "15:10");
            jsonObject.accumulate("to","dqdC6AAAUdI:APA91bG5RWl-LJRysvdj3bPqxxg3p6cpiB9JO0ba2fA9nfrVKAD7hKZMBZ4H8rqqsk62t4Rb5BJch5QaCo9SGJQfN4A-ZgXzNS6UTJQPP4IIUKJq4jizCgxqepViMHcveW9qWgPlFlFW");

            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("park~~~~~~~~~~",json);

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Authorization","key=AIzaSyBg573ZXsZ1xLKW3jqD4v3gd1JLyBQf7-w");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("euc-kr"));
            os.flush();
            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
//                    result = convertInputStreamToString(is);
                    result ="";
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;

    }
}
