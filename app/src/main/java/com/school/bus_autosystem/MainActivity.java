package com.school.bus_autosystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.school.bus_autosystem.SQLite.BusStopDatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private LinearLayout buttonLayout;
    private BusStopDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonLayout = findViewById(R.id.button_layout);
        buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dbHelper = new BusStopDatabaseHelper(this);

        // 데이터베이스 파일이 존재하는지 확인
        if (!isDatabaseExists()) {
            // 데이터베이스 파일이 존재하지 않으면 엑셀 파일 읽기
//            readExcel();
            new LoadDataTask().execute();
        }
    }
    private boolean isDatabaseExists() {
        SQLiteDatabase checkDB = null;
        try {
            String path = getDatabasePath("bus_stops.db").getPath();
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            Log.e("Main", "Error checking database: " + e.getMessage());
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("데이터를 로드하는 중입니다. \n잠시만 기다려주세요...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            readExcel();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
    public void readExcel() {
        try {
            InputStream is = getAssets().open("busan_busstop_location.xls"); // 엑셀파일
            Workbook wb = Workbook.getWorkbook(is); // 엑셀 파일이 있다면
            if (wb != null) {
                Sheet sheet = wb.getSheet(0); // 시트 불러오기
                if (sheet != null) {
                    saveDataToDatabase(sheet);
                }
            }
        } catch (IOException | BiffException e) {
            Log.d("Main", e.toString());
            e.printStackTrace();
        }
    }

    private void saveDataToDatabase(Sheet sheet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // 트랜잭션 시작

        try {
            int rowIndexStart = 1; // 데이터가 시작되는 행 인덱스 (헤더가 0번 행에 있다고 가정)
            int rowTotal = sheet.getRows(); // 전체 행 수

            for (int row = rowIndexStart; row < rowTotal; row++) {
                String stopNumber = sheet.getCell(0, row).getContents();
                String stopName = sheet.getCell(1, row).getContents();
                double latitude = Double.parseDouble(sheet.getCell(2, row).getContents());
                double longitude = Double.parseDouble(sheet.getCell(3, row).getContents());
                String infoDate = sheet.getCell(4, row).getContents();
                String shortNumber = sheet.getCell(5, row).getContents();
                int cityCode = Integer.parseInt(sheet.getCell(6, row).getContents());
                String cityName = sheet.getCell(7, row).getContents();

                db.execSQL("INSERT INTO " + BusStopDatabaseHelper.TABLE_NAME + " (" +
                        BusStopDatabaseHelper.COLUMN_STOP_NUMBER + ", " +
                        BusStopDatabaseHelper.COLUMN_STOP_NAME + ", " +
                        BusStopDatabaseHelper.COLUMN_LATITUDE + ", " +
                        BusStopDatabaseHelper.COLUMN_LONGITUDE + ", " +
                        BusStopDatabaseHelper.COLUMN_INFO_DATE + ", " +
                        BusStopDatabaseHelper.COLUMN_SHORT_NUMBER + ", " +
                        BusStopDatabaseHelper.COLUMN_CITY_CODE + ", " +
                        BusStopDatabaseHelper.COLUMN_CITY_NAME +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                        stopNumber, stopName, latitude, longitude, infoDate, shortNumber, cityCode, cityName
                });

                Log.d("Main", "Inserted row: " + row);
            }
            db.setTransactionSuccessful(); // 트랜잭션 성공적으로 완료
        } catch (Exception e) {
            Log.d("Main", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 트랜잭션 종료
            db.close();
        }
    }
}
