package com.school.bus_autosystem.Retrofitinterface;

import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusArrivalinterface {

    @GET("getSttnAcctoArvlPrearngeInfoList")
    Call<BusstopArriveResponse> getBusArrivalInfo(
            @Query("serviceKey") String serviceKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("_type") String type,
            @Query("cityCode") int cityCode,
            @Query("nodeId") String nodeId
    );
}
