package com.ermakov.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.net.WeatherApiFactory;
import com.ermakov.weatherapp.net.WeatherApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            WeatherApiService weatherApiService = WeatherApiFactory.createWeatherApiService();
            weatherApiService.getWeatherByCityName("London").enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, response.body().toString());
                    }
                    else {
                        Log.d(TAG, "Error code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Weather> call, Throwable t) {
                    Log.d(TAG, "onFailure()");
                    t.printStackTrace();
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
