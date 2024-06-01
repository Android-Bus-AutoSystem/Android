package com.school.bus_autosystem.ResponseClass;

public class BusStop {
    private String stopNumber;
    private String stopName;
    private double latitude;
    private double longitude;
    private String mobileNumber;


    public BusStop(String stopNumber, String stopName, double latitude, double longitude, String mobileNumber) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mobileNumber = mobileNumber;
    }

    // Getter 메서드들 생략...
    public String getStopNumber() {
        return stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }



}
