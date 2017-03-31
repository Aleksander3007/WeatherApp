package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Информация об атмосферных осадках (о дожде или снеге).
 */
public class Precipitation implements Parcelable {
    /**
     * Precipitation volume for the last 3 hours.
     */
    @SerializedName("3h")
    private float mVolume3h;

    public Precipitation(float volume3h) {
        this.mVolume3h = volume3h;
    }

    protected Precipitation(Parcel in) {
        mVolume3h = in.readFloat();
    }

    public static final Creator<Precipitation> CREATOR = new Creator<Precipitation>() {
        @Override
        public Precipitation createFromParcel(Parcel in) {
            return new Precipitation(in);
        }

        @Override
        public Precipitation[] newArray(int size) {
            return new Precipitation[size];
        }
    };

    public float getVolume3h() {
        return mVolume3h;
    }

    @Override
    public String toString() {
        return String.format("'3h':%f", getVolume3h());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mVolume3h);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return mVolume3h == ((Precipitation) obj).getVolume3h();
        }
        catch (ClassCastException ccex) {
            return false;
        }
    }
}
