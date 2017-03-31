package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Информация о ветре.
 */
public class Wind implements Parcelable {

    public enum Direction {
        N, // north
        NNE, // north-north-east
        NE, // north-east
        ENE, // east-north-east
        E, // east
        ESE, // east-south-east
        SE, // south-east
        SSE, // south-south-east
        S, // south
        SSW, // south-south-west
        SW, // south-west
        WSW, // west-south-west
        W, // west
        WNW, // west-north-west
        NW, // north-west
        NNW // north-north-west
    }

    /**
     * Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
     */
    @SerializedName("speed")
    private float mSpeed;

    /**
     * Wind direction, degrees (meteorological).
     */
    @SerializedName("deg")
    private float mDirectionDegrees;

    public Wind(float speed, float directionDegrees) {
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

    public float getDirectionDegrees() {
        return mDirectionDegrees;
    }

    @Override
    public String toString() {
        return String.format("{'speed':%f, 'deg':%f}",
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
        dest.writeFloat(mDirectionDegrees);
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

    /**
     * Конвертировать угол ветра в направление.
     * @return направление ветра.
     */
    public Wind.Direction getDirection() {

        mDirectionDegrees = mDirectionDegrees % 360;

        final float halfSegment = (90f / 8f);

        // Северное направление - особый сегмент.
        // Для северного направления получается что половина лежит после 0,
        // а другая половина до 360.
        if ((mDirectionDegrees >= 0 && mDirectionDegrees <= halfSegment) ||
                (mDirectionDegrees > 360 - halfSegment && mDirectionDegrees <= 360)) {
            return Direction.N;
        }

        int iSegment = 1;
        for (Direction direction : Direction.values()) {

            if (direction == Direction.N) continue;

            if (mDirectionDegrees > iSegment * halfSegment &&
                    mDirectionDegrees <= (iSegment + 2) * halfSegment) {
                return direction;
            }

            iSegment += 2;
        }

        // Сюда он не должен дойти, т.к. мы проверяем полный круг 360 градусов,
        // других значений просто быть не может.
        return null;
    }
}
