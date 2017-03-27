package com.ermakov.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ermakov.weatherapp.models.weather.Weather;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    boolean success = intent.getBooleanExtra(WeatherUpdateService.EXTRA_SUCCESS, false);
                    if (success) {
                        Weather weather = intent.getParcelableExtra(WeatherUpdateService.EXTRA_WEATHER);
                        Log.d(TAG, weather.toString());
                    }
                    else {
                        int httpCode = intent.getExtras().getInt(WeatherUpdateService.EXTRA_HTTP_CODE);
                        Log.d(TAG, "Error code: " + httpCode);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(WeatherUpdateService.ACTION_GET_WEATHER_DATA);
        registerReceiver(weatherReceiver, intentFilter);

        startService(new Intent(this, WeatherUpdateService.class));
    }
}
