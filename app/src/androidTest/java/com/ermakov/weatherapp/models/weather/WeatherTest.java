package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class WeatherTest {

    @Test
    public void testParcelable() {

        final Coordinate stubCoord = new Coordinate(1, 2);
        List<Weather.Description> stubDescriptions = createStubDescriptions();
        final Weather.MainCharacteristics stubCharacteristics = createStubCharacteristics();
        final Wind stubWind = new Wind(3, 4);
        final Clouds stubClouds = new Clouds(5);
        final Precipitation stubRain = new Precipitation(6);
        final Precipitation stubSnow = new Precipitation(7);
        final int stubDataCalc = 8;
        final Weather.Sun stubSun = new Weather.Sun(9, 10);
        final int stubCityId = 11;
        final String stubCityName = "stubCityName";


        Weather writeWeather = new Weather();
        writeWeather.setCityCoord(stubCoord);
        writeWeather.setDescriptions(stubDescriptions);
        writeWeather.setCharacteristics(stubCharacteristics);
        writeWeather.setWind(stubWind);
        writeWeather.setClouds(stubClouds);
        writeWeather.setRain(stubRain);
        writeWeather.setSnow(stubSnow);
        writeWeather.setDataCalculation(stubDataCalc);
        writeWeather.setSun(stubSun);
        writeWeather.setCityId(stubCityId);
        writeWeather.setCityName(stubCityName);

        Parcel parcel = Parcel.obtain();
        writeWeather.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        Weather readWeather = Weather.CREATOR.createFromParcel(parcel);

        assertNotNull(readWeather);

        assertEquals(stubCoord, readWeather.getCityCoord());
        assertNotNull(stubDescriptions);
        assertEquals(stubDescriptions.get(0), readWeather.getDescriptions().get(0));
        assertMainCharacteristics(stubCharacteristics, readWeather.getCharacteristics());
        assertEquals(stubWind, readWeather.getWind());

        assertEquals(stubClouds, readWeather.getClouds());
        assertEquals(stubRain, readWeather.getRain());
        assertEquals(stubSnow, readWeather.getSnow());
        assertEquals(stubDataCalc, readWeather.getDataCalculation());
        assertEquals(stubSun, readWeather.getSun());
        assertEquals(stubCityId, readWeather.getCityId());
        assertEquals(stubCityName, readWeather.getCityName());
    }

    private List<Weather.Description> createStubDescriptions() {
        List<Weather.Description> stubDescriptions = new ArrayList<>();
        stubDescriptions.add(new Weather.Description(1, "stubMain", "stubDescr", "stubIcon"));
        return stubDescriptions;
    }

    private Weather.MainCharacteristics createStubCharacteristics() {
        final Weather.MainCharacteristics stubCharacteristics = new Weather.MainCharacteristics();
        stubCharacteristics.setTemperature(1);
        stubCharacteristics.setPressure(2);
        stubCharacteristics.setHumidity(3);
        stubCharacteristics.setTemperatureMin(4);
        stubCharacteristics.setTemperatureMax(5);
        stubCharacteristics.setPressureSeaLevel(6);
        stubCharacteristics.setPressureGroundLevel(7);
        return stubCharacteristics;
    }

    private void assertMainCharacteristics(Weather.MainCharacteristics expected,
                                           Weather.MainCharacteristics actual) {
        assertEquals(expected.getTemperature(), actual.getTemperature(), 0.1);
        assertEquals(expected.getPressure(), actual.getPressure());
        assertEquals(expected.getHumidity(), actual.getHumidity());
        assertEquals(expected.getTemperatureMin(), actual.getTemperatureMin(), 0.1);
        assertEquals(expected.getTemperatureMax(), actual.getTemperatureMax(), 0.1);
        assertEquals(expected.getPressureGroundLevel(), actual.getPressureGroundLevel());
        assertEquals(expected.getPressureSeaLevel(), actual.getPressureSeaLevel());
    }
}