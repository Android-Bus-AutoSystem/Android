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

import java.util.ArrayList;
import java.util.List;


public class BusArriveActivity extends AppCompatActivity {
    private MySingleton mySingleton = MySingleton.getInstance();

    private BusArriveAdapter busArriveAdapter;

    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busarrivetime);

        TextView busNumberTextView = findViewById(R.id.mainbusstp_number);

//         인텐트로부터 버스 번호를 가져옴
        String busNumber = getIntent().getStringExtra("busNumber");
        busNumberTextView.setText( busNumber + "번");

        btn = findViewById(R.id.btn);
        List<BusstopArriveResponse.Response.Body.Items.Item> bus = mySingleton.getBusStopListSingleton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BusArriveActivity.this, NFCActivity.class);
                if (bus != null){
                    BusstopArriveResponse.Response.Body.Items.Item firstItem = bus.get(0);
                    intent.putExtra("firstBusStopNumber", firstItem.routeno);
                    intent.putExtra("firstBusStopName", firstItem.nodenm);
//                    intent.putExtra("firstBusStopName", firstItem.);
                    intent.putExtra("busNumber", busNumber);
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
