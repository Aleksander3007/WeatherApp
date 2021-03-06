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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ermakov.weatherapp.activities.SettingsActivity;
import com.ermakov.weatherapp.adapters.ForecastAdapter;
import com.ermakov.weatherapp.models.weather.Forecast;
import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.models.weather.Wind;
import com.ermakov.weatherapp.utils.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @BindView(R.id.rv_forecast) RecyclerView mForecastRecyclerView;

    private BroadcastReceiver mWeatherReceiver;
    private AlarmManager mWeatherAlarmManager;
    private PendingIntent mWeatherUpdatePendingIntent;
    private PendingIntent mWeatherNotifPendingIntent;

    private List<Weather> mForecastList = new ArrayList<>();

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

        ForecastAdapter forecastAdapter = new ForecastAdapter(this, mForecastList);
        mForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mForecastRecyclerView.setAdapter(forecastAdapter);

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
        mMainSwipeRefreshLayout.setRefreshing(true);
        startService(createWeatherUpdateIntent());
        mLastWeatherUpdate = SystemClock.elapsedRealtime();
    }

    /**
     * Создать Intent для обновления погоды.
     */
    private Intent createWeatherUpdateIntent() {

        Intent intent = new Intent(this, WeatherUpdateService.class);
        intent.putExtra(WeatherUpdateService.EXTRA_REQUEST_FORECAST, true);
        intent.setAction(WeatherUpdateService.ACTION_GET_WEATHER_DATA);

        return intent;
    }

    /**
     * Запуск будильника обновления данных о погоде через определенные интервалы.
     */
    private void startWeatherUpdateAlarm() {
        mWeatherUpdatePendingIntent
                = PendingIntent.getService(this, 0, createWeatherUpdateIntent(), 0);
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
                        Forecast forecast = intent.getParcelableExtra(WeatherUpdateService.EXTRA_FORECAST);

                        Log.d(TAG, "onReceive");

                        if (weather != null) updateView(weather);
                        if (forecast != null) {
                            // Формируем прогноз в необходимый нам вид.
                            List<Weather> formatForecast = formatForecast(forecast.getWeatherList());
                            Log.d(TAG, "forecast != null");
                            Log.d(TAG, "getWeatherList.size = " + forecast.getWeatherList().size());
                            mForecastList.clear();
                            mForecastList.addAll(formatForecast);
                            mForecastRecyclerView.getAdapter().notifyDataSetChanged();
                        }

                        Log.d(TAG, "Weather is updated.");
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

    /**
     * Привести прогноз погоды в необходимый формат для отображения.
     */
    private List<Weather> formatForecast(List<Weather> weatherList)
    {
        if (weatherList == null || weatherList.size() == 0) return null;

        // Определяем текущий день месяца.
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Группируем прогноз погоды по дням.
        Map<Integer, List<Weather>> daysForecast = groupForecastByDays(weatherList);

        // Формируем в необходимом формате прогноз.
        List<Weather> formatForecast = new ArrayList<>();
        for (Integer day : daysForecast.keySet()) {
            if (day == today) {
                formatForecast.addAll(daysForecast.get(day));
            }
            else {
                formatForecast.add(createForecastForDay(daysForecast.get(day)));
            }
        }

        return formatForecast;
    }

    /**
     * Сформировать прогноз погоды на день по данным почасового прогноза этого дня.
     * @param hoursForecast почасовой прогноз определенного дня.
     * @return прогноз погоды на день.
     */
    private Weather createForecastForDay(List<Weather> hoursForecast)
    {
        int dateCalcMin = hoursForecast.get(0).getDataCalculation();
        float minTemperature = hoursForecast.get(0).getCharacteristics().getTemperatureMin();
        float maxTemperature = hoursForecast.get(0).getCharacteristics().getTemperatureMax();
        Map<String, Integer> icons = new HashMap<>();
        for (Weather hourWeather : hoursForecast) {
            if (hourWeather.getCharacteristics().getTemperatureMin() <  minTemperature) {
                minTemperature = hourWeather.getCharacteristics().getTemperatureMin();
            }
            if (hourWeather.getCharacteristics().getTemperatureMax() > maxTemperature) {
                maxTemperature = hourWeather.getCharacteristics().getTemperatureMax();
            }
            if (hourWeather.getDataCalculation() < dateCalcMin) {
                dateCalcMin = hourWeather.getDataCalculation();
            }

            // Считаем количество каждого типа iconId.
            for (Weather.Description description : hourWeather.getDescriptions()) {
                String iconId =  description.getIconId();
                Integer frequency = icons.get(iconId);
                icons.put(iconId, (frequency == null) ? 1 : frequency);
            }
        }

        // Ищем самою распространенную иконку.
        Map.Entry<String, Integer> widelySpreadIcon = null;
        for (Map.Entry<String, Integer> icon : icons.entrySet()) {
            if (widelySpreadIcon == null ||
                    icon.getValue().compareTo(widelySpreadIcon.getValue()) > 0) {
                widelySpreadIcon = icon;
            }
        }

        // TODO: На самом деле необходимо все поля сформировать верно.
        Weather dayWeather = new Weather();

        dayWeather.setDataCalculation(dateCalcMin);

        Weather.MainCharacteristics  weatherCharacteristics = new Weather.MainCharacteristics();
        weatherCharacteristics.setTemperatureMin(minTemperature);
        weatherCharacteristics.setTemperatureMax(maxTemperature);
        dayWeather.setCharacteristics(weatherCharacteristics);

        Weather.Description weatherDescription = new Weather.Description();
        weatherDescription.setIconId(widelySpreadIcon.getKey());
        dayWeather.setDescriptions(new ArrayList<Weather.Description>());
        dayWeather.getDescriptions().add(weatherDescription);

        return dayWeather;
    }

    private Map<Integer, List<Weather>> groupForecastByDays(List<Weather> weatherList) {

        Calendar calendar = Calendar.getInstance();

        /** Подневной прогноз погоды. */
        Map<Integer, List<Weather>> daysForecast = new LinkedHashMap<>();
        // Группируем прогноз погоды по дням.
        for (Weather weather : weatherList) {
            Date weatherDate = WeatherUtils.convertToDate(weather.getDataCalculation());
            int currentDay = WeatherUtils.getDayOfMoth(calendar, weatherDate);

            if (!daysForecast.containsKey(currentDay)) {
                List<Weather> hoursForecast = new ArrayList<>();
                daysForecast.put(currentDay, hoursForecast);
            }
            daysForecast.get(currentDay).add(weather);
        }

        return daysForecast;
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
        simpleDateFormat.setTimeZone(WeatherUtils.getLocalTimeZone());
        String formattedDate = simpleDateFormat.format(date);

        return formattedDate;
    }

    private void updateDateTime(int dataCalculation) {
        Date cityDate = WeatherUtils.convertToDate(dataCalculation);
        mDateTimeTextView.setText(getDateTimeStr(cityDate));
    }

    private String getDateTimeStr(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM HH:mm");
        simpleDateFormat.setTimeZone(WeatherUtils.getLocalTimeZone());
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

        mTemperatureTextView.setText(
                WeatherUtils.getTemperatureStr(this, temperature, temperatureUnit));
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
