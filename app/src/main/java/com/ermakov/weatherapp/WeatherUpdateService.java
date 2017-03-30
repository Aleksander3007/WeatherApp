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
    public static final String EXTRA_EXCEPTION = "EXTRA_EXCEPTION";

    public static final int EXCEPTION_REQUEST_SERVER = 1;
    public static final int EXCEPTION_SECURITY = 2;

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
            //
            //    Log.d(TAG, "mLastLocation == null");
            //}
        }
        catch (SecurityException sex) {
            sendSecurityException();
            sex.printStackTrace();
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
                        if (response.isSuccessful()) {
                            sendWeather(response.body());
                        }
                        else {
                            sendError(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Weather> call, Throwable t) {
                        sendRequestWeatherException();
                        t.printStackTrace();
                    }
                });
    }

    /**
     * Отправляем информацию о погоде.
     * @param weather инфо о погоде.
     */
    private void sendWeather(Weather weather) {

        Log.d(TAG, weather.toString());

        Intent responseIntent = new Intent(ACTION_GET_WEATHER_DATA);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_SUCCESS, true);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_WEATHER, weather);
        sendBroadcast(responseIntent);
    }

    /**
     * Отправить информацию об ошики.
     * @param httpErrorCode код ошибки.
     */
    private void sendError(int httpErrorCode) {
        Intent responseIntent = new Intent(ACTION_GET_WEATHER_DATA);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_SUCCESS, false);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_HTTP_CODE, httpErrorCode);
        sendBroadcast(responseIntent);
    }

    /**
     * Отправить информацию о том, что произошло исключение при попытки получить данные с сервера.
     */
    private void sendRequestWeatherException() {
        Intent responseIntent = new Intent(ACTION_GET_WEATHER_DATA);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_SUCCESS, false);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_EXCEPTION, EXCEPTION_REQUEST_SERVER);
        sendBroadcast(responseIntent);
    }

    /**
     * Отправить информацию о том нет разрешения на получение местаположения.
     */
    private void sendSecurityException() {
        Intent responseIntent = new Intent(ACTION_GET_WEATHER_DATA);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_SUCCESS, false);
        responseIntent.putExtra(WeatherUpdateService.EXTRA_EXCEPTION, EXCEPTION_SECURITY);
        sendBroadcast(responseIntent);
    }
}
