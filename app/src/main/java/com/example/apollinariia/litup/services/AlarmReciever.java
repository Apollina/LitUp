package com.example.apollinariia.litup.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.apollinariia.litup.data.AlarmContract;

/**
 * Created by Apollinariia on 9/27/2017.
 */

public class AlarmReciever {
    public static class AlarmReceiver extends WakefulBroadcastReceiver {

        private final String TAG = "AlarmReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: " + intent.toString());

            final Bundle bundle = intent.getExtras();
            final Intent serviceIntent = new Intent(context, Schedulle.class);

            //serviceIntent.putExtra(AlarmContract.TAG, bundle.getByteArray(AlarmContract.TAG));
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, serviceIntent);
        }
    }

}
