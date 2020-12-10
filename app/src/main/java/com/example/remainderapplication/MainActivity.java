package com.example.remainderapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText content_et;
    Button add_bt, back_bt;
    RecyclerView rv;
    ListAdapter adapter;
    ItemTouchHelper helper;
    SQLiteDatabase sqlDB;
    DbHelper dbHelper;
    List<String> remindList;
    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Dialog 생성 시 getApplicationContext()를 사용해서 나는 에러.
//        (Activity 이름).this로 변경하면 해결.
        adapter = new ListAdapter(MainActivity.this);// context가 액티비티에 대한 생명주기를 가지고 있어서 영향이 있었다.
        //그래서 dapter = new ListAdapter(MainActivity.this) 로 바꾸어줘야  한다.

        //cardview 설정
        rv = findViewById(R.id.rv);

        //DB에서 가져올 데이터 담을 리스트 생성
//        remindList = new ArrayList<String>();

        //DB에 등록되어 있는 오늘 알림 전체 가져오기
        dbHelper = new DbHelper(getApplicationContext());
        sqlDB = dbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM remind;", null);

        Reminder reminder = new Reminder();

        while (cursor.moveToNext()) {
            String strContent = cursor.getString(1);
            String strTime = cursor.getString(2);

            Log.d("strContent 담긴 값", String.valueOf(strContent));
            Log.d("strTime 담긴 값", String.valueOf(strTime));

            reminder.setContent(strContent);
            reminder.setTime(strTime);
            adapter.addItem(reminder);
            rv.setAdapter(adapter);
        }
        //리스트에 담긴 값 확인하기
        Log.d("리스트에 담긴 값", String.valueOf(remindList));

        cursor.close();
        sqlDB.close();

        add_bt = findViewById(R.id.add_bt);
        final LinearLayout container = findViewById(R.id.container);

        //오늘의 미리 알림 추가하기
        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeAllViews();
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                //프레그먼트이기 때문에 activity에서 findViewById 처럼 적용 되는게 아니라
                //activity를 통해서 전환되야 하는것이라서 그냥 findViewById는 안된다.
                // 하나의 인플레이터를 참조변수에 넣어준 후에 rootView.findViewById 로 적용 후 찾는다.
                ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.today_add_inflation, container, true);
                //inflater.inflate(R.layout.today_add_inflation, container, true);

                final Button finish_add = (Button) rootView.findViewById(R.id.finish_add);
                content_et = rootView.findViewById(R.id.content_et);

                //완료 하였으면, 키보드 숨기기 설정
                final InputMethodManager keyboardHide;
                keyboardHide = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                //DB 등록을 위한 객체 생성
                final DbHelper dbHelper = new DbHelper(getApplicationContext());

                //인플레이션의 완료 버튼(리마인더 등록)
                finish_add.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //시간 등록 설정
                        TimePicker picker = (TimePicker) findViewById(R.id.timePicker);
                        int hour = picker.getHour();
                        int minute = picker.getMinute();
                        String hAddM = hour + ":" + minute;//시간 스트링으로 다시 저장하기

                        //EditText 입력된 값 가져오기
                        String content = content_et.getText().toString();
                        Reminder reminder = new Reminder();
                        reminder.setContent(content);
                        reminder.setTime(hAddM);

                        //ListAdapter에 객체 추가
                        adapter.addItem(reminder);
                        adapter.notifyDataSetChanged();
                        //EditText 초기화
                        content_et.setText("");

                        //리사이클 뷰 순서 값 확인하기
                        int rnum = rv.getAdapter().getItemCount();
                        Log.d("mainActivity의 뷰 순서 값", String.valueOf(rnum));

                        //DB에 오늘 알림 데이터 등록
                        sqlDB = dbHelper.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO remind VALUES ('" + rnum + "','" + content + "','" + hAddM + "', NULL);'");
                        sqlDB.close();


                        //데이터 추가 확인 토스트 띄우기
                        Toast.makeText(MainActivity.this, hour + "시" + minute + "분" + "에 리마인더가 추가되었습니다", Toast.LENGTH_SHORT).show();
                        keyboardHide.hideSoftInputFromWindow(finish_add.getWindowToken(), 0);
                        container.removeAllViews();
                    }
                });
            }
        });

        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        rv.setAdapter(adapter);
        //ItemTouchHelper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        //RecyclerView에 ItemTouchHelper 붙이기
        helper.attachToRecyclerView(rv);

        //메인화면으로 돌아가기
        back_bt = findViewById(R.id.back_bt);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(changeActivity);
            }
        });
    }
}