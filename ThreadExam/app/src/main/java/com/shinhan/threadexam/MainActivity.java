package com.shinhan.threadexam;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.R.attr.value;

public class MainActivity extends AppCompatActivity {
    ProgressHandler progressHandler = new ProgressHandler();
    Handler handler = new Handler();
    ProgressRunnable progressRunnable = new ProgressRunnable();
    ProgressRunnableOne progressRunnableOne = new ProgressRunnableOne();
    boolean isRunning = false;
    ProgressBar progressBar1, progressBar2;
    ProgressBar progressBar3, progressBar4;
    TextView textView1,textView2, textView3, textView4;

    public class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar1.incrementProgressBy(5);
            if(progressBar1.getProgress() == 100){
                textView1.setText("Done");
            }{
                textView1.setText("Working..."+progressBar1.getProgress());
            }
            Log.d("getMAX",progressBar1.getMax()+" / "+progressBar1.getProgress());
        }
    }

    public class ProgressRunnable implements Runnable{

        @Override
        public void run() {
            progressBar2.incrementProgressBy(1);
            if(progressBar2.getProgress() == progressBar2.getMax()){
                textView2.setText("Done");
            }{
                textView2.setText("Working..."+progressBar2.getProgress());
            }
        }
    }

    public class ProgressRunnableOne implements Runnable{
        @Override
        public void run() {
            progressBar3.incrementProgressBy(2);
            if(progressBar3.getProgress() == progressBar3.getMax()){
                textView3.setText("Done");
            }{
                textView3.setText("Working..."+progressBar3.getProgress());
            }
        }
    }

    class ProgressTask extends AsyncTask<Integer, Integer, Integer>{
        private int value =0;
        @Override
        protected Integer doInBackground(Integer...params) { //UI접근 불가
            for(int i = 0 ; i < 10 && isRunning ; i++){
                value +=10;
                publishProgress(value);
                try{
                    Thread.sleep(2000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() { //doInBackground 전에 호출
            super.onPreExecute();
            value = 0;
            progressBar4.setProgress(value);
        }

        @Override
        protected void onPostExecute(Integer integer) { //doInBackground 후에 호출
            super.onPostExecute(integer);
            textView4.setText("Done");
        }

        @Override
        protected void onProgressUpdate(Integer... integers) { //doInBackground 중 필요시 호출
            super.onProgressUpdate(integers);
            progressBar4.setProgress(integers[0]);
            textView4.setText("Working..."+integers[0]);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        progressBar1 = (ProgressBar)findViewById(R.id.progress1);
        progressBar2 = (ProgressBar)findViewById(R.id.progress2);


        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        progressBar3 = (ProgressBar)findViewById(R.id.progress3);
        progressBar4 = (ProgressBar)findViewById(R.id.progress4);

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar1.setProgress(0);//프로그레스 초기화
        progressBar2.setProgress(0);
        Thread thread =new Thread( new Runnable() { //스레드 정의
            @Override
            public void run() { //스레드에서 실행되는 영역 (메인 UI접근 불가. 토스트 등등 모두 포함)
                try{
                    for(int i = 0 ; i < 100 && isRunning ; i++){
                        Thread.sleep(200);
                        //1.핸들러를 이용한 메시지 전송
                        if(i%5==0) {
                            Message msg = progressHandler.obtainMessage();
                            progressHandler.sendMessage(msg);
                        }
                        //2.Runnable 객체를 싱행하는 방법
                        handler.post(progressRunnable);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i = 0 ; i < 100 && isRunning ; i++){
                        Thread.sleep(200);
                        handler.post(progressRunnableOne);
                    }
                }catch (Exception e){

                }
            }
        });
        isRunning = true;
        thread.start();
        thread2.start();
        new ProgressTask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }
}
