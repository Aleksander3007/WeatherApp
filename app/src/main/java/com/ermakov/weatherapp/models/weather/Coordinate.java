package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Географические координаты.
 */
public class Coordinate implements Parcelable {
    @SerializedName("lon")
    private float mLatitude;

    @SerializedName("lat")
    private float mLongitude;

    public Coordinate(float latitude, float longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    protected Coordinate(Parcel in) {
        mLatitude = in.readFloat();
        mLongitude = in.readFloat();
    }

    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mLatitude);
        dest.writeFloat(mLongitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            return mLongitude == ((Coordinate) obj).getLongitude() &&
                    mLatitude == ((Coordinate) obj).getLatitude();
        }
        else {
            return false;
        }
    }
}
