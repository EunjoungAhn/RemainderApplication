package com.example.remainderapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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

        //노티피케이션 설정
        // 앞서 설정한 값으로 보여주기
        // 없으면 디폴트 값은 현재시간
        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate);
        //Toast.makeText(getApplicationContext(), "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


        // 이전 설정값으로 TimePicker 초기화
        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

//        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
//        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));
//
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            picker.setHour(pre_hour);
//            picker.setMinute(pre_minute);
//        } else {
//            picker.setCurrentHour(pre_hour);
//            picker.setCurrentMinute(pre_minute);
//        }


//        Dialog 생성 시 getApplicationContext()를 사용해서 나는 에러.
//        (Activity 이름).this로 변경하면 해결.
        adapter = new ListAdapter(MainActivity.this);// context가 액티비티에 대한 생명주기를 가지고 있어서 영향이 있었다.
        //그래서 adapter = new ListAdapter(MainActivity.this) 로 바꾸어줘야  한다.

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
            rv.setAdapter(adapter);
            adapter.addItem(reminder);
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
                        final Reminder reminder = new Reminder();
                        reminder.setContent(content);
                        reminder.setTime(hAddM);

                        //ListAdapter에 객체 추가
                        adapter.addItem(reminder);
                        adapter.notifyDataSetChanged();
                        //EditText 초기화
                        content_et.setText("");

                        //리사이클 뷰 순서 값 확인하기
                        int rvNum = rv.getAdapter().getItemCount();
                        Log.d("mainActivity의 뷰 순서 값", String.valueOf(rvNum));

                        //DB에 오늘 알림 데이터 등록
                        sqlDB = dbHelper.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO remind VALUES ('" + rvNum + "','" + content + "','" + hAddM + "', NULL);'");
                        sqlDB.close();

                        //데이터 추가 확인 토스트 띄우기
                        Toast.makeText(MainActivity.this, hour + "시" + minute + "분" + "에 리마인더가 추가되었습니다", Toast.LENGTH_SHORT).show();
                        keyboardHide.hideSoftInputFromWindow(finish_add.getWindowToken(), 0);
                        container.removeAllViews();

                        //DB에 리마인더 등록시 알람 설정
//                        int hour2, hour_24, minute2;
//                        String am_pm;
//                        if (Build.VERSION.SDK_INT >= 23) {
////                            hour_24 = picker.getHour();
////                            minute2 = picker.getMinute();
//                            hour_24 = hour;
//                            minute2 = minute;
//                        } else {
//                            hour_24 = picker.getCurrentHour();
//                            minute2 = picker.getCurrentMinute();
//                        }
//                        if (hour_24 > 12) {
//                            am_pm = "PM";
//                            hour2 = hour_24 - 12;
//                        } else {
//                            hour2 = hour_24;
//                            am_pm = "AM";
//                        }

                        // 현재 지정된 시간으로 알람 시간 설정
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
//                        calendar.set(Calendar.HOUR_OF_DAY, hour_24);
//                        calendar.set(Calendar.MINUTE, minute2);
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1);
                        }

                        Date currentDateTime = calendar.getTime();
                        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                        Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

                        //  Preference에 설정한 값 저장
                        SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                        editor.putLong("nextNotifyTime", (long) calendar.getTimeInMillis());
                        editor.apply();

                        diaryNotification(calendar);
                    }
                });
            }
        });

        //RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

//        rv.setAdapter(adapter);
        //ItemTouchHelper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        //RecyclerView에 ItemTouchHelper 붙이기
        helper.attachToRecyclerView(rv);

        //intro화면으로 돌아가기
        back_bt = findViewById(R.id.back_bt);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(changeActivity);
            }
        });
    }

    void diaryNotification(Calendar calendar) {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }
}