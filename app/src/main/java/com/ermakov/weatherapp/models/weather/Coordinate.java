package com.ermakov.weatherapp.models.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Географические координаты.
 */
public class Coordinate {
    @SerializedName("lon")
    private float mLatitude;

    @SerializedName("lat")
    private float mLongitude;

    /**
     * Широта.
     */
    public float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(float latitude) {
        mLatitude = latitude;
    }

    /**
     * Долгота.
     */
    public float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(float longitude) {
        mLongitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("{long, lat} = {%f, %f}", getLongitude(), getLatitude());
    }
}
