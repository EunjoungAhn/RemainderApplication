package com.example.remainderapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> implements ItemTouchHelperListener, OnDialogListener {
    ArrayList<Reminder> items = new ArrayList<>();
    Context context;
    SQLiteDatabase sqlDB;

//    public ListAdapter(){
//    }
    public ListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater를 이용해서 원하는 레이아웃을 띄워줌
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //ItemViewHolder가 생성되고 넣어야할 코드들을 넣어준다.
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Reminder reminder) {
        //items에 Reminder객체 추가
        items.add(reminder);
        //추가후 Adapter에 데이터가 변경된것을 알림
        notifyDataSetChanged();
    }


    @Override
    public boolean onItemMove(int from_position, int to_position) {
        //이동할 객체 저장
//        Person person = items.get(from_position);
        Reminder reminder = items.get(from_position);
        //이동할 객체 삭제
        items.remove(from_position);
        //이동하고 싶은 position에 추가
//        items.add(to_position, person);
        items.add(to_position, reminder);
        //Adapter에 데이터 이동알림
        notifyItemMoved(from_position, to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        items.remove(position);
        notifyItemRemoved(position);

    }

    //왼쪽 버튼 누르면 수정할 다이얼로그 띄우기
    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        //수정 버튼 클릭시 다이얼로그 생성
        CustomDialog dialog = new CustomDialog(context, position, items.get(position));
        //화면 사이즈 구하기
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //다이얼로그 사이즈 세팅
        WindowManager.LayoutParams wm = dialog.getWindow().getAttributes();
        wm.copyFrom(dialog.getWindow().getAttributes());
        wm.width = (int) (width * 0.7);
        wm.height = height / 4;
        //다이얼로그 Listener 세팅
        dialog.setDialogListener(this);
        //다이얼로그 띄우기
        dialog.show();
    }

    //오른쪽 버튼 누르면 아이템 삭제
    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        items.remove(position);
        notifyItemRemoved(position);

//        int gc = getItemCount();
//        Log.d("getItemCount 뷰 순서 값", String.valueOf(gc));

        //리사이클 뷰 순서 값 확인하기
//        items.get(0);//null
//        Log.d(" items.get(0) 값", String.valueOf( items.get(0)));
       int vn = viewHolder.getPosition()+1;
//        int rnum = rv.getAdapter().getItemCount();
//        Log.d("mainActivity의 뷰 순서 값", String.valueOf(rnum));
        Log.d("ListAdapter의 뷰 순서 값", String.valueOf(vn));

        //DB에서 가져올 데이터 담을 리스트 생성
        List<Reminder> reminderList = new ArrayList<>(  );
        Log.d("ListAdapter의 리스트에 담긴 값", String.valueOf(reminderList));

        //DB 등록을 위한 객체 생성
        final DbHelper dbHelper = new DbHelper(context);
        //DB에서 오늘의 알림 삭제
        sqlDB = dbHelper.getWritableDatabase();
        sqlDB.execSQL("DELETE FROM remind WHERE no = " + vn + " ");
        sqlDB.close();
    }

    @Override
    public void onFinish(int position, Reminder reminder) {
        items.set(position, reminder);
        notifyItemChanged(position);

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView list_content, list_time;

        public ItemViewHolder(View itemView) {
            super(itemView);
            list_content = itemView.findViewById(R.id.list_content);
            list_time = itemView.findViewById(R.id.list_time);
        }

        public void onBind(Reminder reminder) {
            list_content.setText(reminder.getContent());
            list_time.setText(reminder.getTime());
        }
    }
}