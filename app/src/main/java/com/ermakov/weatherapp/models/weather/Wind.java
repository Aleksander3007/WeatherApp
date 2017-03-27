package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Информация о ветре.
 */
public class Wind implements Parcelable {

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

    public Wind(float speed, int directionDegrees) {
        this.mSpeed = speed;
        this.mDirectionDegrees = directionDegrees;
    }

    protected Wind(Parcel in) {
        mSpeed = in.readFloat();
        mDirectionDegrees = in.readInt();
    }

    public static final Creator<Wind> CREATOR = new Creator<Wind>() {
        @Override
        public Wind createFromParcel(Parcel in) {
            return new Wind(in);
        }

        @Override
        public Wind[] newArray(int size) {
            return new Wind[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mSpeed);
        dest.writeInt(mDirectionDegrees);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Wind) {
            return  mSpeed == ((Wind) obj).getSpeed() &&
                    mDirectionDegrees == ((Wind) obj).getDirectionDegrees();
        }
        else {
            return false;
        }
    }
}
