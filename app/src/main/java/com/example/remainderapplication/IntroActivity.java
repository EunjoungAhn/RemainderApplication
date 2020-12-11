package com.example.remainderapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {
    Button todayButton, laterButton, allButton, goTostopWatchBtn;
    SQLiteDatabase sqlDB;
    DbHelper dbHelper;
    TextView today_count, all_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //DB에 저장되어 있는 데이터 개수 카운트 화면에 출력
        today_count = findViewById(R.id.today_count);
        all_count = findViewById(R.id.all_count);
         int todayNum = count();
         int allDayNum = count();
        today_count.setText(String.valueOf(todayNum));
        all_count.setText(String.valueOf(allDayNum));

        //오늘 알림 화면으로 이동 버튼
        todayButton = findViewById(R.id.todayButton);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(changeActivity);
            }
        });

        //스톱워치 화면으로 이동 버튼
        goTostopWatchBtn = findViewById(R.id.goTostopWatchBtn);
        goTostopWatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(getApplicationContext(), TreadActivity.class);
                startActivity(changeActivity);
            }
        });
    }

    //DB에 저장되어 있는 데이터 개수 카운트 함수
    public int count() {
        int count = 0;
        //DB에 등록되어 있는 오늘 알림 전체 가져오기
        dbHelper = new DbHelper(getApplicationContext());
        sqlDB = dbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM remind;", null);
        count = cursor.getCount();
        return count;
    }
}
