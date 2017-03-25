package com.shinhan.fcmexam;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


public class MainActivity extends AppCompatActivity {

    final static String TAG = "Firebase Database";
/**
 * 1.data service to activity
 * 2.호스트 : 데이터베이스 변동 메시지 보내고, 계산하여 새로운 값 저장, 그리고 user들에게 push보내기
 * 3.유저 : push메시지 받고, 데이터베이스 조회
 * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy
                    = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public void onClickHostPageButton ( View v){
        Intent i = new Intent(this, Host.class);
        startActivity(i);
    }

}
