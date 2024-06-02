package com.school.bus_autosystem.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.naver.maps.geometry.LatLngBounds;
import com.school.bus_autosystem.ResponseClass.BusStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusStopDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bus_stops.db";
    private static final int DATABASE_VERSION = 1;

    // 테이블 및 열 이름
    public static final String TABLE_NAME = "bus_stops";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STOP_NUMBER = "stop_number";
    public static final String COLUMN_STOP_NAME = "stop_name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_MOBILE_NUMBER = "mobile_number";

    // 생성 쿼리
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_STOP_NUMBER + " TEXT, " +
            COLUMN_STOP_NAME + " TEXT, " +
            COLUMN_LATITUDE + " REAL, " +
            COLUMN_LONGITUDE + " REAL, " +
            COLUMN_MOBILE_NUMBER + " TEXT)";


    public BusStopDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 이전 버전의 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 새로운 버전의 테이블 생성
        onCreate(db);
    }

    public void insertBusStop(String stopNumber, String stopName, double latitude, double longitude, String mobileNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOP_NUMBER, stopNumber);
        values.put(COLUMN_STOP_NAME, stopName);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_MOBILE_NUMBER, mobileNumber);

        long newRowId = db.insert(TABLE_NAME, null, values);
//        Log.d("BusStopDatabaseHelper", "New bus stop inserted with ID: " + newRowId);
    }

    public List<BusStop> getAllBusStops() {
        List<BusStop> busStops = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                BusStop busStop = new BusStop(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOP_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOP_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE_NUMBER))
                );
                busStops.add(busStop);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
//        Log.d("BusStopDatabaseHelper", "Total bus stops: " + busStops.size());
        for (BusStop stop : busStops) {
//            Log.d("BusStopDatabaseHelper", "Bus Stop: " + stop.getStopName() + ", Lat: " + stop.getLatitude() + ", Long: " + stop.getLongitude());
        }

        return busStops;
    }
    public BusStop getBusStopByNodeId(String nodeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{
                        COLUMN_STOP_NUMBER,
                        COLUMN_STOP_NAME,
                        COLUMN_LATITUDE,
                        COLUMN_LONGITUDE,
                        COLUMN_MOBILE_NUMBER},
                COLUMN_STOP_NUMBER + "=?", new String[]{nodeId}, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                BusStop busStop = new BusStop(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOP_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOP_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE_NUMBER))
                );
                cursor.close();
                return busStop;
            }
            cursor.close();
        }
        return null;
    }

    public List<BusStop> getBusStopsInBounds(LatLngBounds bounds) {
        List<BusStop> busStops = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_LATITUDE + " >= ? AND " + COLUMN_LATITUDE + " <= ? AND " +
                COLUMN_LONGITUDE + " >= ? AND " + COLUMN_LONGITUDE + " <= ?";

        String[] selectionArgs = {
                String.valueOf(bounds.getSouthWest().latitude),
                String.valueOf(bounds.getNorthEast().latitude),
                String.valueOf(bounds.getSouthWest().longitude),
                String.valueOf(bounds.getNorthEast().longitude)
        };

//        Log.d("BusStopDatabaseHelper", "Query: " + query);
//        Log.d("BusStopDatabaseHelper", "Selection Args: " + Arrays.toString(selectionArgs));

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                BusStop busStop = new BusStop(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOP_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOP_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE_NUMBER))
                );
                busStops.add(busStop);
            } while (cursor.moveToNext());
        } else {
            // 쿼리 결과가 없을 때 처리
//            Log.d("BusStopDatabaseHelper", "No bus stops found in bounds");
        }

        cursor.close();
        db.close();

        return busStops;
    }



}
