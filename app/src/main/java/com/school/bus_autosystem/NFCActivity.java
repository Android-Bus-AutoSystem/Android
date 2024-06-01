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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter; // NFC 기능을 관리하는 데 사용됩니다.
    private PendingIntent pendingIntent; // NFC 인텐트를 처리하기 위해 설정됩니다.
    private IntentFilter[] intentFiltersArray; // 특정 NFC 이벤트를 필터링하기 위해 사용됩니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
//        BusstopArriveResponse.Response.Body.Items.Item firstItem = bus.get(0);
//        intent.putExtra("firstBusStopNumber", firstItem.routeno);
//        intent.putExtra("firstBusStopName", firstItem.nodenm);
//
        String busNumber = getIntent().getStringExtra("firstBusStopNumber");
        String busStopName = getIntent().getStringExtra("firstBusStopName");
        Log.d("NFC", busNumber + " " + busStopName);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);//NFC 어댑터를 초기화합니다.

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
//        NFC 태그가 스캔될 때 실행할 인텐트를 설정합니다. PendingIntent.FLAG_IMMUTABLE 플래그는 보안 향상을 위해 사용됩니다.
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[] { ndef };
    }

    //onResume: 액티비티가 활성화될 때 NFC 포어그라운드 디스패치를 활성화합니다.
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
        }
    }
    //onPause: 액티비티가 비활성화될 때 NFC 포어그라운드 디스패치를 비활성화합니다.
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
//    onNewIntent: 새로운 NFC 인텐트가 감지되었을 때 호출됩니다. 이 메서드는 인텐트가 NDEF 타입인지 확인하고, 그렇다면 processNfcIntent 메서드를 호출합니다
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            processNfcIntent(intent);
        }
    }
//    processNfcIntent: NFC 태그로부터 NDEF 메시지를 읽고, 각 NDEF 레코드를 처리합니다.
//    readText: NDEF 레코드에서 텍스트를 읽어옵니다.
    private void processNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return;
        }
        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                    Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    String tagContent = readText(ndefRecord);
                    Log.d("NFC", "NFC Tag content: " + tagContent);
                } catch (UnsupportedEncodingException e) {
                    Log.e("NFC", "Unsupported Encoding", e);
                }
            }
        }
    }
//    readText: NDEF 레코드의 페이로드를 UTF-8 또는 UTF-16 인코딩으로 읽어 텍스트를 반환합니다.
    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    public void onBackPressed() {
        // 현재 액티비티를 종료
        super.onBackPressed();
        finish();
    }
}

//public class NFCActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nfc);
//
//
//        String busNumber = getIntent().getStringExtra("firstBusStopNumber");
//        String b = getIntent().getStringExtra("firstBusStopName");
//        Log.d("NFC", busNumber+ " " +  b);
//    }
//    @Override
//    public void onBackPressed() {
//        // 현재 액티비티를 종료
//        super.onBackPressed();
//        finish();
//    }
//}





