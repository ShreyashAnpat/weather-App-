package com.example.weather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {

    String apiKey = "327f16ddf40fa06ef37f7d56dfa1c34a";
    FusedLocationProviderClient fusedLocationProviderClient ;
    TextView temperature ,windFlow ,pressure,humidity,max_temp,min_temp , sun_rise, sun_set , condition  , state;

    Button changeLocation ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperature = findViewById(R.id.temprature);
        windFlow = findViewById(R.id.windFlow);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        max_temp = findViewById(R.id.max_temp);
        min_temp = findViewById(R.id.min_temp);
        sun_rise = findViewById(R.id.sun_rise);
        sun_set = findViewById(R.id.sun_set);
        condition = findViewById(R.id.state);
        state = findViewById(R.id.textView2);
        changeLocation = findViewById(R.id.changeLocation);


        SharedPreferences sharedpreferences = getSharedPreferences(  "CityName" , Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your City Name ...");
        final EditText input = new EditText(this);
        input.setHint("City Name");
        builder.setView(input);
        builder.setPositiveButton("Get Weather", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              String  m_Text = input.getText().toString();
                SharedPreferences sharedpreferences = getSharedPreferences(  "CityName" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("City" ,m_Text );
                editor.commit();
                loadData(m_Text);

            }
        });

        String cityName = sharedpreferences.getString("City" , "");
        if (cityName.equals("")){
            builder.show();
        }

        loadData(cityName);

        changeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });


//     adds :-

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setTitle("Enter your City Name ...");
//                final EditText input = new EditText(getApplicationContext());
//                input.setHint("City Name");
//                builder.setView(input);
//                builder.setPositiveButton("Get Weather", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String  m_Text = input.getText().toString();
//                        SharedPreferences sharedpreferences = getSharedPreferences(  "CityName" , Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("City" ,m_Text );
//                        editor.commit();
//                        loadData(m_Text);
//
//                    }
//                });
//            }
//        });

//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    }

    private void loadData(String cityName) {
        String uri = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid="+apiKey;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    state.setText(response.getString("name"));

                    JSONObject mainObject = response.getJSONObject("main");
                    String  Temp = mainObject.getString("temp");
                    double finalTemp = Math.round((Float.parseFloat(Temp)-273.15)*100.0)/100.0;
                    temperature.setText(String.valueOf(finalTemp)+"°");

                    String  Pressure = mainObject.getString("pressure");
                    pressure.setText(String.valueOf(Pressure));

                    String  Humidity = mainObject.getString("humidity");
                    humidity.setText(String.valueOf(Humidity));

                    String  MAx_Temp = mainObject.getString("temp_max");
                    double finalMax_Temp = Math.round((Float.parseFloat(MAx_Temp)-273.15)*100.0)/100.0;
                    max_temp.setText(String.valueOf(finalMax_Temp)+"°");

                    String  Min_Temp = mainObject.getString("temp_max");
                    double finalMin_Temp = Math.round((Float.parseFloat(Min_Temp)-273.15)*100.0)/100.0;
                    min_temp.setText(String.valueOf(finalMin_Temp)+"°");

                    JSONObject  windObject = response.getJSONObject("wind");
                    String WindFlowSpeed = windObject.getString("speed");
                    double finalWindFlowSpeed = Math.round((Float.parseFloat(WindFlowSpeed)*3.6)*100.0)/100.0;
                    windFlow.setText(String.valueOf(finalWindFlowSpeed));

                    JSONObject  sysObject = response.getJSONObject("sys");

                    Date date = new Date(Integer.parseInt(sysObject.getString("sunrise").toString()) * 1000L);
                    DateFormat format = new SimpleDateFormat("h:mma");
                    format.setTimeZone(TimeZone.getDefault());
                    String formatted = format.format(date);
                    sun_rise.setText(formatted );

                    Date dates = new Date(Integer.parseInt(sysObject.getString("sunset").toString()) * 1000L);
                    String mFormatted = format.format(dates);
                    sun_set.setText(mFormatted );



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);

    }



}