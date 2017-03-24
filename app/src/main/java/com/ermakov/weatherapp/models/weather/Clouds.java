package com.ermakov.weatherapp.models.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Информация об облаках.
 */
public class Clouds {
    /**
     * Cloudiness, %.
     */
    @SerializedName("all")
    private int mValue;

    @Override
    public String toString() {
        return String.format("{'all':%d}", mValue);
    }
}
