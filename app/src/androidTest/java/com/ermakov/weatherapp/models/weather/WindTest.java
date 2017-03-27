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
}