package com.shinhan.fcmexam;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Host extends Activity {
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public static String info;
    //public OkHttpClient client;
    public DataSnapshot mDataSnapshot;
    public String TAG = "ref Trace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //client = new OkHttpClient();

        setContentView(R.layout.activity_host);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("dataSnapshot Added", dataSnapshot.getRef().toString());
                mDataSnapshot = dataSnapshot;
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        myRef.child("state").setValue("connection....");
        myRef.child("state").removeValue();

    }

    public void onClickgetAccountInfo(View v) {

    }

    public void onClickTradeButton(View v) {
        EditText Account = (EditText) findViewById(R.id.Account);
        EditText Money = (EditText) findViewById(R.id.Money);
        RadioButton deposit = (RadioButton) findViewById(R.id.deposit);
        RadioButton withdraw = (RadioButton) findViewById(R.id.withdraw);

        String accountNum = Account.getText().toString();
        String accountUsers;
        String tradeDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date(System.currentTimeMillis()));
        String tradeTime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
        int moneyAmount = Integer.parseInt(Money.getText().toString());
        int balance;

        //if(Account.getText().equals("") || Money.getText().equals("")) {
        if (mDataSnapshot.child("accounts/" + accountNum + "/balance").getValue() != null) {

            if (deposit.isChecked()) {

                Log.e(TAG, "before deposit : " + myRef.toString());
                balance = Integer.parseInt(mDataSnapshot.child("accounts/" + accountNum + "/balance").getValue().toString());
                balance = balance + moneyAmount;

                accountUsers = mDataSnapshot.child("accounts/" + accountNum + "/users").getValue().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("accounts/" + Account.getText().toString() + "/balance");
                myRef.setValue(String.valueOf(balance));
                myRef = database.getReference("accounts/" + Account.getText().toString() + "/Trade_History/"+ tradeDate+"/"+tradeTime);
                myRef.setValue("+"+moneyAmount);

                Log.e(TAG, "after deposit : " + myRef.toString());

                sendTradeMessage(accountNum, moneyAmount, accountUsers, "deposit");


            } else if (withdraw.isChecked()) {
                Log.e(TAG, "before deposit : " + myRef.toString());
                balance = Integer.parseInt(mDataSnapshot.child("accounts/" + accountNum + "/balance").getValue().toString());
                balance = balance - moneyAmount;

                accountUsers = mDataSnapshot.child("accounts/" + accountNum + "/users").getValue().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("accounts/" + Account.getText().toString() + "/balance");
                myRef.setValue(String.valueOf(balance));
                myRef = database.getReference("accounts/" + Account.getText().toString() + "/Trade_History/"+ tradeDate+"/"+tradeTime);
                myRef.setValue("-"+moneyAmount);

                Log.e(TAG, "after deposit : " + myRef.toString());

                sendTradeMessage(accountNum, moneyAmount, accountUsers, "withdraw");

            } else {
                Toast.makeText(this, "거래구분 선택 하세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "after trade : " + myRef.toString());
            Toast.makeText(this, "DataSnapshot Result is invalid", Toast.LENGTH_SHORT).show();
        }
        //}


    }

    public void onClickSendPushMessage(View v) {
        String httpUrl_ = "https://fcm.googleapis.com/fcm/send";
        String Token = "dKbqgbCA9K4:APA91bE0XuMU-ulLrBAFAAP5F9oZfuYcrxFcg9dwS8Q_bGY-jpFES_kgL0vAtNMBx0Ed_8OyYkIFJdrldcb77pqkZ3UMjFiGFgsR2SPAyvnpTT4eZHygT_E9O9UA9GJWQ4ysJ68i-_jN";
        //POST(httpUrl_,Token);//창균주임님
    }

    public void sendTradeMessage(String accNumber, int amount, String users, String tradeType) {
        String httpUrl_ = "https://fcm.googleapis.com/fcm/send";

        StringTokenizer userList = new StringTokenizer(users, ",\"");
        while (userList.hasMoreTokens()) {
            String user = userList.nextToken();
            //Log.e("user @@ : ", mDataSnapshot.child("users/" + user +"/Token").getValue().toString());
            POST(httpUrl_, mDataSnapshot.child("users/" + user +"/Token").getValue().toString(), accNumber, amount, tradeType);
        }
    }

    public static String POST(String url, String Token, String accNumber, int amount, String tradeType) {
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();

            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            if (tradeType.equals("deposit")) {
                notificationObject.accumulate("body", "공용통장 " + accNumber + " 에 " + amount + "원이 입금되었습니다.");
            } else {
                notificationObject.accumulate("body", "공용통장 " + accNumber + " 에서 " + amount + "원이 출금되었습니다.");
            }

            notificationObject.accumulate("title", "S알리미");

            jsonObject.accumulate("notification", notificationObject);
            jsonObject.accumulate("to", Token);

            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("park~~~~~~~~~~", "@@@@" + json);

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Authorization", "key=AIzaSyBg573ZXsZ1xLKW3jqD4v3gd1JLyBQf7-w");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if (is != null)
//                    result = convertInputStreamToString(is);
                    result = "";
                else
                    result = "Did not work!";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpCon.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;

    }

    public void onClickAddTokenButton(View v) {
        String token = FirebaseInstanceId.getInstance().getToken();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/galaxyA8/Token");
        myRef.setValue(token);
        Log.e(TAG, myRef.toString());
    }
}
