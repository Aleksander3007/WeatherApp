package com.ermakov.weatherapp.models.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Информация о погоде.
 */
public class Weather {

    @SerializedName("coord")
    private Coordinate mCityCoord;

    /**
     * more info Weather condition codes.
     */
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

    /**
     * Time of data calculation, unix, UTC.
     */
    @SerializedName("dt")
    private int mDataCalculation;

    @SerializedName("sys")
    private Sun mSun;

    // TODO: Первый раз запрашивать по координатам, или по имени, а второй раз из БД узнавать City ID.
    /**
     * City ID.
     */
    @SerializedName("id")
    private int mCityId;

    /**
     * City name.
     */
    @SerializedName("name")
    private String mCityName;

    /**
     * Географические координаты города.
     */
    public Coordinate getCityCoord() {
        return mCityCoord;
    }

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
        weatherString.append(mCharacteristics.toString());
        weatherString.append("\n");

        weatherString.append("'wind':");
        weatherString.append(mWind.toString());
        weatherString.append("\n");

        weatherString.append("'clouds':");
        weatherString.append(mClouds.toString());
        weatherString.append("\n");

        if (mRain != null) {
            weatherString.append("'rain':");
            weatherString.append(mRain.toString());
            weatherString.append("\n");
        }

        if (mSnow != null) {
            weatherString.append("'snow':");
            weatherString.append(mRain.toString());
            weatherString.append("\n");
        }

        weatherString.append("'dt':");
        weatherString.append(mDataCalculation);
        weatherString.append("\n");

        weatherString.append("'sys':");
        weatherString.append(mSun.toString());
        weatherString.append("\n");

        weatherString.append("'id':");
        weatherString.append(mCityId);
        weatherString.append("\n");

        weatherString.append("'name':");
        weatherString.append(mCityName);
        weatherString.append("\n");

        return weatherString.toString();
    }

    /**
     * Информация о погодных условиях.
     */
    public static class Description {
        @SerializedName("id")
        private int mId;

        @SerializedName("main")

        private String mMain;
        @SerializedName("description")
        private String mDescription;

        @SerializedName("icon")
        private String mIconId;

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
    }

    public static class MainCharacteristics {
        @SerializedName("temp")
        private float mTemperature;

        @SerializedName("pressure")
        private int mPressure;

        @SerializedName("humidity")
        private int mHumidity;

        /**
         * Minimum temperature at the moment.
         * This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally).
         * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        @SerializedName("temp_min")
        private float mTemperatureMin;

        /**
         * Maximum temperature at the moment.
         * This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally).
         * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        @SerializedName("temp_max")
        private float mTemperatureMax;

        /**
         * Atmospheric pressure on the sea level, hPa.
         */
        @SerializedName("sea_level")
        private int mPressureSeaLevel;

        /**
         * Atmospheric pressure on the ground level, hPa.
         */
        @SerializedName("grnd_level")
        private int mPressureGroundLevel;

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
    }

    public static class Sun {
        @SerializedName("sunrise")
        private int sunrise;

        @SerializedName("sunset")
        private int sunset;

        @Override
        public String toString() {
            return String.format("{'sunrise':%d, 'sunset':%d}",
                    getSunrise(),
                    getSunset());
        }

        /**
         * Sunrise time, unix, UTCs
         */
        public int getSunrise() {
            return sunrise;
        }

        /**
         * Sunset time, unix, UTC
         */
        public int getSunset() {
            return sunset;
        }
    }
}
