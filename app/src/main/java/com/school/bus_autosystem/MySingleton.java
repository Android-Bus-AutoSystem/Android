package com.school.bus_autosystem;

import com.school.bus_autosystem.ResponseClass.BusStationResponse;
import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;

import java.util.List;

public class MySingleton {
    private static MySingleton instance;
    private String latitude = "";

    private BusStationResponse.Item firstBusStop;

    private List<BusstopArriveResponse.Response.Body.Items.Item> busStopListSingleton;

    private MySingleton() {
        // Private constructor to prevent instantiation
    }

    public static MySingleton getInstance() {
        if (instance == null) {
            instance = new MySingleton();
        }
        return instance;
    }

    public String getSomeData() {
        return latitude;
    }

    public BusStationResponse.Item getFirstBusStop() {return firstBusStop;}

    public void setLatitude(String someData) {
        this.latitude = someData;
    }

    public void setFirstBusStop(BusStationResponse.Item firstbusstop) {
        this.firstBusStop = firstbusstop;
    }

    public void setBusStopListSingleton( List<BusstopArriveResponse.Response.Body.Items.Item>  busStopList) {
        this.busStopListSingleton = busStopList;
    }

    public List<BusstopArriveResponse.Response.Body.Items.Item> getBusStopListSingleton()
    {
        return busStopListSingleton;
    }
    public void clearBusStopListSingleton(){
        busStopListSingleton.clear();
    }
}
