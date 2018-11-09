package com.papaplant.bluetoothapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    // Debugging
    private static final String TAG = "biyam";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //Layout
    private Button btn_Connect;
    private TextView txt_Result;

    //SeekbarTotal
    private SeekBar seekBarTotal;

    //Seekbar1
    private SeekBar seekBar1;

    //Seekbar2
    private SeekBar seekBar2;

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
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        /** Main Layout **/
        btn_Connect = (Button) findViewById(R.id.btn_connect);
        txt_Result = (TextView) findViewById(R.id.txt_result);

        btn_Connect.setOnClickListener(this);

        //Seekbar Object 생성
        seekBarTotal = (SeekBar) findViewById(R.id.seekBarTotal);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar1);

        seekBarTotal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "SeekBar total ProgressChanged : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar total StartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar total StopTrackingTouch");
            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "SeekBar 1 ProgressChanged : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 1 StartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 1 StopTrackingTouch");
            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "SeekBar 2 ProgressChanged : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 2 StartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 2 StopTrackingTouch");
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

    @Override
    public void onClick(View v){
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

    public void onClickBluetooch(View v){
        Intent bluetoothintent = new Intent(getApplicationContext(), BluetoothListActivity.class);
        Log.d(TAG, "새창 띄우기");
        startActivity(bluetoothintent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {

                } else {

                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }
}
