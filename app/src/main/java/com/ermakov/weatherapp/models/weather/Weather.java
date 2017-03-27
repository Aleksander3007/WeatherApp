package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Информация о погоде.
 */
public class Weather implements Parcelable {

    @SerializedName("coord")
    private Coordinate mCityCoord;

    @SerializedName("weather")
    private List<Weather.Description> mDescriptions;

    @SerializedName("main")
    private Weather.MainCharacteristics mCharacteristics;

    @SerializedName("wind")
    private Wind mWind;

    @SerializedName("clouds")
    private Clouds mClouds;

    @SerializedName("rain")
    private Precipitation mRain;

    @SerializedName("snow")
    private Precipitation mSnow;

    @SerializedName("dt")
    private int mDataCalculation;

    @SerializedName("sys")
    private Sun mSun;

    // TODO: Первый раз запрашивать по координатам, или по имени, а второй раз из БД узнавать City ID.
    @SerializedName("id")
    private int mCityId;

    @SerializedName("name")
    private String mCityName;

    public Weather() {}

    protected Weather(Parcel in) {
        mCityCoord = in.readParcelable(Coordinate.class.getClassLoader());
        mDescriptions = in.readArrayList(Weather.Description.class.getClassLoader());
        mCharacteristics = in.readParcelable(Weather.MainCharacteristics.class.getClassLoader());
        mWind = in.readParcelable(Wind.class.getClassLoader());
        mClouds = in.readParcelable(Clouds.class.getClassLoader());
        mRain = in.readParcelable(Precipitation.class.getClassLoader());
        mSnow = in.readParcelable(Precipitation.class.getClassLoader());
        mDataCalculation = in.readInt();
        mSun = in.readParcelable(Sun.class.getClassLoader());
        mCityId = in.readInt();
        mCityName = in.readString();

    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getCityCoord(), flags);
        dest.writeList(getDescriptions());
        dest.writeParcelable(getCharacteristics(), flags);
        dest.writeParcelable(getWind(), flags);
        dest.writeParcelable(getClouds(), flags);
        dest.writeParcelable(getRain(), flags);
        dest.writeParcelable(getSnow(), flags);
        dest.writeInt(getDataCalculation());
        dest.writeParcelable(getSun(), flags);
        dest.writeInt(getCityId());
        dest.writeString(getCityName());
    }

    /**
     * Географические координаты города.
     */
    public Coordinate getCityCoord() {
        return mCityCoord;
    }

    /**
     * more info Weather condition codes.
     */
    public List<Weather.Description> getDescriptions() {
        return mDescriptions;
    }

    @Override
    public String toString() {
        StringBuilder weatherString = new StringBuilder("");
        weatherString.append(getCityCoord().toString());
        weatherString.append("\n");

        weatherString.append("[");
        for (Weather.Description description : getDescriptions()) {
            weatherString.append(description.toString());
        }
        weatherString.append("]");
        weatherString.append("\n");

        weatherString.append("'main':");
        weatherString.append(getCharacteristics().toString());
        weatherString.append("\n");

        weatherString.append("'wind':");
        weatherString.append(getWind().toString());
        weatherString.append("\n");

        weatherString.append("'clouds':");
        weatherString.append(getClouds().toString());
        weatherString.append("\n");

        if (getRain() != null) {
            weatherString.append("'rain':");
            weatherString.append(getRain().toString());
            weatherString.append("\n");
        }

        if (getSnow() != null) {
            weatherString.append("'snow':");
            weatherString.append(getRain().toString());
            weatherString.append("\n");
        }

        weatherString.append("'dt':");
        weatherString.append(getDataCalculation());
        weatherString.append("\n");

        weatherString.append("'sys':");
        weatherString.append(getSun().toString());
        weatherString.append("\n");

        weatherString.append("'id':");
        weatherString.append(getCityId());
        weatherString.append("\n");

        weatherString.append("'name':");
        weatherString.append(getCityName());
        weatherString.append("\n");

        return weatherString.toString();
    }

    public void setCityCoord(Coordinate cityCoord) {
        mCityCoord = cityCoord;
    }

    public void setDescriptions(List<Description> descriptions) {
        mDescriptions = descriptions;
    }

    public MainCharacteristics getCharacteristics() {
        return mCharacteristics;
    }

    public void setCharacteristics(MainCharacteristics characteristics) {
        mCharacteristics = characteristics;
    }

    public Wind getWind() {
        return mWind;
    }

    public void setWind(Wind wind) {
        mWind = wind;
    }

    public Clouds getClouds() {
        return mClouds;
    }

    public void setClouds(Clouds clouds) {
        mClouds = clouds;
    }

    public Precipitation getRain() {
        return mRain;
    }

    public void setRain(Precipitation rain) {
        mRain = rain;
    }

    public Precipitation getSnow() {
        return mSnow;
    }

    public void setSnow(Precipitation snow) {
        mSnow = snow;
    }

    /**
     * Time of data calculation, unix, UTC.
     */
    public int getDataCalculation() {
        return mDataCalculation;
    }

    public void setDataCalculation(int dataCalculation) {
        mDataCalculation = dataCalculation;
    }

    public Sun getSun() {
        return mSun;
    }

    public void setSun(Sun sun) {
        mSun = sun;
    }

    /**
     * City ID.
     */
    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        mCityId = cityId;
    }

    /**
     * City name.
     */
    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    /**
     * Информация о погодных условиях.
     */
    public static class Description implements Parcelable {
        @SerializedName("id")
        private int mId;

        @SerializedName("main")
        private String mMain;

        @SerializedName("description")
        private String mDescription;

        @SerializedName("icon")
        private String mIconId;

        public Description(int id, String main, String description, String iconId) {
            this.mId = id;
            this.mMain = main;
            this.mDescription = description;
            this.mIconId = iconId;
        }

        protected Description(Parcel in) {
            mId = in.readInt();
            mMain = in.readString();
            mDescription = in.readString();
            mIconId = in.readString();
        }

        public static final Creator<Description> CREATOR = new Creator<Description>() {
            @Override
            public Description createFromParcel(Parcel in) {
                return new Description(in);
            }

            @Override
            public Description[] newArray(int size) {
                return new Description[size];
            }
        };

        /**
         * Weather condition id.
         */
        public int getId() {
            return mId;
        }

        /**
         * Group of weather parameters (Precipitation, Snow, Extreme etc.).
         */
        public String getMain() {
            return mMain;
        }

        /**
         * Weather condition within the group.
         */
        public String getDescription() {
            return mDescription;
        }

        /**
         * Weather icon id.
         */
        public String getIconId() {
            return mIconId;
        }

        @Override
        public String toString() {
            return String.format("{'id':%d, 'main':%s, 'description':%s, 'icon':%s}",
                    getId(),
                    getMain(),
                    getDescription(),
                    getIconId());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeString(mMain);
            dest.writeString(mDescription);
            dest.writeString(mIconId);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Weather.Description) {
                return mId == ((Description) obj).getId() &&
                        mMain.equals(((Description) obj).getMain()) &&
                        mDescription.equals(((Description) obj).getDescription()) &&
                        mIconId.equals(((Description) obj).getIconId());
            }
            else {
                return false;
            }
        }
    }

    /**
     * Главные характеристики погоды.
     */
    public static class MainCharacteristics implements Parcelable {
        @SerializedName("temp")
        private float mTemperature;

        @SerializedName("pressure")
        private int mPressure;

        @SerializedName("humidity")
        private int mHumidity;

        @SerializedName("temp_min")
        private float mTemperatureMin;

        @SerializedName("temp_max")
        private float mTemperatureMax;

        @SerializedName("sea_level")
        private int mPressureSeaLevel;

        @SerializedName("grnd_level")
        private int mPressureGroundLevel;

        public MainCharacteristics() {}

        protected MainCharacteristics(Parcel in) {
            setTemperature(in.readFloat());
            setPressure(in.readInt());
            setHumidity(in.readInt());
            setTemperatureMin(in.readFloat());
            setTemperatureMax(in.readFloat());
            setPressureSeaLevel(in.readInt());
            setPressureGroundLevel(in.readInt());
        }

        public static final Creator<MainCharacteristics> CREATOR = new Creator<MainCharacteristics>() {
            @Override
            public MainCharacteristics createFromParcel(Parcel in) {
                return new MainCharacteristics(in);
            }

            @Override
            public MainCharacteristics[] newArray(int size) {
                return new MainCharacteristics[size];
            }
        };

        @Override
        public String toString() {
            return String.format("{'temp':%f, 'pressure':%d, 'humidity':%d}",
                    getTemperature(),
                    getPressure(),
                    getHumidity());
        }

        /**
         * Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        public float getTemperature() {
            return mTemperature;
        }

        /**
         * Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa.
         */
        public int getPressure() {
            return mPressure;
        }

        /**
         * Humidity, %.
         */
        public int getHumidity() {
            return mHumidity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(getTemperature());
            dest.writeInt(getPressure());
            dest.writeInt(getHumidity());
            dest.writeFloat(getTemperatureMin());
            dest.writeFloat(getTemperatureMax());
            dest.writeInt(getPressureSeaLevel());
            dest.writeInt(getPressureGroundLevel());
        }

        public void setTemperature(float temperature) {
            mTemperature = temperature;
        }

        public void setPressure(int pressure) {
            mPressure = pressure;
        }

        public void setHumidity(int humidity) {
            mHumidity = humidity;
        }

        /**
         * Minimum temperature at the moment.
         * This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally).
         * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        public float getTemperatureMin() {
            return mTemperatureMin;
        }

        public void setTemperatureMin(float temperatureMin) {
            mTemperatureMin = temperatureMin;
        }

        /**
         * Maximum temperature at the moment.
         * This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally).
         * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        public float getTemperatureMax() {
            return mTemperatureMax;
        }

        public void setTemperatureMax(float temperatureMax) {
            mTemperatureMax = temperatureMax;
        }

        /**
         * Atmospheric pressure on the sea level, hPa.
         */
        public int getPressureSeaLevel() {
            return mPressureSeaLevel;
        }

        public void setPressureSeaLevel(int pressureSeaLevel) {
            mPressureSeaLevel = pressureSeaLevel;
        }

        /**
         * Atmospheric pressure on the ground level, hPa.
         */
        public int getPressureGroundLevel() {
            return mPressureGroundLevel;
        }

        public void setPressureGroundLevel(int pressureGroundLevel) {
            mPressureGroundLevel = pressureGroundLevel;
        }
    }

    public static class Sun implements Parcelable {
        @SerializedName("sunrise")
        private int mSunrise;

        @SerializedName("sunset")
        private int mSunset;

        public Sun(int sunrise, int sunset) {
            this.mSunrise = sunrise;
            this.mSunset = sunset;
        }

        protected Sun(Parcel in) {
            mSunrise = in.readInt();
            mSunset = in.readInt();
        }

        public static final Creator<Sun> CREATOR = new Creator<Sun>() {
            @Override
            public Sun createFromParcel(Parcel in) {
                return new Sun(in);
            }

            @Override
            public Sun[] newArray(int size) {
                return new Sun[size];
            }
        };

        @Override
        public String toString() {
            return String.format("{'mSunrise':%d, 'mSunset':%d}",
                    getSunrise(),
                    getSunset());
        }

        /**
         * Sunrise time, unix, UTCs
         */
        public int getSunrise() {
            return mSunrise;
        }

        /**
         * Sunset time, unix, UTC
         */
        public int getSunset() {
            return mSunset;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mSunrise);
            dest.writeInt(mSunset);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Weather.Sun) {
                return mSunrise == ((Sun) obj).getSunrise() &&
                        mSunset == ((Sun) obj).getSunset();
            }
            else {
                return false;
            }
        }
    }
}
