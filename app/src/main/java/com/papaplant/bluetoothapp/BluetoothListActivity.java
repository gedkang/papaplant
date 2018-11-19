package com.papaplant.bluetoothapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class BluetoothListActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "biyam";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //Layout
    private Button btn_Connect_blue;
    private TextView txt_Result;

    private BluetoothService btService = null;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "새창 onCreate");

        setContentView(R.layout.activity_bluetooth);

        // Bluetooth Service 클래스 생성
        if(btService == null){
            btService = new BluetoothService(this, mHandler);
        }

        /** Main Layout **/
        btn_Connect_blue = (Button) findViewById(R.id.search_bluetootd);
        txt_Result = (TextView) findViewById(R.id.bluetooth_result);

        btn_Connect_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Main Activity 버튼 클릭");
                if(btService == null){
                    Log.d(TAG, "블루투스 서비스 모듈 없음");
                } else {
                    if (btService.getDeviceState()) {
                        Log.d("biyam", "onClick true");
                        //블루투스가 지원 가능한 기기일 때
                        btService.enableBluetooth();
                    } else {
                        Log.d(TAG, "onClick false");
                        //finish();
                    }
                }
            }
        });

        // Bluetooth Service 클래스 생성
        if(btService == null){
            btService = new BluetoothService(this, mHandler);
        }

        if(btService == null){
            Log.d(TAG, "블루투스 Object 얻기 실패");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Bluetooth is enabled.");
                } else {

                    Log.d(TAG, "Bluetooth is not enabled   .");
                }
                break;
        }
    }

    //돌아가기 버튼 눌렀을 때
    public void onBackButtonCliked(View v){
        Log.d(TAG, "닫기 버튼 클릭");
        //화면 종료
        finish();
    }
}
