package com.ermakov.weatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.net.WeatherApi;
import com.ermakov.weatherapp.net.WeatherApiFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Сервис отвечает за получение данных о погоде из интернета, отправку данных в приложение,
 * и отправку уведомлений.
 */
public class WeatherUpdateService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = WeatherUpdateService.class.getSimpleName();

    public static final String ACTION_GET_WEATHER_DATA = "com.ermakov.weatherapp.ACTION_GET_WEATHER_DATA";

    public static final String EXTRA_WEATHER = "EXTRA_WEATHER";
    public static final String EXTRA_SUCCESS = "EXTRA_SUCCESS";
    public static final String EXTRA_HTTP_CODE = "EXTRA_HTTP_CODE";

    /** Время ожидания получения геопозиции. */
    private static final int TIMEOUT_LOCATION_REQUEST = 60 * 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    /**
     * Для объявления в манифесте Service требуется default-конструктор без параметров.
     */
    public WeatherUpdateService() {
        this(WeatherUpdateService.class.getCanonicalName());
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
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setExpirationDuration(TIMEOUT_LOCATION_REQUEST)
                .setNumUpdates(1);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            mGoogleApiClient.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if (mLastLocation != null) {
            //    double latitude = mLastLocation.getLatitude();
            //    double longitude = mLastLocation.getLongitude();
            //    requestWeather((int)latitude, (int)longitude);
            //}
            //else {
            //    // TODO: Что делать? Пытаться получить еще раз или получать другим способом
            //    Log.d(TAG, "mLastLocation == null");
            //}
        }
        catch (SecurityException sex) {
            sex.printStackTrace();
            // TODO:  Необходимо оповестить, что нет полномочий на получение геопозиции, чтобы приложение запросило их.
            /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             */
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d(TAG, String.format("Location via Google API: {long, lat} = {%f, %f}", longitude, latitude));
        requestWeather(latitude, longitude);
    }

    private void requestWeather(double latitude, double longitude) {
        WeatherApi weatherApi = WeatherApiFactory.createWeatherApiService();
        weatherApi.getWeatherByGeoCoordinates(latitude, longitude).enqueue(new Callback<Weather>() {
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
}
