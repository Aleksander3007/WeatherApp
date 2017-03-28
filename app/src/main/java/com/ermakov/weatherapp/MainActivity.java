package com.ermakov.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.models.weather.Wind;

import java.util.List;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_date_time) TextView mDateTimeTextView;
    @BindView(R.id.tv_description) TextView mDescriptionTextView;
    @BindView(R.id.tv_sunrise) TextView mSunriseTextView;
    @BindView(R.id.tv_sunset) TextView mSunsetTextView;
    @BindView(R.id.tv_temperature) TextView mTemperatureTextView;
    @BindView(R.id.tv_wind) TextView mWindTextView;

    private BroadcastReceiver mWeatherReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startService(new Intent(this, WeatherUpdateService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerWeatherUpdateReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mWeatherReceiver);
    }

    /**
     * Создаем и регистрируем BroadcastReceiver для приема данных о погоде.
     */
    private void registerWeatherUpdateReceiver() {
        mWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    boolean success = intent.getBooleanExtra(WeatherUpdateService.EXTRA_SUCCESS, false);
                    if (success) {
                        Weather weather = intent.getParcelableExtra(WeatherUpdateService.EXTRA_WEATHER);
                        updateView(weather);
                    }
                    else {
                        int httpCode = intent.getExtras().getInt(WeatherUpdateService.EXTRA_HTTP_CODE);
                        Log.d(TAG, "Error code: " + httpCode);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(WeatherUpdateService.ACTION_GET_WEATHER_DATA);
        registerReceiver(mWeatherReceiver, intentFilter);
    }

    private void updateView(Weather weather) {
        updateTemperature(weather.getCharacteristics().getTemperature());
        updateWeatherDescription(weather.getDescriptions());
        updateDateTime(weather.getDataCalculation());
        updateSunInfo(weather.getSun());
        updateWindInfo(weather.getWind());
    }

    private void updateWindInfo(Wind wind) {
        mWindTextView.setText(getWindStr(wind));
    }

    private String getWindStr(Wind wind) {
        return String.format("Wind %.0f m/s from %d", wind.getSpeed(), wind.getDirectionDegrees());
    }

    private void updateSunInfo(Weather.Sun sun) {
        mSunriseTextView.setText(String.valueOf(sun.getSunrise()));
        mSunsetTextView.setText(String.valueOf(sun.getSunset()));
    }

    private void updateDateTime(int dataCalculation) {
        mDateTimeTextView.setText(String.valueOf(dataCalculation));
    }

    private void updateWeatherDescription(List<Weather.Description> descriptions) {
        StringBuilder totalDescription = new StringBuilder();
        for (Weather.Description description : descriptions) {
            totalDescription.append(description.getMain());
            totalDescription.append(",");
        }
        totalDescription.deleteCharAt(totalDescription.length() - 1);
        mDescriptionTextView.setText(totalDescription);
    }

    private void updateTemperature(float temperature) {
        mTemperatureTextView.setText(getTemperatureStr(temperature));
    }

    private String getTemperatureStr(float temperature) {
        return String.format("%.0f", temperature);
    }
}
