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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class NFCActivity extends AppCompatActivity {

    Button cancle;

    TextView mainbusstop;


    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private boolean isNfcEnabled = false;
    private TextView nfcContent;

    private final String serverIP = "192.168.137.32"; // 안드로이드 서버 IP 주소
    private final int serverPort = 9000; // 안드로이드 서버 포트 번호
    private String busNumber;
    private String busStopName;
    private  String busStopid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
//        BusstopArriveResponse.Response.Body.Items.Item firstItem = bus.get(0);
//        intent.putExtra("firstBusStopNumber", firstItem.routeno);
//        intent.putExtra("firstBusStopName", firstItem.nodenm);
//
        //110-1번
        busNumber = getIntent().getStringExtra("firstBusStopNumber");
        //가야1치안센터
        busStopName = getIntent().getStringExtra("firstBusStopName");
        busStopid = getIntent().getStringExtra("firstBusStopId");

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

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcContent = findViewById(R.id.textview);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[]{ndef};
        Log.d("ServerThread", "ServerThread2");
        // 서버 소켓을 열어 아두이노로부터의 응답을 수신
        new Thread(new ServerThread()).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null && isNfcEnabled) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("NFC", "NFC Tag detected");

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            handleNdefDiscovered(intent);
        }
    }

    private void handleNdefDiscovered(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null) {
            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }
            // NDEF 메시지 처리
            NdefMessage ndefMessage = (NdefMessage) rawMsgs[0];
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                        Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        String tagContent = readText(ndefRecord);
                        Log.d("NFC", "NFC Tag content: " + tagContent);
                        nfcContent.setText("NFC Tag content: " + tagContent);

                        // NFC 태그에서 읽은 데이터를 ESP8266 서버로 전송
                        sendToArduino(tagContent);

                    } catch (UnsupportedEncodingException e) {
                        Log.e("NFC", "Unsupported Encoding", e);
                    }
                }
            }
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private void sendToArduino(String message) {
        String url = "http://" + serverIP + ":" + serverPort + "/update?result=" + message;
        new Thread(() -> {
            try {
                java.net.URL serverUrl = new java.net.URL(url);
                java.net.HttpURLConnection urlConnection = (java.net.HttpURLConnection) serverUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();
                Log.d("HTTP", "Response Code : " + responseCode);
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private String parseResult(String receivedMessage) {
        if (receivedMessage != null && receivedMessage.contains("result=")) {
            int startIndex = receivedMessage.indexOf("result=") + 7;
            int endIndex = receivedMessage.indexOf(" ", startIndex);
            if (endIndex == -1) {
                endIndex = receivedMessage.length();
            }
            return receivedMessage.substring(startIndex, endIndex);
        }
        return null;
    }

    private class ServerThread implements Runnable {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                Log.d("ServerThread", "Server socket created, waiting for connections...");
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        Log.d("ServerThread", "Client connected");
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // 비동기적으로 데이터를 읽기 위해 새로운 스레드 생성
                        new Thread(() -> {
                            try {
                                StringBuilder receivedData = new StringBuilder();
                                String inputLine;

                                while (true) {
                                    Log.d("ServerThread", "Waiting for input...");
                                    inputLine = in.readLine();
                                    if (inputLine != null) {
                                        Log.d("ServerThread", "Received line: " + inputLine);
                                        receivedData.append(inputLine).append("\n");
                                        final String receivedMessage = receivedData.toString().trim();
                                        Log.d("ServerThread", "Received from Arduino: " + receivedMessage);
                                        final String resultValue = parseResult(receivedMessage);
                                        Log.d("ServerThread", "Parsed result: " + resultValue);

                                        // UI 업데이트를 위해 메인 스레드로 전달
                                        runOnUiThread(() -> {
                                            Log.d("UIThread", "Updating UI with message: " + receivedMessage);
                                            if ("true".equals(resultValue))
                                            {
                                                Intent intent = new Intent(NFCActivity.this, ReservationActivity.class);
                                                intent.putExtra("firstBusStopNumber", busNumber);
                                                intent.putExtra("firstBusStopName", busStopName);
                                                intent.putExtra("firstBusStopId", busStopid);
                                                startActivity(intent);
                                                finish();
                                            }
//                                            nfcContent.setText("Received from Arduino: " + receivedMessage);
                                        });
                                    } else {
                                        Log.d("ServerThread", "Input stream closed.");
                                        break; // 입력 스트림이 닫혔으면 루프를 종료
                                    }
                                }
                            } catch (IOException e) {
                                Log.e("ServerThread", "Error in communication", e);
                            } finally {
                                try {
                                    in.close();
                                    socket.close();
                                    Log.e("ServerThread", "Resources closed");
                                } catch (IOException e) {
                                    Log.e("ServerThread", "Error closing resources", e);
                                }
                            }
                        }).start();
                    } catch (IOException e) {
                        Log.e("ServerThread", "Error in communication", e);
                    }
                }
            } catch (IOException e) {
                Log.e("ServerThread", "Server error", e);
            }
            Log.e("ServerThread", "Server thread ending unexpectedly");
        }
    }

    @Override
    public void onBackPressed() {
        // 현재 액티비티를 종료
        super.onBackPressed();
        finish();
    }
}



