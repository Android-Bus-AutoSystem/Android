package com.school.bus_autosystem;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;


public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private InfoWindow infoWindow;

    Marker busStopMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("Map Start");


        String NAVER_CLIENT_ID = BuildConfig.NAVER_CLIENT_ID;


        NaverMapSdk.getInstance(this).setClient(new NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID));
        //인터페이스 역할을 하는 NaverMap객체가 필요
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        //위치 추가
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);

        //자기 자신 위치 바로 설정
//        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // 마커 초기화
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true); // 기본 파란색 마커로 표시





        // Initialize InfoWindow
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return (String) infoWindow.getMarker().getTag(); // Display the tag as info window text
            }
        });

        addBusStopMarkers(naverMap);


        // 버스 정류장 보이게 하기
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);

    }

    private void addBusStopMarkers(NaverMap naverMap) {
        // Example bus stop coordinates and names
        LatLng[] busStopLocations = {
                new LatLng(35.17623995, 129.04665672),  // Example coordinates
                new LatLng(37.5651, 126.9895)
        };
        String[] busStopNames = {
                "Bus Stop 1",
                "Bus Stop 2"
        };

        for (int i = 0; i < busStopLocations.length; i++) {
            Marker marker = new Marker();
            marker.setPosition(busStopLocations[i]);
            marker.setMap(naverMap);
            marker.setTag(busStopNames[i]);  // Set tag to bus stop name

            // Set click listener for the marker
            marker.setOnClickListener(overlay -> {
                infoWindow.open(marker);
                return true;
            });
        }
    }

    // 위치 업데이트 시 호출될 콜백 함수 내부
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {

            if (!locationSource.isActivated()) { // 권한 거부됨
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);

            }
            else {
                // 권한이 승인되었을 때 현재 위치 좌표를 얻어옴
                CameraPosition cameraPosition = naverMap.getCameraPosition();
                LatLng currentPosition = cameraPosition.target;
                double latitude = currentPosition.latitude;
                double longitude = currentPosition.longitude;

                // 이제 latitude와 longitude 변수에 현재 위치의 위도와 경도가 저장됨
                Log.d("MapActivity", "현재 위치: " + latitude + ", " + longitude);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }
}
