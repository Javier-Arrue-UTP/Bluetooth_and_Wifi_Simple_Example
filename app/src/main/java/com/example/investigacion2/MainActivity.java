package com.example.investigacion2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//GitHub :)

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btWifi(View view){
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);

    }

    public void btnBluetooth(View view){
        Intent intent = new Intent(this,BluetoothActivity.class);
        startActivity(intent);
    }

}
