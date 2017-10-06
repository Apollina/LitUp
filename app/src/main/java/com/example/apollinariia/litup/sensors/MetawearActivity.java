package com.example.apollinariia.litup.sensors;

/**
 * Created by Apollinariia on 10/5/2017.
 */

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;


import com.example.apollinariia.litup.R;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.Debug;
import com.mbientlab.metawear.module.GyroBmi160;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.module.GyroBmi160.Range;
import com.mbientlab.metawear.module.GyroBmi160.OutputDataRate;


import bolts.Continuation;
import bolts.Task;

public class MetawearActivity extends Activity implements ServiceConnection {

    private static final String LOG_TAG = "MetawearActivity";
    private static final String GYRO_STREAM_KEY = "gyro_stream";

    private BtleService.LocalBinder serviceBinder;
    private final String MW_MAC_ADDRESS = "FB:47:01:E3:CE:7A";
    private MetaWearBoard board;
    private Debug debug;
    private Logging logging;

    private GyroBmi160 gyroBmi160;
    TextView gyroData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_alert);

        // Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        gyroData = (TextView) findViewById(R.id.textGyro);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Typecast the binder to the service's LocalBinder class
        serviceBinder = (BtleService.LocalBinder) service;

        Log.i("MetawearActivity", "Service Connected");

        retrieveBoard(MW_MAC_ADDRESS);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    public void sensorMsg(String msg, final String sensor) {
        final String reading = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gyroData.setText("Gyro: " + reading);
            }
        });
    }

    private void retrieveBoard(final String MW_MAC_ADDRESS) {
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        board = serviceBinder.getMetaWearBoard(remoteDevice);
        board.connectAsync().onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                Log.i("MetawearActivity", "Connected to " + MW_MAC_ADDRESS);

                gyroBmi160 = board.getModule(GyroBmi160.class);
                gyroBmi160.configure()
                        .odr(OutputDataRate.ODR_50_HZ)
                        .range(Range.FSR_500)
                        .commit();


                return gyroBmi160.angularVelocity().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                Log.i("MetawearActivity", data.value(AngularVelocity.class).toString());
                            }
                        });
                    }
                }).continueWith(new Continuation<Route, Void>() {
                    @Override
                    public Void then(Task<Route> task) throws Exception {
                        gyroBmi160.angularVelocity();
                        gyroBmi160.start();
                        return null;
                    }
                });
            }
        });
    }
}

