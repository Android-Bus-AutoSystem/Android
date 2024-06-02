package com.school.bus_autosystem;


import static kotlinx.coroutines.DelayKt.delay;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.school.bus_autosystem.ResponseClass.BusStationResponse;
import com.school.bus_autosystem.RetrofitClient.RetrofitClient;
import com.school.bus_autosystem.Retrofitinterface.BusStationApi;
import com.school.bus_autosystem.SQLite.BusStopDatabaseHelper;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private ProgressDialog progressDialog;
    private LinearLayout buttonLayout;
    private BusStopDatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private MySingleton mySingleton = MySingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        showLoadingDialog();
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

        if (!isDatabaseExists()) {
            new LoadDataTask().execute();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // 위치 업데이트 처리
                    Log.d("MainActivity", "현재 위치: " + location.getLatitude() + ", " + location.getLongitude());
                    double latitude = location.getLatitude();
                    String latitudeString = Double.toString(latitude);
                    mySingleton.setLatitude(latitudeString);
                    if (mySingleton.getSomeData().isEmpty() == true)
                    {
//                        Log.d("MainActivity", "here");
                    }
                    else
                    {
//                        Log.d("MainActivity", "here2");
                        getBusStations(location.getLatitude(), location.getLongitude());
                        fusedLocationClient.removeLocationUpdates(locationCallback);
//                        break;
                    }
//
                }

//
            }
        };
        requestLocationPermission();
//        getBusStations(35.1503, 129.0039033);
    }


    public void readExcel() {
        try {
            InputStream is = getAssets().open("busan_busstop_location_version2.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet != null) {
                saveDataToDatabase(sheet);
            }
        } catch (IOException e) {
            Log.d("Main", e.toString());
            e.printStackTrace();
        }
    }
    private void saveDataToDatabase(Sheet sheet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // 트랜잭션 시작
        try {
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();
            while (rowIterator.hasNext()) {
                String latitude = null;
                String longitude = null;
                Row row = rowIterator.next();

                String stopNumber = row.getCell(0).getStringCellValue();
                String stopName = row.getCell(1).getStringCellValue();
                switch (row.getCell(2).getCellType())
                {
                    case FORMULA:
                        latitude = row.getCell(2).getCellFormula();
                        break;
                    case STRING:
                        latitude =  row.getCell(2).getStringCellValue() + "";
                        break;
                    case NUMERIC:
                        latitude =  row.getCell(2).getNumericCellValue() + "";
                        break;
                    case BLANK:
                        latitude = row.getCell(2).getBooleanCellValue() + "";
                        break;
                    case ERROR:
                        latitude = row.getCell(2).getErrorCellValue() + "";
                        break;
                }
                switch (row.getCell(3).getCellType())
                {
                    case FORMULA:
                        longitude = row.getCell(3).getCellFormula();
                        break;
                    case STRING:
                        longitude =  row.getCell(3).getStringCellValue() + "";
                        break;
                    case NUMERIC:
                        longitude =  row.getCell(3).getNumericCellValue() + "";
                        break;
                    case BLANK:
                        longitude = row.getCell(3).getBooleanCellValue() + "";
                        break;
                    case ERROR:
                        longitude = row.getCell(3).getErrorCellValue() + "";
                        break;
                }
                String mobileNumber =  row.getCell(4).getNumericCellValue() + "";
                dbHelper.insertBusStop(stopNumber, stopName, Double.parseDouble(latitude), Double.parseDouble(longitude), mobileNumber);
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
//    private void saveDataToDatabase(Sheet sheet) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.beginTransaction(); // 트랜잭션 시작
//        try {
//            Iterator<Row> rowIterator = sheet.iterator();
//            rowIterator.next();
//            while (rowIterator.hasNext()) {
//
//                String latitude = null;
//                String longitude = null;
//                Row row = rowIterator.next();
//
//                String stopNumber = row.getCell(0).getStringCellValue();
//                String stopName = row.getCell(1).getStringCellValue();
//                switch (row.getCell(2).getCellType())
//                {
//                    case FORMULA:
//                        latitude = row.getCell(2).getCellFormula();
//                        break;
//                    case STRING:
//                        latitude =  row.getCell(2).getStringCellValue() + "";
//                        break;
//                    case NUMERIC:
//                        latitude =  row.getCell(2).getNumericCellValue() + "";
//                        break;
//                    case BLANK:
//                        latitude = row.getCell(2).getBooleanCellValue() + "";
//                        break;
//                    case ERROR:
//                        latitude = row.getCell(2).getErrorCellValue() + "";
//                        break;
//                }
//                switch (row.getCell(3).getCellType())
//                {
//                    case FORMULA:
//                        longitude = row.getCell(3).getCellFormula();
//                        break;
//                    case STRING:
//                        longitude =  row.getCell(3).getStringCellValue() + "";
//                        break;
//                    case NUMERIC:
//                        longitude =  row.getCell(3).getNumericCellValue() + "";
//                        break;
//                    case BLANK:
//                        longitude = row.getCell(3).getBooleanCellValue() + "";
//                        break;
//                    case ERROR:
//                        longitude = row.getCell(3).getErrorCellValue() + "";
//                        break;
//                }
//                String mobileNumber =  row.getCell(4).getNumericCellValue() + "";
//                // INSERT 쿼리 실행
//                String insertQuery = "INSERT INTO " + BusStopDatabaseHelper.TABLE_NAME + " (" +
//                        BusStopDatabaseHelper.COLUMN_STOP_NUMBER + ", " +
//                        BusStopDatabaseHelper.COLUMN_STOP_NAME + ", " +
//                        BusStopDatabaseHelper.COLUMN_LATITUDE + ", " +
//                        BusStopDatabaseHelper.COLUMN_LONGITUDE + ", " +
//                        BusStopDatabaseHelper.COLUMN_MOBILE_NUMBER +
//                        ") VALUES (?, ?, ?, ?, ?)";
//
//                db.execSQL(insertQuery, new String[]{stopNumber, stopName, latitude, longitude, mobileNumber});
//            }
//
//            db.setTransactionSuccessful(); // 트랜잭션 성공적으로 완료
//        } catch (Exception e) {
//            Log.d("Main", "Error: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            db.endTransaction(); // 트랜잭션 종료
//            db.close();
//        }
//    }

    public void getBusStations(double gpsLati, double gpsLong) {
        BusStationApi api = RetrofitClient.getClient().create(BusStationApi.class);
        Call<BusStationResponse> call = api.getBusStations("ZEMlvlhLRj4MWmL4i3tWe/YeTMmm4poPtCz03TK7+kCWv5hukZpl5EVWN6UWFuk3fizuY85oCYcTl5V9f7GfrA==", 1, 100, "json", gpsLati, gpsLong);

        call.enqueue(new Callback<BusStationResponse>() {
            @Override
            public void onResponse(Call<BusStationResponse> call, Response<BusStationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 호출 성공, 데이터를 처리
                    BusStationResponse busStationResponse = response.body();
//                    Log.d("MainActivity", "Bus Stations: " + busStationResponse.getResponse().getBody().getItems().toString());

                    List<BusStationResponse.Item> busstop = busStationResponse.getResponse().getBody().getItems().getItem();
//                    for (BusStationResponse.Item item : busstop) {
//                        Log.d("MainActivity", "Bus Stop Name: " + item.getNodenm());
//                    }
                    BusStationResponse.Item firstbusstop = busstop.get(0);
//                    Log.d("MainActivity", "Bus Stop Name: " + firstbusstop.getNodenm());
                    TextView busstop_textView = (TextView) findViewById(R.id.location_busstop_text);
                    busstop_textView.setText(firstbusstop.getNodenm());
                    mySingleton.setFirstBusStop(firstbusstop);
//                    dismissLoadingDialog();
                } else {
                    // 응답이 성공적이지 않음
                    Log.e("MainActivity", "API 호출 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BusStationResponse> call, Throwable t) {
                // 네트워크 또는 기타 오류
                Log.e("MainActivity", "API 호출 실패", t);
            }
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(500); // 10초마다 업데이트
//        locationRequest.setFastestInterval(5000); // 가장 빠른 업데이트 간격 5초
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setNumUpdates(1); // 위치 업데이트를 한 번만 받도록 설정

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
//    private void showLoadingDialog() {
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("데이터를 로드하는 중입니다...");
//            progressDialog.setCancelable(false);
//        }
//        progressDialog.show();
//    }

//    private void dismissLoadingDialog() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }

    private boolean isDatabaseExists() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Log.e("checkcode", "Error1");
        db.close();
        SQLiteDatabase checkDB = null;
//        Log.e("checkcode", "Error2");
        try {
            String path = getDatabasePath("bus_stops.db").getPath();
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
//            Log.e("checkcode", "Error3");
        } catch (SQLiteException e) {
            // 데이터베이스가 존재하지 않는 경우
            Log.e("Main", "Error checking database: " + e.getMessage());
        }
        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB == null;
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

}
