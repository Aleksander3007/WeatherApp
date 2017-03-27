package com.ermakov.weatherapp.models.weather;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CoordinateTest {

    @Test
    public void testParcelable() {
        final int stubLatitude = 1;
        final int stubLongitude = 2;
        Coordinate writeCoordinate = new Coordinate(stubLatitude, stubLongitude);

        // Получение Parcel и запись parcelable-объекта туда:
        Parcel parcel = Parcel.obtain();
        writeCoordinate.writeToParcel(parcel, 0);

        // После записи сбрасываем parcel для дальнейшего чтения.
        parcel.setDataPosition(0);

        Coordinate readCoordinate = Coordinate.CREATOR.createFromParcel(parcel);
        assertEquals(writeCoordinate.getLatitude(), readCoordinate.getLatitude(), 0.1);
        assertEquals(writeCoordinate.getLongitude(), readCoordinate.getLongitude(), 0.1);
    }
}