package com.school.bus_autosystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.school.bus_autosystem.ResponseClass.BusStop;
import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;
import com.school.bus_autosystem.SQLite.BusStopDatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ReservationActivity extends AppCompatActivity {
    private String busNumber;
    private String busStopName;
    private String busStopid;
    TextView mainbusstop;
    TextView mainbusstop_number;
    MySingleton mySingleton = MySingleton.getInstance();
    BusStopDatabaseHelper dbHelper;

    TextView busNumbertextView;
    TextView arriveTime;
    private MyHTTPD server;

    private List<BusstopArriveResponse.Response.Body.Items.Item> bus;
    private int currentBusIndex = 0;
    private List<BusstopArriveResponse.Response.Body.Items.Item> filteredBusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        dbHelper = new BusStopDatabaseHelper(this);

        busNumber = getIntent().getStringExtra("firstBusStopNumber");
        busStopName = getIntent().getStringExtra("firstBusStopName");
        busStopid = getIntent().getStringExtra("firstBusStopId");
        Log.d("nodeid", busNumber);

        mainbusstop = findViewById(R.id.MainBusStopName);
        mainbusstop_number = findViewById(R.id.mainbusstp_number);

        mainbusstop.setText(busStopName);
        mainbusstop_number.setText(busNumber);

        bus = mySingleton.getBusStopListSingleton();


        // busStopid와 일치하는 항목만 필터링
        filterBusListByStopId(busStopid);

        if (!filteredBusList.isEmpty()) {
            updateBusStopInfo(currentBusIndex);
        } else {
            new AlertDialog.Builder(ReservationActivity.this)
                    .setTitle("알림")
                    .setMessage("저상버스가 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }

        // HTTP 서버 초기화 및 시작
        try {
            server = new MyHTTPD(8080);
            server.start();
            Log.d("HTTPD", "서버가 시작되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // DB에서 busStopid로 정류장 이름 검색 및 HTTP GET 요청
        BusStop busStop = dbHelper.getBusStopByNodeId(busStopid);
        if (busStop != null) {
            String stopName = busStop.getStopName();
            Log.d("ServerThreadReservation", "busStop : " + stopName);
//            new HttpGetRequest(this).execute("http://192.168.137.167:80/?stop=" + stopName);
            new HttpGetRequest(this).execute("http://192.168.137.167:80/?stop=" + stopName);
        } else {
            Log.d("ReservationActivity", "정류장을 찾을 수 없습니다.");
        }
    }

    private void filterBusListByStopId(String busStopid) {
        for (BusstopArriveResponse.Response.Body.Items.Item item : bus) {

            if (item.routeno.equals(busNumber) && item.vehicletp.equals("저상버스"))
//                ("저상버스".equals(item.vehicletp) && uniqueRoutes.add(item.routeid))
            {
                filteredBusList.add(item);
                Log.d("nodeid", "nodeid : " + item.nodeid);
                Log.d("nodeid", "busStopid : " + item.nodenm);
            }
        }
    }

    private void updateBusStopInfo(int index) {
        if (index >= 0 && index < filteredBusList.size()) {
            BusstopArriveResponse.Response.Body.Items.Item currentItem = filteredBusList.get(index);
            busNumbertextView = findViewById(R.id.near_busstop_text);
            busNumbertextView.setText(currentItem.routeno);

            arriveTime = findViewById(R.id.location_busstop_text);
            int totalSeconds = currentItem.arrtime;
            int minutes = totalSeconds / 60;
            arriveTime.setText(String.format("%d분남음", minutes));
        } else {
            new AlertDialog.Builder(ReservationActivity.this)
                    .setTitle("알림")
                    .setMessage("저상버스가 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
            Log.d("HTTPD", "서버가 종료되었습니다.");
        }
    }

    private class HttpGetRequest extends AsyncTask<String, Void, String> {
        private Context context;

        public HttpGetRequest(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        Log.d("HTTP", "GET 요청: " + line);
                    }
                    reader.close();
                } else {
                    Log.d("HTTP", "GET 요청 실패: 응답 코드 " + responseCode);
                }
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("HTTP", "응답: " + result);

            if ("true".equalsIgnoreCase(result)) {
                new AlertDialog.Builder(ReservationActivity.this)
                        .setTitle("알림")
                        .setMessage("편안한 탑승 되세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();  // 현재 액티비티 종료
                            }
                        })
                        .show();
            } else if ("false".equalsIgnoreCase(result)) {
                new AlertDialog.Builder(ReservationActivity.this)
                        .setTitle("알림")
                        .setMessage("다음 버스를 이용해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentBusIndex++;
                                if (currentBusIndex < filteredBusList.size()) {
                                    updateBusStopInfo(currentBusIndex);
                                } else {
                                    new AlertDialog.Builder(ReservationActivity.this)
                                            .setTitle("알림")
                                            .setMessage("더 이상 버스가 없습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .show();
                                }
                            }
                        })
                        .show();
            }
        }
    }

    public class MyHTTPD extends NanoHTTPD {

        public MyHTTPD(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String clientIp = session.getRemoteIpAddress();
            String uri = session.getUri();
            Map<String, String> params = session.getParms();

            // 로그로 요청 정보를 출력
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Log.d("HTTPD", "매개변수: " + entry.getKey() + " = " + entry.getValue());
            }
            for (Map.Entry<String, String> header : session.getHeaders().entrySet()) {
                Log.d("HTTPD", "헤더: " + header.getKey() + " = " + header.getValue());
            }
            Log.d("HTTPD", "클라이언트 IP: " + clientIp + ", URI: " + uri);
            Log.d("HTTPD", "요청 수신됨.");

            // 기본 응답 메시지 설정
            String response = "thank you";

            // 요청 파라미터에 "status"가 포함되어 있는 경우 응답 메시지 설정 및 다이얼로그 표시
            if (params.containsKey("status")) {
                String status = params.get("status");
                String stop = params.get("stop");

                // status 값이 true인지 false인지 검사
                if ("true".equalsIgnoreCase(status)) {
                    response = "true";
                    Log.d("HTTPD", "정류장: " + stop + " 상태: 활성화됨");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ReservationActivity.this)
                                    .setTitle("알림")
                                    .setMessage("편안한 탑승 되세요")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();  // 현재 액티비티 종료
                                        }
                                    })
                                    .show();
                        }
                    });
                } else if ("false".equalsIgnoreCase(status)) {
                    response = "false";
                    Log.d("HTTPD", "정류장: " + stop + " 상태: 비활성화됨");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ReservationActivity.this)
                                    .setTitle("알림")
                                    .setMessage("다음 버스를 이용해주세요")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            currentBusIndex++;
                                            if (currentBusIndex < filteredBusList.size()) {
                                                updateBusStopInfo(currentBusIndex);
                                            } else {
                                                new AlertDialog.Builder(ReservationActivity.this)
                                                        .setTitle("알림")
                                                        .setMessage("더 이상 버스가 없습니다.")
                                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .show();
                                            }
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    response = "unknown";
                    Log.d("HTTPD", "정류장: " + stop + " 상태: 알 수 없음");
                }
            }

            // 응답 반환
            return newFixedLengthResponse(response);
        }
    }
}

