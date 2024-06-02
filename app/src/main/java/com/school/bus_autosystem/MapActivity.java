package com.school.bus_autosystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.school.bus_autosystem.ResponseClass.BusStationResponse;
import com.school.bus_autosystem.ResponseClass.BusStop;
import com.school.bus_autosystem.SQLite.BusStopDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private BusStopDatabaseHelper dbHelper;
    private MySingleton mySingleton = MySingleton.getInstance();
    private List<Marker> markers = new ArrayList<>();
    private Marker selectedMarker;
    private LinearLayout infoLayout;
    private TextView busStopNameTextView;
    private Button viewDetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("Map Start");

        String NAVER_CLIENT_ID = BuildConfig.NAVER_CLIENT_ID;

        NaverMapSdk.getInstance(this).setClient(new NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID));
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);

        dbHelper = new BusStopDatabaseHelper(this);

        // 초기화
        infoLayout = findViewById(R.id.info_layout);
        busStopNameTextView = findViewById(R.id.bus_stop_name_text_view);
        viewDetailsButton = findViewById(R.id.view_details_button);

        viewDetailsButton.setOnClickListener(v -> {
            String busStopId = busStopNameTextView.getTag().toString();
            LatLng position = null;
            for (Marker marker : markers) {
                if (busStopId.equals(marker.getTag())) {
                    position = marker.getPosition();
                    break;
                }
            }
            if (position != null) {
                Intent intent = new Intent(MapActivity.this, BusStopDetailActivity.class);
                intent.putExtra("busStopId", busStopId);
                intent.putExtra("busStopName", busStopNameTextView.getText().toString());
                Log.d("busStopName" , busStopNameTextView.getText().toString());
                intent.putExtra("busStopLat", position.latitude);
                intent.putExtra("busStopLong", position.longitude);
                startActivity(intent);
            } else {
//                Toast.makeText(MapActivity.this, "Bus stop location not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        LatLng initialPosition = new LatLng(	35.151052243176, 	129.01226968041); // 서울 시청 좌표 (예시)
        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(initialPosition, 19).animate(CameraAnimation.Easing);
        naverMap.moveCamera(cameraUpdate);

        loadMarkers(naverMap.getContentBounds());

        naverMap.addOnCameraChangeListener((reason, animated) -> {
            loadMarkers(naverMap.getContentBounds());
        });

        naverMap.setLocationSource(locationSource);

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);

//        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, f);

        moveToFirstBusStop();
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.setMap(null);
        }
        markers.clear();
    }




//    private void loadMarkers(LatLngBounds bounds) {
//        clearMarkers();
//
//        // 백그라운드 스레드에서 데이터베이스 쿼리 실행
//        new Thread(() -> {
//            List<BusStop> busStops = dbHelper.getBusStopsInBounds(bounds);
//
//            // UI 스레드에서 마커 추가
//            runOnUiThread(() -> {
//                if (busStops.isEmpty()) {
//                    return;
//                }
//                for (BusStop busStop : busStops) {
//                    Marker marker = new Marker();
//                    marker.setPosition(new LatLng(busStop.getLatitude(), busStop.getLongitude()));
//                    marker.setWidth(50);
//                    marker.setHeight(70);
//                    marker.setTag(busStop.getStopNumber()); // 버스 정류장 ID로 태그 설정
//                    marker.setMap(naverMap);
//                    markers.add(marker);
//
//                    marker.setOnClickListener(overlay -> {
//                        // 클릭된 마커의 크기 조정
//                        if (selectedMarker != null) {
//                            selectedMarker.setWidth(50);
//                            selectedMarker.setHeight(70);
//                        }
//                        marker.setWidth(150);
//                        marker.setHeight(150);
//                        selectedMarker = marker;
//                        busStopNameTextView.setText(busStop.getStopName()); // 버스 정류장 이름 설정
//                        busStopNameTextView.setTag(busStop.getStopNumber()); // 버스 정류장 ID를 태그로 설정
//                        infoLayout.setVisibility(View.VISIBLE);
//                        return true;
//                    });
//                }
//            });
//        }).start();
//    }


    private void loadMarkers(LatLngBounds bounds) {
        clearMarkers();
        List<BusStop> busStops = dbHelper.getBusStopsInBounds(bounds);
        if (busStops.isEmpty()) {
            return;
        }
        for (BusStop busStop : busStops) {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(busStop.getLatitude(), busStop.getLongitude()));
            marker.setWidth(50);
            marker.setHeight(70);
            marker.setTag(busStop.getStopNumber()); // 버스 정류장 ID로 태그 설정
            marker.setMap(naverMap);
            markers.add(marker);

            marker.setOnClickListener(overlay -> {
                // 클릭된 마커의 크기 조정
                if (selectedMarker != null) {
                    selectedMarker.setWidth(50);
                    selectedMarker.setHeight(70);
                }
                marker.setWidth(150);
                marker.setHeight(150);
                selectedMarker = marker;
                busStopNameTextView.setText(busStop.getStopName()); // 버스 정류장 이름 설정
                busStopNameTextView.setTag(busStop.getStopNumber()); // 버스 정류장 ID를 태그로 설정
                infoLayout.setVisibility(View.VISIBLE);
                return true;
            });
        }
    }

    public void moveToFirstBusStop() {
        BusStationResponse.Item firstBusStop = mySingleton.getFirstBusStop();
        if (firstBusStop != null) {
            String nodeId = firstBusStop.getNodeid();
            List<BusStop> allBusStops = dbHelper.getAllBusStops();
            for (BusStop busStop : allBusStops) {
                if (busStop.getStopNumber().equals(nodeId)) {
                    LatLng busStopLocation = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(busStopLocation, 19); // 줌 레벨 18로 설정
                    naverMap.moveCamera(cameraUpdate);

//                    // 기존 마커 제거4
                    // 선택된 마커의 크기 변경
//                    if (selectedMarker != null) {
//                        selectedMarker.setWidth(50);
//                        selectedMarker.setHeight(70);
//                    }
                    for (Marker marker : markers) {
                        if (marker.getTag().equals(nodeId)) {
                            marker.setWidth(50);
                            marker.setHeight(70);
                            selectedMarker = marker;
                            break;
                        }
                    }


                    busStopNameTextView.setText(busStop.getStopName()); // 버스 정류장 이름 설정
                    busStopNameTextView.setTag(busStop.getStopNumber()); // 버스 정류장 ID를 태그로 설정
                    infoLayout.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } else {
//            Toast.makeText(this, "firstBusStop 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {

            if (!locationSource.isActivated()) {
//                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMarkers();
    }
}
