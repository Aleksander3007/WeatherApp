package com.ermakov.weatherapp.utils;

import android.content.Context;

import com.ermakov.weatherapp.R;
import com.ermakov.weatherapp.activities.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Вспомогательные методы для работы с данными о погоде.
 */
public class WeatherUtils {

    /**
     * Конвертировать величину из Кельвинов в Цельсии.
     * @param kelvin величина в Кельвинах.
     * @return величина в градусах Цельсия.
     */
    public static float convertToCelsius(float kelvin) {
        return kelvin - 273.15f;
    }

    /**
     * Конвертировать в DateTime из UNIX-time.
     * @return
     */
    public static Date convertToDate(int unixTimeStamp) {
        // * 1000L - конвертируем в миллисекунды.
        Date date = new Date(unixTimeStamp * 1000L);
        return date;
    }

    /**
     * Конвертировать из м/с в км/ч.
     * @param meterPerSecond величина в м/с.
     * @return величина в км/ч.
     */
    public static float convertToKmPerHour(float meterPerSecond) {
        // k м/с = k * 3600 / 1000 км/ч.
        return meterPerSecond * 3.6f;
    }

    public static String getTemperatureStr(Context context, float temperature, String temperatureUnit) {

        String unitStr;
        if (temperatureUnit.equals(SettingsActivity.NAME_CELSIUS)) {
            temperature = WeatherUtils.convertToCelsius(temperature);
            unitStr = context.getResources().getString(R.string.celsius);
        }
        else {
            unitStr = context.getResources().getString(R.string.kelvin);
        }

        return String.format("%.0f %s", temperature, unitStr);
    }

    /**
     *  Получить локальную временную зону.
     */
    public static TimeZone getLocalTimeZone() {
        return Calendar.getInstance().getTimeZone();
    }

    public static int getDayOfMoth(Calendar calendar, Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}
