package com.ermakov.weatherapp.net;

import com.ermakov.weatherapp.models.weather.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Интерфейс, объявляющий методы для работы с OpenWeatherMap API.
 */
public interface WeatherApi {

    /**
     * Получить информацию о погоде по имени города.
     * @param cityName city name and country code divided by comma, use ISO 3166 country codes.
     */
    @GET("/data/2.5/weather")
    Call<Weather> getWeatherByCityName(
            @Query("q") String cityName
    );

    /**
     * Получить информацию о погоде по географическим координатам.
     * @param latitude широта.
     * @param longitude долгота.
     * @return
     */
    @GET("/data/2.5/weather")
    Call<Weather> getWeatherByGeoCoordinates(
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );
}
