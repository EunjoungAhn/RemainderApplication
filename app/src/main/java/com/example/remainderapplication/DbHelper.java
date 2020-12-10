package com.example.remainderapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.example.remainderapplication/databases";

    //코드에서 만들어야 제대로 만들어짐.

    public DbHelper(Context context){
        super(context, "remindb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //table을 만들때!
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //table을 삭제할 때!
    }
}