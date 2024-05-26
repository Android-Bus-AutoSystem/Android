package com.school.bus_autosystem;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kakao.vectormap.Compass;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapGravity;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.ScaleBar;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;

public class MapActivity extends AppCompatActivity {
    MapView mapView;
    KakaoMap kakaoMap;
    TextView location_busstop2, near_busstop, location_busstop;
    private ScaleBar scaleBar;
    private Compass compass;
    private int startZoomLevel = 15;
    private LatLng startPosition = LatLng.from(37.394660, 127.111182);   // 판교역

//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//    private FusedLocationProviderClient fusedLocationClient;
//    private LatLng currentLocation;

    // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
    private KakaoMapReadyCallback readyCallback = new KakaoMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull KakaoMap map) {
            kakaoMap = map;

            scaleBar = kakaoMap.getScaleBar();
            scaleBar.setPosition(MapGravity.LEFT | MapGravity.BOTTOM, 20, 20);
            scaleBar.show();
            compass = kakaoMap.getCompass();
            compass.show();

            Toast.makeText(getApplicationContext(), "Map Start!", Toast.LENGTH_SHORT).show();

            near_busstop.setText(String.valueOf(startPosition.getLatitude()));
            location_busstop.setText(String.valueOf(startPosition.getLongitude()));
            location_busstop2.setText(String.valueOf(startZoomLevel));

            Log.i("k3f", "startPosition: "
                    + kakaoMap.getCameraPosition().getPosition().toString());
            Log.i("k3f", "startZoomLevel: "
                    + kakaoMap.getZoomLevel());
//            if (currentLocation != null) {
//                updateMapLocation();
//            }
        }

        @NonNull
        @Override
        public LatLng getPosition() {
            return startPosition;
        }

        @NonNull
        @Override
        public int getZoomLevel() {
            return startZoomLevel;
        }
    };

    // MapLifeCycleCallback 을 통해 지도의 LifeCycle 관련 이벤트를 수신할 수 있다.
    private MapLifeCycleCallback lifeCycleCallback = new MapLifeCycleCallback() {

        @Override
        public void onMapResumed() {
            super.onMapResumed();
        }

        @Override
        public void onMapPaused() {
            super.onMapPaused();
        }

        @Override
        public void onMapDestroy() {
            Toast.makeText(getApplicationContext(), "onMapDestroy",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapError(Exception error) {
            Toast.makeText(getApplicationContext(), error.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("Map Start");

//         near_busstop = (TextView) findViewById(R.id.near_busstop_text);
//         location_busstop = (TextView) findViewById(R.id.location_busstop_text);
//         location_busstop2 = (TextView) findViewById(R.id.location_busstop_text2);

        mapView = findViewById(R.id.map_view);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION}, LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            getLastKnownLocation();
//        }

        mapView.start(lifeCycleCallback, readyCallback);

    }

//    private void getLastKnownLocation() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            currentLocation = LatLng.from(location.getLatitude(), location.getLongitude());
//                            if (kakaoMap != null) {
//                                updateMapLocation();
//                            }
//                        }
//                    }
//                });
//    }

//    private void updateMapLocation() {
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(currentLocation)
//                .zoom(15)
//                .build();
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        kakaoMap.moveCamera(cameraUpdate);
//
//        near_busstop.setText(String.valueOf(currentLocation.getLatitude()));
//        location_busstop.setText(String.valueOf(currentLocation.getLongitude()));
//        location_busstop2.setText(String.valueOf(kakaoMap.getZoomLevel()));
//
////        kakaoMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
////        near_busstop.setText(String.valueOf(currentLocation.getLatitude()));
////        location_busstop.setText(String.valueOf(currentLocation.getLongitude()));
////        location_busstop2.setText(String.valueOf(kakaoMap.getZoomLevel()));
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastKnownLocation();
//            } else {
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
