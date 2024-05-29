package com.school.bus_autosystem.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BusStopDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bus_stops.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bus_stops";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STOP_NUMBER = "stop_number";
    public static final String COLUMN_STOP_NAME = "stop_name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_INFO_DATE = "info_date";
    public static final String COLUMN_SHORT_NUMBER = "short_number";
    public static final String COLUMN_CITY_CODE = "city_code";
    public static final String COLUMN_CITY_NAME = "city_name";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STOP_NUMBER + " TEXT, " +
                    COLUMN_STOP_NAME + " TEXT, " +
                    COLUMN_LATITUDE + " REAL, " +
                    COLUMN_LONGITUDE + " REAL, " +
                    COLUMN_INFO_DATE + " TEXT, " +
                    COLUMN_SHORT_NUMBER + " TEXT, " +
                    COLUMN_CITY_CODE + " INTEGER, " +
                    COLUMN_CITY_NAME + " TEXT);";

    public BusStopDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
