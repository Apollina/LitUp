package com.example.apollinariia.litup.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.apollinariia.litup.services.AlarmReciever;

/**
 * Created by Apollinariia on 9/27/2017.
 */

    public class BootReciever extends BroadcastReceiver {
        AlarmReciever.AlarmReceiver alarm = new AlarmReciever.AlarmReceiver();
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            {
            }
        }
}

