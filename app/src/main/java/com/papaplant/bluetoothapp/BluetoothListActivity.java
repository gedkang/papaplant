package com.papaplant.bluetoothapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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
    private BluetoothAdapter btAdapter = null;
    private Set<BluetoothDevice> devices;


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
                        Log.d(TAG, "onClick true");
                        //블루투스가 지원 가능한 기기일 때
                        btService.enableBluetooth();

                        btAdapter = btService.getBtAdapter();

                        selectBluetoothDevice();

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
                    btAdapter = BluetoothAdapter.getDefaultAdapter();

                } else {

                    Log.d(TAG, "Bluetooth is not enabled   .");
                }
                break;
        }
    }

    public void selectBluetoothDevice() {

        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = btAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        int pariedDeviceCount = devices.size();

        // 페어링 되어있는 장치가 없는 경우
        if(pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog.Builder builder = null;

            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");

            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();

            // 모든 디바이스의 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }

            list.add("취소");

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    //connectDevice(charSequences[which].toString());
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);

            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }
    }


    //돌아가기 버튼 눌렀을 때
    public void onBackButtonCliked(View v){
        Log.d(TAG, "닫기 버튼 클릭");
        //화면 종료
        finish();
    }
}
