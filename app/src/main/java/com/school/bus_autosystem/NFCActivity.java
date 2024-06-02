package com.school.bus_autosystem;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCActivity extends AppCompatActivity {

    Button cancle;

    TextView mainbusstop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
//        BusstopArriveResponse.Response.Body.Items.Item firstItem = bus.get(0);
//        intent.putExtra("firstBusStopNumber", firstItem.routeno);
//        intent.putExtra("firstBusStopName", firstItem.nodenm);
//
        //110-1번
        String busNumber = getIntent().getStringExtra("firstBusStopNumber");
        //가야1치안센터
        String busStopName = getIntent().getStringExtra("firstBusStopName");
        String busStopid = getIntent().getStringExtra("firstBusStopId");

        Log.d("checkNFC", "nfc : " + busStopid);
        Log.d("NFC", busNumber + " " + busStopName);

        cancle = findViewById(R.id.cancel_btn);
        mainbusstop = findViewById(R.id.MainBusStop);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mainbusstop.setText(busStopName);


    }
    @Override
    public void onBackPressed() {
        // 현재 액티비티를 종료
        super.onBackPressed();
        finish();
    }
}



