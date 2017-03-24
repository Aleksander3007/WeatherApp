package com.ermakov.weatherapp.models.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Информация об атмосферных осадках (о дожде или снеге).
 */
public class Precipitation {
    /**
     * Precipitation volume for the last 3 hours.
     */
    @SerializedName("3h")
    private int mVolume3h;

    public int getVolume3h() {
        return mVolume3h;
    }

    @Override
    public String toString() {
        return String.format("'3h':%d", getVolume3h());
    }
}
