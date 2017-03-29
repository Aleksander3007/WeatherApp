package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class WindTest {

    @Test
    public void testParcelable() {
        final float stubSpeed = 1;
        final int stubDirectionDegrees = 2;
        Wind writeWind = new Wind(stubSpeed, stubDirectionDegrees);

        Parcel parcel = Parcel.obtain();
        writeWind.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Wind readWind = Wind.CREATOR.createFromParcel(parcel);
        assertEquals(writeWind, readWind);
    }

    @Test
    public void testConvertToDirection() {

        final float stubSpeed = 0;

        // Точная проверка для сегмента north-east.
        float step = 90f / 8f;
        float currentDegrees = 90f / 16f;
        StringBuilder windDirectionStr = new StringBuilder();
        while (currentDegrees <= 90f) {
            Wind wind = new Wind(stubSpeed, (int)currentDegrees);
            windDirectionStr.append(wind.getDirection().toString());
            windDirectionStr.append("-");
            currentDegrees += step;
        }
        assertEquals("N-NNE-NNE-NE-NE-ENE-ENE-E-", windDirectionStr.toString());

        // Проверка по основным направлениям.
        step = 90f / 4f;
        currentDegrees = 0;
        windDirectionStr = new StringBuilder();
        while (currentDegrees <= 360f) {
            Wind wind = new Wind(stubSpeed, (int)currentDegrees);
            windDirectionStr.append(wind.getDirection().toString());
            windDirectionStr.append("-");
            currentDegrees += step;
        }
        assertEquals("N-NNE-NE-ENE-E-ESE-SE-SSE-S-SSW-SW-WSW-W-WNW-NW-NNW-N-", windDirectionStr.toString());
    }
}