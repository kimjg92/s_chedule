package com.shinhan.fcmexam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Iterator;


public class MainActivity extends Activity {

    final static String TAG = "Firebase Database";
    private String userToken;
    private DataSnapshot mDataSnapshot;
    public BankFragmentListViewAdapter adapter;
    public ListView listView;

    /**
     * 1.data service to activity
     * 2.호스트 : 데이터베이스 변동 메시지 보내고, 계산하여 새로운 값 저장, 그리고 user들에게 push보내기
     * 3.유저 : push메시지 받고, 데이터베이스 조회
     *
     * 리스트뷰 순서 변경
     * 다이얼로그 조건 설정하기
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy
                    = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        CheckTypesTask task = new CheckTypesTask();
        task.execute();

        listView = (ListView) findViewById(R.id.bank_fragmnet_listview);
        adapter = new BankFragmentListViewAdapter();

        //connectDB();
        //loadTradeHistory(userToken);


    }

    public void onClickAfterConnectionButton(View v) {
        userToken = getUserToken();
        adapter.clearItem();

        listView.setAdapter(adapter);


        loadTradeHistory(userToken);

        Log.d("load Test", mDataSnapshot.child("users").getChildren().iterator().toString());
    }

    public void loadTradeHistory(String userToken) {
        FirebaseInstanceId.getInstance().getToken();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Log.d("Token test", mDataSnapshot + "");

        //토큰 기반 계좌 찾는 코드 추가 작성 필요
        Log.d("Token Test", mDataSnapshot.child("accounts/110-123-456789/Trade_History").toString());
        Iterator<DataSnapshot> tradeHistory = mDataSnapshot.child("accounts/110-123-456789/Trade_History").getChildren().iterator();

        DataSnapshot historyDate;
        int maxDate = 5;
        int countDate = 0;

        while (tradeHistory.hasNext()) {
            if (countDate++ > maxDate) {
                break;
            }
            historyDate = tradeHistory.next();
            Iterator<DataSnapshot> historyTime = historyDate.getChildren().iterator();
            DataSnapshot history;
            while (historyTime.hasNext()) {
                history = historyTime.next();
                Log.d("History Test", historyDate.getKey() + " / " + history.getKey().toString() + " / " + history.getValue().toString());

                String date = historyDate.getKey().toString().replaceAll("_",".").substring(5);
                String time = history.getKey().toString().substring(0,5);
                String amount = history.getValue().toString();

                if (amount.contains("+")) {
                    amount = amount.replace("+", "");
                    //   public void addItem(Drawable icon, String Account, String Amount,String Date, String Time) {
                    adapter.addItem(ContextCompat.getDrawable(this, R.drawable.smail_inform_ic01), "110-123-456789", amount,date,time);
                } else {
                    amount = amount.replace("-", "");
                    adapter.addItem(ContextCompat.getDrawable(this, R.drawable.smail_inform_ic03), "110-123-456789", amount,date,time);
                }

            }
        }
        listView.setAdapter(adapter);
    }

    private String getUserToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }


    private void connectDB() {
        FirebaseDatabase database;
        DatabaseReference myRef;

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


        while (mDataSnapshot == null) {
            Log.d("Token test", mDataSnapshot + "");
        }
        Log.d("Token test", mDataSnapshot + "");
    }

    public void onClickHostPageButton2(View v) {
        Log.d("Token test", mDataSnapshot + "");
    }

    public void onClickHostPageButton(View v) {
        Intent i = new Intent(this, Host.class);
        startActivity(i);
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            FirebaseDatabase database;
            DatabaseReference myRef;

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


            while (mDataSnapshot != null) {
                Log.d("Token test", mDataSnapshot + "");
            }
            Log.d("Token test", mDataSnapshot + "");

            try {
                for (int i = 0; i < 5; i++) {
                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }


}
