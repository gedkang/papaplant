package com.papaplant.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


public class BluetoothService {
    // Debugging
    private static final String TAG = "biyam";

    //Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    // Constructors
    public BluetoothService(Activity ac, Handler h)
    {
        mActivity = ac;
        mHandler = h;
        Log.d(TAG, "BluetoothAdapter 얻기 시작");
        //BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
            Log.d(TAG, "BluetoothAdapter 얻기 실패");
    }

    public boolean getDeviceState(){
        Log.d(TAG, "Check the Bluetooth support");

        if(btAdapter == null){
            Log.d(TAG, "Bluetooth is not available");

            return false;
        } else {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    public void enableBluetooth(){
        Log.d(TAG,"Check the enabled Bluetooth");

        if(btAdapter.isEnabled()){
            // 기기의 블루투스 상태가 off인 경우
            Log.d(TAG, "Bluetooth enable Now");

            //next step
        } else {
            //기기의 블루투스 상태가 off인 경우
            Log.d(TAG, "Bluetooth Enable Request");

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i,REQUEST_ENABLE_BT);
        }
    }

}
