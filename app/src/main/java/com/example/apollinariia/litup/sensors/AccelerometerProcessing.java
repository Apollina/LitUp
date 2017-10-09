package com.example.apollinariia.litup.sensors;

import android.hardware.SensorEvent;
import android.util.Log;

import java.util.Date;

/**
 * Computing and processing accelerometer data.
 */
public class AccelerometerProcessing implements OnThresholdChangeListener {

    private static final String TAG = AccelerometerProcessing.class.getSimpleName();

    private static AccelerometerProcessing instance = null;

    public static AccelerometerProcessing getInstance() {

        if (instance == null)
            return new AccelerometerProcessing();
        return instance;
    }

    private static final int INACTIVE_PERIODS = 12;
    public static final float THRESH_INIT_VALUE = 12.72f;

    // dynamic variables
    private int mInactiveCounter = 0;
    private boolean isActiveCounter = true;

    private static double mThresholdValue = THRESH_INIT_VALUE;
    private double[] mAccelValues = new double[AccelerometerSignals.count];
    private double[] mAccelLastValues = new double[AccelerometerSignals.count];

    private SensorEvent mEvent;

    // computational variables
    private double[] gravity = new double[3];
    private double[] linear_acceleration = new double[3];

    //Gets the current SensorEvent data.
    void setEvent(SensorEvent e) {
        mEvent = e;
    }

    public double getThresholdValue() {
        Log.d(TAG,"Getting Threshold: " + mThresholdValue);
        return mThresholdValue;
    }

    //Get event time.
    long timestampToMilliseconds() {
        return (new Date()).getTime() + (mEvent.timestamp - System.nanoTime()) / 1000000L;
    }

    //Vector Magnitude |V| = sqrt(x^2 + y^2 + z^2)
    double calcMagnitudeVector(int i) {
        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = mEvent.values[0] - gravity[0];
        linear_acceleration[1] = mEvent.values[1] - gravity[1];
        linear_acceleration[2] = mEvent.values[2] - gravity[2];

        mAccelValues[i] = Math.sqrt(
                linear_acceleration[0] * linear_acceleration[0] +
                        linear_acceleration[1] * linear_acceleration[1] +
                        linear_acceleration[2] * linear_acceleration[2]);
        return mAccelValues[i];
    }

    //Exponential Moving average.
    double calcExpMovAvg(int i) {
        final double alpha = 0.1;
        mAccelValues[i] = alpha * mAccelValues[i] + (1 - alpha) * mAccelLastValues[i];
        mAccelLastValues[i] = mAccelValues[i];
        return mAccelValues[i];
    }

    /**
     * Step detection algorithm.
     * When the value is over the threshold, the step is found and the algorithm sleeps for
     * the specified distance
     */
    boolean stepDetected(int i) {
        if (mInactiveCounter == INACTIVE_PERIODS) {
            mInactiveCounter = 0;
            if (!isActiveCounter)
                isActiveCounter = true;
        }
        if (mAccelValues[i] > mThresholdValue) {
            if (isActiveCounter) {
                mInactiveCounter = 0;
                isActiveCounter = false;
                return true;
            }
        }
        ++mInactiveCounter;
        return false;
    }

    @Override
    public void onThresholdChange(double value) {
        mThresholdValue = value;
    }
}