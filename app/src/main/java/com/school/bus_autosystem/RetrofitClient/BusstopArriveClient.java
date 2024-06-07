package com.school.bus_autosystem.RetrofitClient;

import com.school.bus_autosystem.Retrofitinterface.BusArrivalinterface;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusstopArriveClient {
    private static final String BASE_URL = "https://apis.data.go.kr/1613000/ArvlInfoInqireService/";
    private static Retrofit retrofit = null;

    public static BusArrivalinterface getBusArrivalService() {
        if (retrofit == null) {
            // HTTP 요청/응답 로깅 인터셉터 설정
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // OkHttp 클라이언트 설정
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)  // 로깅 인터셉터 추가
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(BusArrivalinterface.class);
    }
}