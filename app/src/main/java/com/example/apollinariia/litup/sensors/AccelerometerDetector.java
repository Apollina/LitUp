package com.example.apollinariia.litup.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Configuring accelerometer and handling its results.
 */
public class AccelerometerDetector implements SensorEventListener {

    private static final String TAG = AccelerometerDetector.class.getSimpleName();

    private static final int CONFIG_SENSOR = SensorManager.SENSOR_DELAY_GAME;

    private double[] mAccelResult = new double[AccelerometerSignals.count];
    private AccelerometerGraph mAccelGraph;
    private AccelerometerProcessing mAccelProcessing = AccelerometerProcessing.getInstance();

    private SensorManager mSensorManager;
    private Sensor mAccel;

    private OnStepCountChangeListener mStepListener;

    public void setStepCountChangeListener(OnStepCountChangeListener listener) {
        mStepListener = listener;
    }

    public AccelerometerDetector(SensorManager sensorManager, AccelerometerGraph graph) {
        mStepListener = null;
        mSensorManager = sensorManager;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "Success! There's a accelerometer. Resolution:" + mAccel.getResolution()
                    + " Max range: " + mAccel.getMaximumRange()
                    + "\n Time interval: " + mAccel.getMinDelay() / 1000 + "ms.");
        } else {
            Log.d(TAG, "Failure! No accelerometer.");
        }
        // get graph handles
        mAccelGraph = graph;
    }

    public void startDetector() {
        if (!mSensorManager.registerListener(this, mAccel, CONFIG_SENSOR)) {
            Log.d(TAG, "The sensor is not supported and unsuccessfully enabled.");
        }
    }

    public void stopDetector() {
        mSensorManager.unregisterListener(this, mAccel);
        mAccelGraph.reset();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // handle accelerometer data
        mAccelProcessing.setEvent(event);
        final long eventMsecTime = mAccelProcessing.timestampToMilliseconds();

        mAccelResult[0] = mAccelProcessing.calcMagnitudeVector(0);
        mAccelResult[0] = mAccelProcessing.calcExpMovAvg(0);
        mAccelResult[1] = mAccelProcessing.calcMagnitudeVector(1);

        // update graph with value and timestamp
        mAccelGraph.invalidate(eventMsecTime, mAccelResult);

        // step detection
        if (mAccelProcessing.stepDetected(1)) {

            // notify potential listeners
            if (mStepListener != null)
                mStepListener.onStepCountChange(eventMsecTime);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}