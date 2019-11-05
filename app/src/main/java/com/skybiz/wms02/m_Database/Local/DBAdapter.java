package com.skybiz.wms02.m_Database.Local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by 7 on 30/10/2017.
 */

public class DBAdapter {
    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper = new DBHelper(c);
    }

    //OPEN DATABASE
    public DBAdapter openDB() {
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return this;
    }

    //CLOSE DATABASE
    public void closeDB() {
        try {
            if(db.isOpen())
                helper.close();
            //helper.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public long exeQuery(String query){
        try {
            db.execSQL(query);
            //return db.insert(Constants.TB_COMPANYSETUP, Constants.RunNo, cv);
            return 1;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public Cursor getQuery(String Query) {
        /*Cursor mCursor = db.rawQuery(Query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;*/
        return db.rawQuery(Query, null);
    }

    public boolean isColumnExists(String table, String column){
        //Cursor cursor=db.rawQuery("select "+column+" from "+table, null);
        Cursor cursor=db.rawQuery("PRAGMA table_info("+table+")", null);
        if(cursor != null){
            while(cursor.moveToNext()) {
                //String name = cursor.getColumnName(0);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if(column.equalsIgnoreCase(name)){
                    return true;
                }
            }
           // cursor.close();
        }
        return false;
    }

    public long addQuery(String Table, ContentValues cv) {
        try {
            return db.insert(Table, "RunNo", cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
