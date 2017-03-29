package com.ermakov.weatherapp.utils;

import java.text.SimpleDateFormat;
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
}
