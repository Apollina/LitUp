package com.example.apollinariia.litup.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.apollinariia.litup.MainActivity;
import com.example.apollinariia.litup.R;

/**
 * Created by Apollinariia on 9/27/2017.
 */

public class Schedulle extends IntentService

    {

        public Schedulle() {
        super("SchedulingService");
    }

        public static final String TAG = "Scheduling Demo";
        // An ID used to post the notification.
        public static final int NOTIFICATION_ID = 1;
        private NotificationManager mNotificationManager;

        @Override
        protected void onHandleIntent (Intent intent){
        sendNotification("alarm", intent);
        AlarmReciever.AlarmReceiver.completeWakefulIntent(intent);
    }

        // Post a notification indicating whether a doodle was found.

    private void sendNotification(String msg, Intent intent) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent sendIntent = new Intent(this, MainActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putAll(intent.getExtras());
        bundle.putBoolean("fromNotification", true);

        sendIntent.putExtras(bundle);

//        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                sendIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("alert")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

//        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
    }
}
