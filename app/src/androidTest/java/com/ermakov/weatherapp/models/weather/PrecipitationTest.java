package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PrecipitationTest {

    @Test
    public void testParcelable() {
        final int stubVolume3h = 1;
        Precipitation writePrecipitation = new Precipitation(stubVolume3h);

        Parcel parcel = Parcel.obtain();
        writePrecipitation.writeToParcel(parcel, 0);

        // После записи сбрасываем parcel для дальнейшего чтения.
        parcel.setDataPosition(0);

        Precipitation readClouds = Precipitation.CREATOR.createFromParcel(parcel);
        assertEquals(writePrecipitation, readClouds);
    }
}