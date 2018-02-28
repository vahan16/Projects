package com.example.vahan.maps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

class DB {

    private static final String DB_NAME = "gps1hjbej";
    private static final int DB_VERSION = 1;
    private static final String DB_MUSEUM = "Museum";
    private static final String DB_THEATRE = "Theatres";
    private static final String DB_CAFETERIA = "Cafeterias";
    private static final String DB_CINEMA = "Cinemas";
    private static final String DB_PHARMACY = "Pharmacies";

    private static final String COLUMN_ID = "_id";

    static final String COLUMN_LAT = "Latitude";
    static final String COLUMN_LONG = "Longitude";
    static final String COLUMN_NAME = "Name";

    private static final String DB_CREATE_M =
            "create table " + DB_MUSEUM + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LAT + " REAL," +
                    COLUMN_LONG + " REAL," +
                    COLUMN_NAME + " Name" +
                    ");";

    private static final String DB_CREATE_CA =
            "create table " + DB_CAFETERIA + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LAT + " REAL," +
                    COLUMN_LONG + " REAL," +
                    COLUMN_NAME + " Name" +
                    ");";

    private static final String DB_CREATE_CI =
            "create table " + DB_CINEMA + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LAT + " REAL," +
                    COLUMN_LONG + " REAL," +
                    COLUMN_NAME + " Name" +
                    ");";

    private static final String DB_CREATE_P =
            "create table " + DB_PHARMACY + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LAT + " REAL," +
                    COLUMN_LONG + " REAL," +
                    COLUMN_NAME + " Name" +
                    ");";


    private static final String DB_CREATE_T =
            "create table " + DB_THEATRE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_LAT + " REAL," +
                    COLUMN_LONG + " REAL," +
                    COLUMN_NAME + " Name" +
                    ");";

    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    DB(Context ctx) {
        mCtx = ctx;
    }

    Cursor SelectFrom(String table){
        return mDB.rawQuery("select * from " + table, null);
    }

    Cursor getMusData() {
        return mDB.query(DB_MUSEUM, null, null, null, null, null, null);
    }

    Cursor getTheatreData() {
        return mDB.query(DB_THEATRE, null, null, null, null, null, null);
    }

    Cursor getCafeData() {
        return mDB.query(DB_CAFETERIA, null, null, null, null, null, null);
    }

    Cursor getPharmData() {
        return mDB.query(DB_PHARMACY, null, null, null, null, null, null);
    }

    Cursor getCinemaData() {
        return mDB.query(DB_CINEMA, null, null, null, null, null, null);
    }


    void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    void addMus(double latitudeValue,double longitudeValue,String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, latitudeValue);
        cv.put(COLUMN_LONG, longitudeValue);
        cv.put(COLUMN_NAME, name);
        mDB.insert(DB_MUSEUM, null, cv);
    }

    void addCafe(double latitudeValue,double longitudeValue,String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, latitudeValue);
        cv.put(COLUMN_LONG, longitudeValue);
        cv.put(COLUMN_NAME, name);
        mDB.insert(DB_CAFETERIA, null, cv);
    }

    void addTheatre(double latitudeValue,double longitudeValue,String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, latitudeValue);
        cv.put(COLUMN_LONG, longitudeValue);
        cv.put(COLUMN_NAME, name);
        mDB.insert(DB_THEATRE, null, cv);
    }

    void addCinema(double latitudeValue,double longitudeValue,String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, latitudeValue);
        cv.put(COLUMN_LONG, longitudeValue);
        cv.put(COLUMN_NAME, name);
        mDB.insert(DB_CINEMA, null, cv);
    }

    void addPharmacy(double latitudeValue,double longitudeValue,String name) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAT, latitudeValue);
        cv.put(COLUMN_LONG, longitudeValue);
        cv.put(COLUMN_NAME, name);
        mDB.insert(DB_PHARMACY, null, cv);
    }


    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DB_CREATE_T);
            db.execSQL(DB_CREATE_CA);
            db.execSQL(DB_CREATE_CI);
            db.execSQL(DB_CREATE_P);
            db.execSQL(DB_CREATE_M);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
