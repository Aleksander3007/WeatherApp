package com.ermakov.weatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.net.WeatherApi;
import com.ermakov.weatherapp.net.WeatherApiFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Сервис отвечает за получение данных о погоде из интернета, отправку данных в приложение,
 * и отправку уведомлений.
 */
public class WeatherUpdateService extends IntentService {

    public static final String TAG = WeatherUpdateService.class.getSimpleName();

    public static final String ACTION_GET_WEATHER_DATA = "com.ermakov.weatherapp.ACTION_GET_WEATHER_DATA";

    public static final String EXTRA_WEATHER = "EXTRA_WEATHER";
    public static final String EXTRA_SUCCESS = "EXTRA_SUCCESS";
    public static final String EXTRA_HTTP_CODE = "EXTRA_HTTP_CODE";

    /**
     * Для объявления в манифесте Service требуется default-конструктор без параметров.
     */
    public WeatherUpdateService() {
        super(WeatherUpdateService.class.getCanonicalName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WeatherUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            WeatherApi weatherApi = WeatherApiFactory.createWeatherApiService();
            weatherApi.getWeatherByCityName("London").enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    Intent responseIntent = new Intent(ACTION_GET_WEATHER_DATA);
                    responseIntent.putExtra(WeatherUpdateService.EXTRA_SUCCESS, response.isSuccessful());
                    responseIntent.putExtra(WeatherUpdateService.EXTRA_WEATHER, response.code());
                    if (response.isSuccessful()) {
                        Log.d(TAG, response.body().toString());
                        responseIntent.putExtra(WeatherUpdateService.EXTRA_WEATHER, (Parcelable) response.body());
                    }
                    sendBroadcast(responseIntent);
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
