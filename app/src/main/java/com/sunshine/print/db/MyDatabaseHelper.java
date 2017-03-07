package com.sunshine.print.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_DATEPLAN = "create table DatePlan("
            +"id integer primary key autoincrement, "
            +"sn text, "
            +"pass text, "
            +"mac text, "
            +"pno text, "
            +"enc text, "
            +"date text, "
            +"des text, "
            +"key text)";

    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATEPLAN);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists DatePlan");
        onCreate(db);
    }
}
