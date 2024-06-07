package com.school.bus_autosystem.RetrofitClient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    private static final String BASE_URL = "https://apis.data.go.kr/1613000/BusSttnInfoInqireService/";
//    private static final String SERVICE_KEY = "ZEMlvlhLRj4MWmL4i3tWe%2FYeTMmm4poPtCz03TK7%2BkCWv5hukZpl5EVWN6UWFuk3fizuY85oCYcTl5V9f7GfrA%3D%3D";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // JSON을 lenient 모드로 파싱하기 위한 Gson 설정
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // HTTP 요청/응답 로깅 인터셉터 설정
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 요청을 수정하는 커스텀 인터셉터 설정
            Interceptor requestInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();


//                    Log.d("intercept", originalRequest.body().toString());

                    Request modifiedRequest = originalRequest.newBuilder()
                            // 여기서 요청을 수정할 수 있습니다. 예를 들어, 쿼리 파라미터를 추가하거나 헤더를 수정할 수 있습니다.
                            .url(originalRequest.url().newBuilder()
//                                    .addQueryParameter("serviceKey", SERVICE_KEY)
                                    .build())
                            .build();
                    return chain.proceed(modifiedRequest);
                }
            };

            // OkHttp 클라이언트 설정
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)  // 로깅 인터셉터 추가
                    .addInterceptor(requestInterceptor)  // 커스텀 인터셉터 추가
                    .build();

            // Retrofit 인스턴스 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
