package com.example.apollinariia.litup;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Apollinariia on 9/27/2017.
 */

public class Parcelables {
    public static byte[] toByteArray(Parcelable parcelable) {
    Parcel parcel = Parcel.obtain();

    parcelable.writeToParcel(parcel, 0);

    byte[] result = parcel.marshall();

    parcel.recycle();

    return (result);
}

        public static Alarm toParcelableAlarm(byte[] bytes) {
            Alarm alarm = null;

            try {
                alarm = toParcelable(bytes, Alarm.CREATOR);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return alarm;
        }

        private static <T> T toParcelable(byte[] bytes,
                                          Parcelable.Creator<T> creator) throws Exception {
            final Parcel parcel = Parcel.obtain();

            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);

            final T result = creator.createFromParcel(parcel);

            parcel.recycle();

            return (result);
        }
}

