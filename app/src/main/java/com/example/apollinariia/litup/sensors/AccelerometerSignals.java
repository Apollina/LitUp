package com.example.apollinariia.litup.sensors;

public enum AccelerometerSignals {

    MAGNITUDE,
    MOV_AVERAGE;

    public static final int count = AccelerometerSignals.values().length;
    public static final String[] OPTIONS = {"|V|","\\u0394g"};

}
