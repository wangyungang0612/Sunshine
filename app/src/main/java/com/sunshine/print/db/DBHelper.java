package com.sunshine.print.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.sunshine.print.CallbackBundle;
import com.sunshine.print.DatePlanActivity;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "SunShine.db"; // DB name
	private Context mcontext;
	private DBHelper mDbHelper;
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 3);
		this.mcontext = context;
	}

	public DBHelper(Context context, String name, CursorFactory factory,
					int version) {

		super(context, name, factory, version);
		this.mcontext = context;

	}

	/**
	 * 用户第一次使用软件时调用的操作，用于获取数据库创建语句（SW）,然后创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//,clothes text,doctor text,laiwang text,baby text,live text,other text,remark text
		//String sql = "create table if not exists family_bill(id integer primary key,time text,food text,use text,traffic text,travel text)";
		String sql = "create table if not exists dateplan2(id integer primary key,sn text,pass text,mac text," +
				"pno text,enc text,date text,des text,key text,print text,type text,version text)";
		db.execSQL(sql);
		String sql2 = "create table if not exists type(id integer primary key,type text)";
		db.execSQL(sql2);
		String sql3 = "create table if not exists version(id integer primary key,version text)";
		db.execSQL(sql3);
		String sql4 = "create table if not exists num(id integer primary key,num text,printnum text)";
		db.execSQL(sql4);
		Toast.makeText(mcontext,"创建成功",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists dateplan2");
		db.execSQL("drop table if exists type");
		db.execSQL("drop table if exists version");
		db.execSQL("drop table if exists num");
		onCreate(db);
	}

	/* 打开数据库,如果已经打开就使用，否则创建 */
	public DBHelper open() {
		if (null == mDbHelper) {
		//	mDbHelper = new DBHelper(mcontext);
			mDbHelper = new DBHelper(mcontext,DB_NAME,null,3);
		}
		db = mDbHelper.getWritableDatabase();
		return this;
	}

	/* 关闭数据库 */
	public void close() {
		db.close();
		mDbHelper.close();
	}

	/** 添加数据 */
	public long insert(String tableName, ContentValues values) {
		return db.insert(tableName, null, values);
	}

	/** 查询数据 */
	public Cursor findList(String tableName, String[] columns,
						   String selection, String[] selectionArgs, String groupBy,
						   String having, String orderBy, String limit) {
		return db.query(tableName, columns, selection, selectionArgs, groupBy,
				having, orderBy, limit);
	}

	public Cursor exeSql(String sql) {
		return db.rawQuery(sql, null);
	}

}