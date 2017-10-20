package com.example.vahan.simplecursonadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

class DB {

    private static final String DB_NAME = "ahfvfthsnjiufghfhfdjkjjjjhnjjjfhdmhgfhhnhhhjjhfj";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "mytab";
    private static final String COLUMN_ID = "_id";
    static final String COLUMN_IMG = "img";
    static final String COLUMN_IMG1 = "img1";
    static final String COLUMN_TXT = "name";
    static final String COLUMN_DATE = "date";
    private static final String COLUMN_PASSWORD = "password";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_IMG + " integer, " +
                    COLUMN_IMG1 + " integer, " +
                    COLUMN_TXT + " name," +
                    COLUMN_DATE + " date" +
                    ");";

    private static final String DB_CREATE_P =
            "create table " + "tabl" + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PASSWORD + " pas" +
                    ");";

    private static final String DB_CREATE_IMG =
            "create table " + "tab" + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_IMG + " img" +
                    ");";

    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    DB(Context ctx) {
        mCtx = ctx;
    }

    void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    Cursor getSearchedData(String filter) {
       return mDB.query(true, DB_TABLE, new String[] { COLUMN_ID,
                        COLUMN_TXT }, COLUMN_TXT + " LIKE ?",
                new String[] {"%"+ filter+ "%" }, null, null, null,
                null);
    }

    Cursor getAllData() {

        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    Cursor getData() {
        String sqlQuery = "Select * From mytab Order By _id Desc";
        return mDB.rawQuery(sqlQuery, null);
    }

    Cursor getPassData() {
        return mDB.query("tabl",null,null,null,null,null,null);
    }

    void addRec(String txt,String date, int img,int img1) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_IMG, img);
        cv.put(COLUMN_IMG1, img1);
        mDB.insert(DB_TABLE, null, cv);
    }

    void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    void delAll(){
        mDB.delete(DB_TABLE,null,null);
    }

    void editPas(String pas,String id){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASSWORD,pas);
        cv.put(COLUMN_ID,id);
        mDB.update("tabl",cv,COLUMN_ID + " = ?",new String[]{"1"});
    }

    void editRec(String txt, String date,String id,int img,int img1){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT, txt);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_IMG, img);
        cv.put(COLUMN_IMG1, img1);
        mDB.update(DB_TABLE, cv, COLUMN_ID + " = ?" , new String[]{id});
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DB_CREATE);
            db.execSQL(DB_CREATE_P);
            db.execSQL(DB_CREATE_IMG);

            ContentValues cv1 = new ContentValues();
            cv1.put(COLUMN_PASSWORD,"");
            cv1.put(COLUMN_ID,"1");
            db.insert("tabl",null,cv1);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}