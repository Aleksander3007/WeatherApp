package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Информация о прогнозе погоды.
 */
public class Forecast implements Parcelable {

    @SerializedName("city")
    private Forecast.City mCity;

    @SerializedName("list")
    private List<Weather> mWeatherList;

    protected Forecast(Parcel in) {
        mCity = in.readParcelable(City.class.getClassLoader());
        mWeatherList = in.createTypedArrayList(Weather.CREATOR);
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public City getCity() {
        return mCity;
    }

    public void setCity(City city) {
        mCity = city;
    }

    public List<Weather> getWeatherList() {
        return mWeatherList;
    }

    public void setWeatherList(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mCity, flags);
        dest.writeTypedList(mWeatherList);
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("'city': ");
        if (mCity != null) stringBuilder.append(getCity().toString());
        stringBuilder.append("\n");

        stringBuilder.append("'list': ");
        if (mWeatherList != null) {
            for (Weather weather : mWeatherList) stringBuilder.append(weather.toString());
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public static class City implements Parcelable {

        @SerializedName("id")
        private int mId;

        @SerializedName("name")
        private String mName;

        @SerializedName("coord")
        private Coordinate mCoordinate;

        @SerializedName("country")
        private String mCountryCode;

        protected City(Parcel in) {
            mId = in.readInt();
            mName = in.readString();
            mCoordinate = in.readParcelable(Coordinate.class.getClassLoader());
            mCountryCode = in.readString();
        }

        public static final Creator<City> CREATOR = new Creator<City>() {
            @Override
            public City createFromParcel(Parcel in) {
                return new City(in);
            }

            @Override
            public City[] newArray(int size) {
                return new City[size];
            }
        };

        /**
         * Id города.
         */
        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        /**
         * Имя города.
         */
        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public Coordinate getCoordinate() {
            return mCoordinate;
        }

        public void setCoordinate(Coordinate coordinate) {
            mCoordinate = coordinate;
        }

        public String getCountryCode() {
            return mCountryCode;
        }

        public void setCountryCode(String countryCode) {
            mCountryCode = countryCode;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeString(mName);
            dest.writeParcelable(mCoordinate, flags);
            dest.writeString(mCountryCode);
        }

        @Override
        public String toString() {
            return String.format("{'id': %d, 'name': %s, 'coord': %s}",
                    getId(),
                    getName(),
                    getCoordinate().toString()
            );
        }
    }
}
