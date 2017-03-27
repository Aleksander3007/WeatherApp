package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CloudsTest {

    @Test
    public void testParcelable() {
        final int stubCloudiness = 1;
        Clouds writeClouds = new Clouds(stubCloudiness);

        Parcel parcel = Parcel.obtain();
        writeClouds.writeToParcel(parcel, 0);

        // После записи сбрасываем parcel для дальнейшего чтения.
        parcel.setDataPosition(0);

        Clouds readClouds = Clouds.CREATOR.createFromParcel(parcel);
        assertEquals(writeClouds, readClouds);
    }
}