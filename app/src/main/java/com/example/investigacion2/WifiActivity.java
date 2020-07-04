package com.example.investigacion2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class WifiActivity extends AppCompatActivity {

    Button enableButton,disableButton, mnetworkList,mButtonConsumeAPI;
    TextView mTextViewStatus;
    WifiManager wifi;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        mButtonConsumeAPI = (Button)findViewById(R.id.button_consume_api);
        enableButton=(Button)findViewById(R.id.button_enable_wifi);
        disableButton=(Button)findViewById(R.id.button_disable_wifi);
        mnetworkList=(Button)findViewById(R.id.button_list_wifi);
        mTextViewStatus=(TextView)findViewById(R.id.textView_status);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        ShowWIFIStatus();

        enableButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                wifi.setWifiEnabled(true);

                ShowWIFIStatus();
                showToast("Activando Wifi");
            }
        });

        disableButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                wifi.setWifiEnabled(false);

                ShowWIFIStatus();
                showToast("Desactivando Wifi");
            }
        });

        mButtonConsumeAPI.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                String user = "julioaescobar";
                String url = "https://api.github.com/users/" + user;

                if (wifi.isWifiEnabled())
                {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(

                            Request.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("onResponse: " , response.toString());
                                    try {
                                        String id = "ID: " + response.get("id").toString() + "\n";
                                        String login = "Login: " + response.get("login").toString() + "\n";
                                        String url = "URL" + response.get("url").toString() + "\n";
                                        String name = "Name: " + response.get("name").toString() + "\n";
                                        mTextViewStatus.setText(id + login + url + name);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("onResponse: " , error.toString());
                                }
                            }
                    );
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        mnetworkList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ShowNetworkList();
            }
        });
    }

    private void ShowNetworkList() {
        String connectionDescription = "";

        if (wifi.isWifiEnabled())
        {
            WifiInfo connectionInfo = wifi.getConnectionInfo();
            String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
            connectionDescription = connectionDescription + "IP Address: " +  ip + "\n";
            connectionDescription = connectionDescription + "Mac Address: " +  connectionInfo.getMacAddress() + "\n";
        }

        TextView outputs = (TextView)findViewById(R.id.textView_wifi_list);
        outputs.setText(connectionDescription);
    }

    private void ShowWIFIStatus()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (wifi.isWifiEnabled()) {
                    mTextViewStatus.setText("Estado: Habilitado");
                }
                else
                {
                    mTextViewStatus.setText("Estado: Desactivado");
                }
            }
        }, 5000);
    }


    private void showToast(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();

    }


}
