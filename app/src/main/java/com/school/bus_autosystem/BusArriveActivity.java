package com.school.bus_autosystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.bus_autosystem.Adapter.BusArriveAdapter;
import com.school.bus_autosystem.Adapter.BusStopAdapter;
import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class BusArriveActivity extends AppCompatActivity {
    private MySingleton mySingleton = MySingleton.getInstance();

    private BusArriveAdapter busArriveAdapter;

    private TextView busStopName_TextView;

    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busarrivetime);

        busStopName_TextView = findViewById(R.id.MainBusStop);
        TextView busNumberTextView = findViewById(R.id.mainbusstp_number);
//         인텐트로부터 버스 번호를 가져옴
        //110-1번
        String busNumber = getIntent().getStringExtra("busNumber");
        busNumberTextView.setText( busNumber + "번");
        String busStopId = getIntent().getStringExtra("busStopId");
        String busStopName = getIntent().getStringExtra("busStopNamer");
        busStopName_TextView.setText(busStopName);





        btn = findViewById(R.id.btn);
        List<BusstopArriveResponse.Response.Body.Items.Item> bus = mySingleton.getBusStopListSingleton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BusArriveActivity.this, NFCActivity.class);
                if (bus != null){
                    for (BusstopArriveResponse.Response.Body.Items.Item item : bus) {
                        Log.d("BusList", "RouteNo: " + item.routeno + ", ArrTime: " + item.arrtime + ", BusStopId: " + item.nodeid + ", vehicletp" + item.vehicletp);
                    }
                    BusstopArriveResponse.Response.Body.Items.Item firstItem = bus.get(0);
//                    intent.putExtra("firstBusStopNumber", firstItem.routeno);
//                    intent.putExtra("firstBusStopName", firstItem.nodenm);
//                    intent.putExtra("firstBusStopId", firstItem.nodeid);

                    intent.putExtra("firstBusStopNumber", busNumber);
                    intent.putExtra("firstBusStopName", busStopName);
                    intent.putExtra("firstBusStopId", busStopId);

//                    String busStopId = getIntent().getStringExtra("busStopId");
//                    intent.putExtra("firstBusStopName", firstItem.);
//                    intent.putExtra("busNumber", busNumber);
                }
//                intent.putExtra("busNumber", busNumber);
                startActivity(intent);
            }
        });



        List<BusstopArriveResponse.Response.Body.Items.Item> filterBus =new ArrayList<>();

        if (bus != null )
        {
            for (BusstopArriveResponse.Response.Body.Items.Item item : bus)
            {
//                Log.d("nodenm",busNumber + "1");
//                Log.d("nodenm",item.nodenm + " nodenm");
//                Log.d("nodenm",item.nodeid + " nodeid");
//                Log.d("nodenm",item.routeno + " routeno");

                if (item.routeno.equals(busNumber))
                {
//                    Log.d("nodenm", "here1");
                    filterBus.add(item);
                }
                else
                {

                }
            }
            busArriveAdapter = new BusArriveAdapter(this, filterBus);
             RecyclerView recyclerView = findViewById(R.id.recycler_view);
             recyclerView.setLayoutManager(new LinearLayoutManager(this));
             recyclerView.setAdapter(busArriveAdapter);


        }
        else
        {

        }




    }
    @Override
    public void onBackPressed() {
        // 현재 액티비티를 종료
        super.onBackPressed();
        finish();
    }
}
