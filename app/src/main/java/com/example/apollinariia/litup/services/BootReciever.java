package com.example.apollinariia.litup.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.apollinariia.litup.services.AlarmReciever;


public class BootReciever extends BroadcastReceiver {

    AlarmReciever alarm = new AlarmReciever();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            // TODO
        }
    }
}

