package com.ermakov.weatherapp.models.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Информация о ветре.
 */
public class Wind {

    /**
     * Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
     */
    @SerializedName("speed")
    private float mSpeed;

    /**
     * Wind direction, degrees (meteorological).
     */
    @SerializedName("deg")
    private int mDirectionDegrees;

    public float getSpeed() {
        return mSpeed;
    }

    public int getDirectionDegrees() {
        return mDirectionDegrees;
    }

    @Override
    public String toString() {
        return String.format("{'speed':%f, 'deg':%d}",
                getSpeed(),
                getDirectionDegrees());
    }
}
