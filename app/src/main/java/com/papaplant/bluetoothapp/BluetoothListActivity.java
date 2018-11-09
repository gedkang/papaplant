package com.papaplant.bluetoothapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class BluetoothListActivity extends AppCompatActivity {
    // Debugging
    private static final String TAG = "biyam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "새창 onCreate");

        setContentView(R.layout.activity_bluetooth);
    }

    //돌아가기 버튼 눌렀을 때
    public void onBackButtonCliked(View v){
        Toast.makeText(getApplicationContext(), "버튼 눌렀어요.", Toast.LENGTH_LONG);
        Log.d(TAG, "닫기 버튼 클릭");
        //화면 종료
        finish();
    }
}
