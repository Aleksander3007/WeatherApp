package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Информация об облаках.
 */
public class Clouds implements Parcelable {
    /**
     * Cloudiness, %.
     */
    @SerializedName("all")
    private int mValue;

    public Clouds(int cloudiness) {
        this.mValue = cloudiness;
    }

    public int getValue() {
        return mValue;
    }

    protected Clouds(Parcel in) {
        mValue = in.readInt();
    }

    public static final Creator<Clouds> CREATOR = new Creator<Clouds>() {
        @Override
        public Clouds createFromParcel(Parcel in) {
            return new Clouds(in);
        }

        @Override
        public Clouds[] newArray(int size) {
            return new Clouds[size];
        }
    };

    @Override
    public String toString() {
        return String.format("{'all':%d}", mValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mValue);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return mValue == ((Clouds) obj).getValue();
        }
        catch (ClassCastException ccex) {
            return false;
        }
    }
}
