package com.school.bus_autosystem.Retrofitinterface;

import com.school.bus_autosystem.ResponseClass.BusStationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusStationApi {
    @GET("getCrdntPrxmtSttnList")
    Call<BusStationResponse> getBusStations(
            @Query("serviceKey") String serviceKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("_type") String type,
            @Query("gpsLati") double gpsLati,
            @Query("gpsLong") double gpsLong
    );
}
