package com.example.apollinariia.litup.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.widget.Toast;


import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.example.apollinariia.litup.R;
import com.example.apollinariia.litup.services.AlarmReciever;
import com.example.apollinariia.litup.services.AlarmReciever.*;
import com.example.apollinariia.litup.services.BootReciever;




public class AlarmContract {

    public static final String AUTHORITY = "com.example.apollinariia.litup";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ALARMS = "alarm";

    public static final class AlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARMS).build();

        public static final String TABLE_NAME = "alarm";
        public static final String ALARM_ID = "_id";
        public static final String ALARM_ACTIVE = "alarm_active";
        public static final String ALARM_TIME = "alarm_time";
        public static final String ALARM_NAME = "alarm_name";

    }
}
