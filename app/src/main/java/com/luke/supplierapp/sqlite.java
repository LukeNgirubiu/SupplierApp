package com.luke.supplierapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sqlite extends SQLiteOpenHelper {
    public static final String Dbname = "Users";
    public static final int Dbversion = 1;
    public static final String LOCALTB = "mytable";
    public static final String THIRDCOLUMN= "firestoreid";
    public static final String FIRSTCOLUMN= "myind";
    public static final String CREATETABLE="CREATE TABLE mytable(myind INTEGER PRIMARY KEY,firestoreid TEXT NOT NULL)";
    public sqlite(Context context) {
        super(context, Dbname, null, Dbversion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATETABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+LOCALTB);
        onCreate(db);
    }

    public int CountRows() {
        String query = "SELECT*FROM "+LOCALTB;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();
    }

    public long addId(String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(THIRDCOLUMN,userid);
        return db.insert(LOCALTB,null,values);
    }

    public String getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,LOCALTB, new String[]{
                        FIRSTCOLUMN, THIRDCOLUMN}, FIRSTCOLUMN + "=?",
                new String[]{String.valueOf(1)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String helper=cursor.getString(1);
        // return contact
        return helper;
    }
}
