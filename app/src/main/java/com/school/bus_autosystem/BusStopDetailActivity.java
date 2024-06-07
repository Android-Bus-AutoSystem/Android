package com.school.bus_autosystem;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.bus_autosystem.Adapter.BusStopAdapter;
import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;
import com.school.bus_autosystem.RetrofitClient.BusstopArriveClient;
import com.school.bus_autosystem.Retrofitinterface.BusArrivalinterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusStopDetailActivity extends AppCompatActivity {
    private static final String SERVICE_KEY = "ZEMlvlhLRj4MWmL4i3tWe/YeTMmm4poPtCz03TK7+kCWv5hukZpl5EVWN6UWFuk3fizuY85oCYcTl5V9f7GfrA==";
    private RecyclerView recyclerView;
    private BusStopAdapter busStopAdapter;
    private List<BusstopArriveResponse.Response.Body.Items.Item> busStopList;
    private MySingleton singleton = MySingleton.getInstance();

    TextView MainBusStopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop_detail);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        busStopList = new ArrayList<>();
        busStopAdapter = new BusStopAdapter(this, busStopList);
        recyclerView.setAdapter(busStopAdapter);

        // 인텐트로부터 버스 정류장 이름을 가져옴
        String busStopName = getIntent().getStringExtra("busStopName");
        String busStopId = getIntent().getStringExtra("busStopId");

        MainBusStopName = (TextView) findViewById(R.id.mainbusstp_name);
        MainBusStopName.setText(busStopName);

        BusArrivalinterface service = BusstopArriveClient.getBusArrivalService();
        Call<BusstopArriveResponse> call = service.getBusArrivalInfo(SERVICE_KEY, 1, 50, "json", 21, busStopId);

        call.enqueue(new Callback<BusstopArriveResponse>() {
            @Override
            public void onResponse(Call<BusstopArriveResponse> call, Response<BusstopArriveResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BusstopArriveResponse apiResponse = response.body();
//                    Set<Integer> uniqueRoutes = new HashSet<>();
                    Set<String> uniqueRoutes = new HashSet<>();
                    List<BusstopArriveResponse.Response.Body.Items.Item> filteredList = new ArrayList<>();

                    List<BusstopArriveResponse.Response.Body.Items.Item> AllBusStopList = new ArrayList<>();

                    List<BusstopArriveResponse.Response.Body.Items.Item> list = singleton.getBusStopListSingleton();
                    if (list != null ){
                        singleton.clearBusStopListSingleton();
                    }

                    for (BusstopArriveResponse.Response.Body.Items.Item item : apiResponse.response.body.items.itemList) {
                        if ("저상버스".equals(item.vehicletp)) {
                            AllBusStopList.add(item);
                        }
                    }
                    singleton.setBusStopListSingleton(AllBusStopList);

                    for (BusstopArriveResponse.Response.Body.Items.Item item : apiResponse.response.body.items.itemList) {
                        if ("저상버스".equals(item.vehicletp) && uniqueRoutes.add(item.routeid)) {  // routeid를 Set에 추가 및 비교
                            filteredList.add(item);
                        }
                    }
//                    for (BusstopArriveResponse.Response.Body.Items.Item item : apiResponse.response.body.items.itemList) {
//                        if ("저상버스".equals(item.vehicletp) && uniqueRoutes.add(item.routeno)) {
//                            filteredList.add(item);
//                        }
//                    }
                    busStopList.clear();
                    busStopList.addAll(filteredList);
                    busStopAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API Error", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BusstopArriveResponse> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }
    @Override
    public void onBackPressed() {
        // 현재 액티비티를 종료
        super.onBackPressed();
        finish();
    }
}
