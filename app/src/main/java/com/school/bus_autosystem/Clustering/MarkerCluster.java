package com.school.bus_autosystem.Clustering;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MarkerCluster {
    private List<Marker> markers = new ArrayList<>();
    private Marker clusterMarker;

    public MarkerCluster() {
        // 초기화 작업
    }

    public void addMarker(Marker marker) {
        markers.add(marker);
    }

    public void showClusterMarker(NaverMap naverMap) {
        if (clusterMarker == null) {
            clusterMarker = new Marker();
            clusterMarker.setPosition(getClusterPosition());
            clusterMarker.setCaptionText("Cluster (" + markers.size() + ")");
            clusterMarker.setMap(naverMap);
        } else {
            clusterMarker.setMap(naverMap);
        }
    }

    public void hideClusterMarker() {
        if (clusterMarker != null) {
            clusterMarker.setMap(null);
        }
    }

    private LatLng getClusterPosition() {
        double latitude = 0;
        double longitude = 0;
        for (Marker marker : markers) {
            latitude += marker.getPosition().latitude;
            longitude += marker.getPosition().longitude;
        }
        return new LatLng(latitude / markers.size(), longitude / markers.size());
    }

    public boolean contains(Marker marker) {
        return markers.contains(marker);
    }
}
