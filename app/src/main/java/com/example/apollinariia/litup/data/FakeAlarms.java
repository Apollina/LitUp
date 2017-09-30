package com.example.apollinariia.litup.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.apollinariia.litup.data.AlarmContract.*;



public class FakeAlarms {
    public static void insertPresidentData (SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(AlarmEntry.ALARM_ACTIVE, "true");
        cv.put(AlarmEntry.ALARM_TIME, "9");
        cv.put(AlarmEntry.ALARM_NAME, "Alarm1");
        list.add(cv);


        try {
            db.beginTransaction();
            db.delete(AlarmEntry.TABLE_NAME, null, null);
            for (ContentValues c : list) {
                db.insert(AlarmEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //too bad :(
        } finally {
            db.endTransaction();
        }

    }
}
