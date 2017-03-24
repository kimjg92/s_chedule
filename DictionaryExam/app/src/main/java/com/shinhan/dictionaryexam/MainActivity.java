package com.shinhan.dictionaryexam;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onButtonClicked (View view){
        EditText word = (EditText)findViewById(R.id.word);
        EditText definition = (EditText)findViewById(R.id.definition);
        String wordString = word.getText().toString();
        String definitionString = definition.getText().toString();
        if(!wordString.isEmpty() && !definitionString.isEmpty()){ //단어 뜻을 입력했으면
            writeDatabase(wordString, definitionString);//DB에 저장
            readDatabase();//DB 내용 읽기
            Log.i("count","실행");
        }
    }

    public void writeDatabase(String word, String definition){
        Dictionary dictionary = new Dictionary(MainActivity.this);  //DB파일 열기
        SQLiteDatabase database = dictionary.getWritableDatabase(); //쓰기모드로 열기
        ContentValues values = new ContentValues();                 //저장 객체 생성
        values.put("word",word);                                    //Word col에 대이터 저장
        values.put("definition",definition);
        database.insert(Dictionary.TABLE_NAME,null,values);         //DB에 데이터 insert
    }

    public void readDatabase(){
        Dictionary dictionary = new Dictionary(MainActivity.this);
        SQLiteDatabase database = dictionary.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+Dictionary.TABLE_NAME,null);
        Log.i("cusor count",cursor.getCount()+"");
        String [] words = new String[cursor.getCount()];
        for(int i = 0 ; i < cursor.getCount(); i++){
            cursor.moveToNext();
            words[i]=cursor.getString(1)+" ("+cursor.getString(2)+")";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,words);
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }
}
