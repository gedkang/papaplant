package com.papaplant.bluetoothapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    // Debugging
    private static final String TAG = "papalant.biyam";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    //private static final int REQUEST_ENABLE_BT = 2;

    //Layout
    private Button btn_Connect;
    private TextView txt_Result;

    //SeekbarTotal
    private SeekBar seekBarTotal;

    //Seekbar1
    private SeekBar seekBar1;

    //Seekbar2
    private SeekBar seekBar2;

    //values
    private String total = "25";
    private String period = "300";
    private String once_time = "10";
    private String vent_onoff = "0";

    private BluetoothService btService = null;

    //BLE 구성을 위한 변수 선언 시작
    // used to identify adding bluetooth names
    private final static int REQUEST_ENABLE_BT= 1;
    // used to request fine location permission
    private final static int REQUEST_FINE_LOCATION= 2;
    // scan period in milliseconds
    private final static int SCAN_PERIOD= 5000;
    // ble adapter
    private BluetoothAdapter ble_adapter_;
    // flag for scanning
    private boolean is_scanning_= false;
    // flag for connection
    private boolean connected_= false;
    // scan results
    private Map<String, BluetoothDevice> scan_results_;
    // scan callback
    private ScanCallback scan_cb_;
    // ble scanner
    private BluetoothLeScanner ble_scanner_;
    // scan handler
    private Handler scan_handler_;


    public static String SERVICE_STRING = "0000aab0-f845-40fa-995d-658a43feea4c";
    public static UUID UUID_TDCS_SERVICE= UUID.fromString(SERVICE_STRING);
    public static String CHARACTERISTIC_COMMAND_STRING = "0000AAB1-F845-40FA-995D-658A43FEEA4C";
    public static UUID UUID_CTRL_COMMAND = UUID.fromString( CHARACTERISTIC_COMMAND_STRING );
    public static String CHARACTERISTIC_RESPONSE_STRING = "0000AAB2-F845-40FA-995D-658A43FEEA4C";
    public static UUID UUID_CTRL_RESPONSE = UUID.fromString( CHARACTERISTIC_RESPONSE_STRING );
    public final static String MAC_ADDR= "78:A5:04:58:A7:92";
    //BLE 구성을 위한 변수 선언 끝

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

        total = pad("25",3);
        period = pad("300", 4);
        once_time = pad("10", 4);

        /** Main Layout **/
        btn_Connect = (Button) findViewById(R.id.vent_connect);
        txt_Result = (TextView) findViewById(R.id.txt_result);

        btn_Connect.setOnClickListener(this);

        //Seekbar Object 생성
        seekBarTotal = (SeekBar) findViewById(R.id.seekBarTotal);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);

        seekBarTotal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "SeekBar total ProgressChanged : " + progress);
                total = pad(progress +"", 3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar total StartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar total StopTrackingTouch : " + total) ;
                sendMessage();
            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "SeekBar 1 ProgressChanged : " + progress);
                period = pad(progress + "", 4);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 1 StartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 1 StopTrackingTouch : " + period);
                sendMessage();
            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "SeekBar 2 ProgressChanged : " + progress);
                once_time = pad(progress + "", 4);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 2 StartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "SeekBar 2 StopTrackingTouch : " + once_time);
                sendMessage();
            }
        });

        // Bluetooth Service 클래스 생성
        if(btService == null){
            btService = new BluetoothService(this, mHandler);
        }


        if(btService == null){
            Log.d(TAG, "블루투스 Object 얻기 실패");
        }

        // ble manager
        BluetoothManager ble_manager;
        ble_manager= (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        // set ble adapter
        ble_adapter_= ble_manager.getAdapter();

        /*버튼 이벤트를 발생 시켜야 함.
        //// set click event handler
        btn_Connect.setOnClickListener( new Button.OnClickListener(){
            startScan(this) {

            }
        });

        //원본 : btn_scan_.setOnClickListener( (v) -> { startScan(v); });
        */
        btn_Connect.setOnClickListener( (v) -> { startScan(v); });
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
        Intent bluetoothintent = new Intent(getApplicationContext(), BluetoothLeListActivity.class);
        Log.d(TAG, "Bluetooth Le Scan Call");
        startActivity(bluetoothintent);
    }

    public void onClickBluetooch(View v){
        /*
        Intent bluetoothintent = new Intent(getApplicationContext(), BluetoothLeListActivity.class);
        Log.d(TAG, "Bluetooth Le Scan Call");
        startActivity(bluetoothintent);
        */
        Intent bluetoothintent = new Intent(getApplicationContext(), BluetoothListActivity.class);
        Log.d(TAG, "새창 호출");
        startActivity(bluetoothintent);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {

                } else {

                    Log.d(TAG, "Bluetooth is not enabled   .");
                }
                break;
        }
    }

    public String pad(String str, int size)
    {
        char padChar = '0';

        while (str.length() < size)
        {
            str = padChar + str;
        }
        return str;
    }

    public String sendMessage(){
        String returnMsg = "M" + total + "V" + vent_onoff + "T" + period + "P" + once_time + "PA";
        Log.d(TAG, returnMsg);
        return returnMsg;
    }



    /*
    Start BLE scan
     */
    private void startScan( View v ) {

        // setup scan filters
        List<ScanFilter> filters= new ArrayList<>();
        ScanFilter scan_filter= new ScanFilter.Builder()
                .setServiceUuid( new ParcelUuid( UUID_TDCS_SERVICE ) )
                .build();
        filters.add( scan_filter );

        //txt_Result.setText("Scanning...");
        // check ble adapter and ble enabled
        if (ble_adapter_ == null || !ble_adapter_.isEnabled()) {
            requestEnableBLE();
            //txt_Result.setText("Scanning Failed: ble not enabled");
            return;
        }
        // check if location permission
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            //txt_Result.setText("Scanning Failed: no fine location permission");
            return;
        }

        //// scan settings
        // set low power scan mode
        ScanSettings settings= new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_POWER )
                .build();

        scan_results_= new HashMap<>();
        scan_cb_= new BLEScanCallback( scan_results_ );

        //// now ready to scan
        // start scan
        ble_scanner_.startScan( filters, settings, scan_cb_ );
        // set scanning flag
        is_scanning_= true;
    }

    /*
    Request BLE enable
    */
    private void requestEnableBLE() {
        Intent ble_enable_intent= new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
        startActivityForResult( ble_enable_intent, REQUEST_ENABLE_BT );

    }

    /*
    Request Fine Location permission
     */
    private void requestLocationPermission() {
        requestPermissions( new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION );
    }


    /*
BLE Scan Callback class
*/
    private class BLEScanCallback extends ScanCallback {
        private Map<String, BluetoothDevice> cb_scan_results_;

        /*
        Constructor
         */
        BLEScanCallback( Map<String, BluetoothDevice> _scan_results ) {
            cb_scan_results_= _scan_results;
        }

        @Override
        public void onScanResult( int _callback_type, ScanResult _result ) {
            Log.d( TAG, "onScanResult" );
            addScanResult( _result );
        }

        @Override
        public void onBatchScanResults( List<ScanResult> _results ) {
            for( ScanResult result: _results ) {
                addScanResult( result );
            }
        }

        @Override
        public void onScanFailed( int _error ) {
            Log.e( TAG, "BLE scan failed with code " +_error );
        }

        /*
        Add scan result
         */
        private void addScanResult( ScanResult _result ) {
            // get scanned device
            BluetoothDevice device= _result.getDevice();
            // get scanned device MAC address
            String device_address= device.getAddress();
            // add the device to the result list
            cb_scan_results_.put( device_address, device );
            // log
            Log.d( TAG, "scan results device: " + device );
            txt_Result.setText( "add scanned device: " + device_address );
        }
    }
}
