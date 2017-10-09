package com.example.apollinariia.litup.sensors;

import android.os.Bundle;
import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.GyroBmi160;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Apollinariia on 10/9/2017.
 */

public class Gyro {
    private static final String GYRO_STREAM_KEY = "gyro_stream";
    private static GyroBmi160 gyroBmi160;

    private static MetaWearBoard board;
    private static Data d;

    public static void start() {

        gyroBmi160 = board.getModule(GyroBmi160.class);

        // set the data rat to 50Hz and the
        // data range to +/- 2000 degrees/s
        gyroBmi160.configure()
                .odr(GyroBmi160.OutputDataRate.ODR_50_HZ)
                .range(GyroBmi160.Range.FSR_2000)
                .commit();

        gyroBmi160.angularVelocity().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                        Log.i("GyroActivity", data.value(AngularVelocity.class).toString());
                        d = data;
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                gyroBmi160.angularVelocity();
                gyroBmi160.start();
                Log.i("GyroActivity", "Normally the gyro has started...");
                return null;
            }
        });

    }

    public static String getGyroData() {
        if(null != d)
            return d.value(AngularVelocity.class).toString();
        Log.i("GyroActivity", "No data from gyro");
        return "N/A";
    }
}

