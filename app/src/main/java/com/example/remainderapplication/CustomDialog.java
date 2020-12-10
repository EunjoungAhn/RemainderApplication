package com.example.remainderapplication;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.remainderapplication.R.layout.customdialog;

public class CustomDialog extends Dialog {
    private OnDialogListener listener;
    private Button mod_bt;
    private EditText mod_content, mod_time;
    private String content;
    SQLiteDatabase sqlDB;
    List<String> remindList;

    public CustomDialog(final Context context, final int position, final Reminder reminder) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(customdialog);
        content = reminder.getContent();
        //내용, 시간 EditText에 값 채우기
        mod_content = findViewById(R.id.mod_content);
        mod_content.setText(content);

        //DB 등록을 위한 객체 생성
        final DbHelper dbHelper = new DbHelper(context);

        mod_bt = findViewById(R.id.mod_bt);
        mod_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    //다시 수정하여 값 set하기
                    String content = mod_content.getText().toString();
                    TimePicker picker = (TimePicker) findViewById(R.id.changeTimePicker);
                    int hour = picker.getHour();
                    int minute = picker.getMinute();
                    String hAddM = hour + ":" + minute;//시간 스트링으로 다시 저장하기

                    Reminder reminder = new Reminder(content, hAddM);
                    Log.d("버튼 안 값 들어옴", hAddM);

                    //cardview 설정
                    RecyclerView rv = findViewById(R.id.rv);
                    Log.d("뷰 값 확인: ", String.valueOf(rv));

                    //리스트에 담긴 값 확인하기
                    //DB에서 가져올 데이터 담을 리스트 생성
                    remindList = new ArrayList<String>();
                    Log.d("리스트 담긴 값 doalog에서 확인", String.valueOf(remindList));

//                    View view = RecyclerView.inflate(context, customdialog, rv);
//                    View rb = view.findViewById(R.id.rv);
//                    Log.d("Dialog에서 값 확인: ", String.valueOf(rv));

                    //DB에 오늘 알림 데이터 수정
                    sqlDB = dbHelper.getWritableDatabase();
                    sqlDB.execSQL("UPDATE remind SET content = '" + content + "', time = '" + hAddM + "' WHERE no = '" + "1" + "'");
                    sqlDB.close();

                    //Listener를 통해서 person객체 전달
                    listener.onFinish(position, reminder);
                    //다이얼로그 종료
                    dismiss();
                }
            }
        });
    }

    public void setDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}