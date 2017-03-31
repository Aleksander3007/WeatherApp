package com.ermakov.weatherapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ermakov.weatherapp.activities.SettingsActivity;
import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.models.weather.Wind;
import com.ermakov.weatherapp.utils.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_WEATHER_UPDATE = "KEY_WEATHER_UPDATE";
    private static final int PERMISSION_REQUEST_LOCATION = 1;

    // OpenWeatherMap.org просит не обновлять погоду чаще, чем раз в 10 минут, в мс.
    public static final int INTERVAL_WEATHER_UPDATE = 10 * 60 * 1000;
    /** Интервал отправки уведомлений о погоде. */
    public static final int INTERVAL_WEATHER_NOTIF = 180 * 60 * 1000; // каждые 3 часа.

    @BindView(R.id.tv_date_time) TextView mDateTimeTextView;
    @BindView(R.id.tv_description) TextView mDescriptionTextView;
    @BindView(R.id.tv_sunrise) TextView mSunriseTextView;
    @BindView(R.id.tv_sunset) TextView mSunsetTextView;
    @BindView(R.id.tv_temperature) TextView mTemperatureTextView;
    @BindView(R.id.tv_wind) TextView mWindTextView;
    @BindView(R.id.toolbar_main) Toolbar mMainToolbar;
    @BindView(R.id.abl_main) AppBarLayout mMainAppBarLayout;
    @BindView(R.id.l_weather_info) View mWeatherInfoLayout;
    @BindView(R.id.srl_main) SwipeRefreshLayout mMainSwipeRefreshLayout;

    private BroadcastReceiver mWeatherReceiver;
    private AlarmManager mWeatherAlarmManager;
    private PendingIntent mWeatherUpdatePendingIntent;
    private PendingIntent mWeatherNotifPendingIntent;

    /** Время последнего обновления инфо о погоде, мс. */
    private long mLastWeatherUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mMainToolbar);
        if (getSupportActionBar() != null) {
            // TODO: Заглушка title of ToolBar.
            getSupportActionBar().setTitle("Current location");
        }

        // Установка всех настроек приложение в default-значения во время первого запуска приложения.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        /**
         * Добавляем анимацию для AppBarLayout.
         */
        mMainAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                float currentAlpha = 1.0f - (float)(-verticalOffset) / mWeatherInfoLayout.getHeight();
                if (currentAlpha > 1.0f) currentAlpha = 1.0f;
                if (currentAlpha < 0.0f) currentAlpha = 0.0f;

                mWeatherInfoLayout.setAlpha(currentAlpha);
            }
        });

        requestLocationPermissions();

        mMainSwipeRefreshLayout.setOnRefreshListener(this);

        mWeatherAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        initWeatherNotifPendingIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "current time " + String.valueOf(SystemClock.elapsedRealtime()));
        // Когда приложение работает, уведомления о погоде будут лишними.
        cancelWeatherNotificationAlarm();
        registerWeatherUpdateReceiver();
        startWeatherUpdateAlarm();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelWeatherUpdateAlarm();
        unregisterReceiver(mWeatherReceiver);
        startWeatherNotificationAlarm();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_WEATHER_UPDATE, mLastWeatherUpdate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastWeatherUpdate = savedInstanceState.getLong(KEY_WEATHER_UPDATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.mi_settings:
                    openSettingsActivity();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                // Если запрос был отменен, то grantResults будет пустым.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Стартуем сразу сервис для получения информации о погоде.
                    requestWeatherInfo();

                } else {
                    Toast.makeText(this,
                            R.string.msg_fail_permission_location, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onRefresh() {
        requestWeather();
    }

    /**
     * Запрашиваем данные о погоде.
     */
    private void requestWeather() {
        requestLocationPermissions();
    }

    /**
     * Запрос данных о погоде.
     */
    private void requestWeatherInfo() {
        Intent intent = new Intent(this, WeatherUpdateService.class);
        intent.setAction(WeatherUpdateService.ACTION_GET_WEATHER_DATA);
        startService(intent);
        mLastWeatherUpdate = SystemClock.elapsedRealtime();
    }

    /**
     * Запуск будильника обновления данных о погоде через определенные интервалы.
     */
    private void startWeatherUpdateAlarm() {
        Intent weatherUpdateIntent = new Intent(this, WeatherUpdateService.class);
        weatherUpdateIntent.setAction(WeatherUpdateService.ACTION_GET_WEATHER_DATA);
        mWeatherUpdatePendingIntent
                = PendingIntent.getService(this, 0, weatherUpdateIntent, 0);
        // Стартуем через INTERVAL_WEATHER_UPDATE от последнего обновления.
        long firstRunInterval = INTERVAL_WEATHER_UPDATE - (SystemClock.elapsedRealtime() - mLastWeatherUpdate);
        Log.d(TAG, "update in " + String.valueOf(firstRunInterval));
        mWeatherAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + firstRunInterval,
                INTERVAL_WEATHER_UPDATE,
                mWeatherUpdatePendingIntent);
    }

    /**
     * Отмена будильника получения данных о погоде.
     */
    private void cancelWeatherUpdateAlarm() {
        Log.d(TAG, "cancel weather update");
        if (mWeatherAlarmManager != null) {
            mWeatherAlarmManager.cancel(mWeatherUpdatePendingIntent);
        }
    }

    /**
     * Запуск будильника отправки уведомлений о погоде через определенные интервалы.
     */
    private void startWeatherNotificationAlarm() {
        Log.d(TAG, "start notif");
        mWeatherAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INTERVAL_WEATHER_NOTIF,
                INTERVAL_WEATHER_NOTIF,
                mWeatherNotifPendingIntent);
    }

    /**
     * Отмена будильника отправки уведомлений о погоде.
     */
    private void cancelWeatherNotificationAlarm() {
        Log.d(TAG, "cancel notif");
        if (mWeatherAlarmManager != null) {
            mWeatherAlarmManager.cancel(mWeatherNotifPendingIntent);
        }
    }

    /**
     * Инициализация PendingIntent для отправки запроса на уведомление о погоде.
     */
    private void initWeatherNotifPendingIntent() {
        Intent weatherUpdateIntent = new Intent(this, WeatherUpdateService.class);
        weatherUpdateIntent.setAction(WeatherUpdateService.ACTION_CREATE_NOTIFICATION);
        mWeatherNotifPendingIntent
                = PendingIntent.getService(this, 0, weatherUpdateIntent, 0);
    }

    /**
     * Открыть окно с настройками.
     */
    private void openSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    /**
     * Создаем и регистрируем BroadcastReceiver для приема данных о погоде.
     */
    private void registerWeatherUpdateReceiver() {
        mWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMainSwipeRefreshLayout.setRefreshing(false);
                if (intent.getExtras() != null) {
                    boolean success = intent.getBooleanExtra(WeatherUpdateService.EXTRA_SUCCESS, false);
                    if (success) {
                        mLastWeatherUpdate = SystemClock.elapsedRealtime();
                        Weather weather = intent.getParcelableExtra(WeatherUpdateService.EXTRA_WEATHER);
                        updateView(weather);
                        Log.d(TAG, "Update Weather");
                    }
                    else {
                        if (intent.getExtras().containsKey(WeatherUpdateService.EXTRA_EXCEPTION) &&
                                intent.getExtras().getInt(WeatherUpdateService.EXTRA_EXCEPTION)
                                        == WeatherUpdateService.EXCEPTION_SECURITY) {
                            requestLocationPermissions();
                        }
                        else {
                            Toast.makeText(MainActivity.this, R.string.error_network_connection, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(WeatherUpdateService.ACTION_GET_WEATHER_DATA);
        registerReceiver(mWeatherReceiver, intentFilter);
    }

    private void updateView(Weather weather) {
        updateTitle(weather.getCityName());
        updateTemperature(weather.getCharacteristics().getTemperature());
        updateWeatherDescription(weather.getDescriptions());
        updateDateTime(weather.getDataCalculation());
        updateSunInfo(weather.getSun());
        updateWindInfo(weather.getWind());
    }

    private void updateTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Обновление информации о ветре.
     * @param wind объект ветер.
     */
    private void updateWindInfo(Wind wind) {
        String windSpeedUnit = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SettingsActivity.PREF_WIND_SPEED_UNITS, "");
        mWindTextView.setText(getWindStr(wind, windSpeedUnit));
    }

    private String getWindStr(Wind wind, String windSpeedUnit) {

        float windSpeed = wind.getSpeed();

        String unitStr;
        if (windSpeedUnit.equals(SettingsActivity.NAME_KILOMETERS_PER_HOUR)) {
            windSpeed = WeatherUtils.convertToKmPerHour(windSpeed);
            unitStr = getResources().getString(R.string.kilometers_per_hour);
        }
        else {
            unitStr = getResources().getString(R.string.meters_per_second);
        }

        String directionStr = convertDirectionToString(wind.getDirection());
        return String.format("Wind %.0f %s from %s", windSpeed, unitStr, directionStr);
    }

    private String convertDirectionToString(Wind.Direction direction) {
        switch (direction) {
            case N:
                return getResources().getString(R.string.north);
            case NNE:
                return getResources().getString(R.string.north_north_east);
            case NE:
                return getResources().getString(R.string.north_east);
            case ENE:
                return getResources().getString(R.string.east_north_east);
            case E:
                return getResources().getString(R.string.east);
            case ESE:
                return getResources().getString(R.string.east_south_east);
            case SE:
                return getResources().getString(R.string.south_east);
            case SSE:
                return getResources().getString(R.string.south_south_east);
            case S:
                return getResources().getString(R.string.south);
            case SSW:
                return getResources().getString(R.string.south_south_west);
            case SW:
                return getResources().getString(R.string.south_west);
            case WSW:
                return getResources().getString(R.string.west_south_west);
            case W:
                return getResources().getString(R.string.west);
            case WNW:
                return getResources().getString(R.string.west_north_west);
            case NW:
                return getResources().getString(R.string.north_west);
            case NNW:
                return getResources().getString(R.string.north_north_west);
            default:
                return "";
        }
    }

    private void updateSunInfo(Weather.Sun sun) {
        Date sunriseTime = WeatherUtils.convertToDate(sun.getSunrise());
        Date sunsetTime = WeatherUtils.convertToDate(sun.getSunset());
        mSunriseTextView.setText(getSunPositionStr(sunriseTime));
        mSunsetTextView.setText(getSunPositionStr(sunsetTime));
    }

    private String getSunPositionStr(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

    private void updateDateTime(int dataCalculation) {
        Date cityDate = WeatherUtils.convertToDate(dataCalculation);
        mDateTimeTextView.setText(getDateTimeStr(cityDate));
    }

    private String getDateTimeStr(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

    private void updateWeatherDescription(List<Weather.Description> descriptions) {
        StringBuilder totalDescription = new StringBuilder();
        for (Weather.Description description : descriptions) {
            totalDescription.append(description.getDescription());
            totalDescription.append(",");
        }
        totalDescription.deleteCharAt(totalDescription.length() - 1);
        mDescriptionTextView.setText(totalDescription);
    }

    private void updateTemperature(float temperature) {
        String temperatureUnit = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SettingsActivity.PREF_TEMPERATURE_UNITS, "");

        mTemperatureTextView.setText(getTemperatureStr(temperature, temperatureUnit));
    }

    private String getTemperatureStr(float temperature, String temperatureUnit) {

        String unitStr;
        if (temperatureUnit.equals(SettingsActivity.NAME_CELSIUS)) {
            temperature = WeatherUtils.convertToCelsius(temperature);
            unitStr = getResources().getString(R.string.celsius);
        }
        else {
            unitStr = getResources().getString(R.string.kelvin);
        }

        return String.format("%.0f %s", temperature, unitStr);
    }

    /**
     * Запрос на разрешение использовать геопозицию.
     */
    private void requestLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
        else {
            // Стартуем сразу сервис для получения информации о погоде.
            requestWeatherInfo();
        }
    }

}
