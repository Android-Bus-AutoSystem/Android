package com.school.bus_autosystem;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.kakao.vectormap.Compass;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapGravity;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.ScaleBar;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.TrackingManager;

public class MapActivity  extends AppCompatActivity {
    MapView mapView;
    KakaoMap kakaoMap;
    TextView location_busstop2, near_busstop, location_busstop;
    private ScaleBar scaleBar;
    private Compass compass;
    private int startZoomLevel = 15;
    private LatLng startPosition = LatLng.from(37.394660,127.111182);   // 판교역


    // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
    private KakaoMapReadyCallback readyCallback = new KakaoMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull KakaoMap map) {
            kakaoMap = map;

            scaleBar = kakaoMap.getScaleBar();
            scaleBar.setPosition(MapGravity.LEFT|MapGravity.BOTTOM, 20, 20);
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

         near_busstop = (TextView) findViewById(R.id.near_busstop_text);
         location_busstop = (TextView) findViewById(R.id.location_busstop_text);
         location_busstop2 = (TextView) findViewById(R.id.location_busstop_text2);

        mapView = findViewById(R.id.map_view);
        mapView.start(lifeCycleCallback, readyCallback);
    }
}
