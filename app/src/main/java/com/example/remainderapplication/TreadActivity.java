package com.example.remainderapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TreadActivity extends AppCompatActivity {
    TextView clockTextView ;
    private static Handler mHandler ;
    Button stopWatchBtn, back_btFromTime;
    EditText timeResult;
    String strTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tread);

        timeResult = findViewById(R.id.timeResult);
        mHandler = new Handler();


        // 핸들러로 전달할 runnable 객체. 수신 스레드 실행.
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                strTime = sdf.format(cal.getTime());

                clockTextView = findViewById(R.id.clock);
                clockTextView.setText(strTime);
            }
        };

        // 새로운 스레드 실행 코드. 1초 단위로 현재 시각 표시 요청.
        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mHandler.post(runnable);
                }
            }
        }

        NewRunnable nr = new NewRunnable();
        Thread t = new Thread(nr);
        t.start();

        //시간 타이머 체크 버튼
        stopWatchBtn = findViewById(R.id.stopWatchBtn);
        stopWatchBtn.setOnClickListener(new View.OnClickListener() {
            int count;
            @Override
            public void onClick(View v) {
                count++;
                timeResult.append("       "+count+"번 스톱 워치 기록: "+strTime+ "\r\n");
            }
        });

        //메인화면으로 돌아가기
        back_btFromTime = findViewById(R.id.back_btFromTime);
        back_btFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(changeActivity);
            }
        });
    }
}