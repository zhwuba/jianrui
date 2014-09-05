package com.wb.launcher3.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorUtil {
    public abstract interface ISensorCallback {
        public abstract void detectShake();
    }

    final class MySensorEventListener implements SensorEventListener {
        public MySensorEventListener(SensorUtil sensorUtil) {
            super();
        }

        private void dealAccelerometer(SensorEvent event) {
            if (event.values[0] * event.values[0] + event.values[1] * event.values[1] > SHAKETHRESHOLD) {
                SensorUtil.this.mSensorCallback.detectShake();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            SensorUtil.this.setSensorUtilEvent(event);
            switch (event.sensor.getType()) {
            case 1:
                this.dealAccelerometer(event);
                break;
            }
        }
    }

    private static final int SHAKETHRESHOLD = 200;
    private Context context;
    private ISensorCallback mSensorCallback;
    private MySensorEventListener sensorEventListener;
    private SensorManager sensorManager;
    private SensorEvent sensorUtilEvent;
    private static SensorUtil sensorUtilInstance;

    static {
        SensorUtil.sensorUtilInstance = null;
    }

    private SensorUtil(Context context) {
        super();
        this.context = context;
    }

    public void closeSensor() {
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }

    public static synchronized SensorUtil getInstance(Context context) {
        if (sensorUtilInstance == null) {
            sensorUtilInstance = new SensorUtil(context);
        }

        return sensorUtilInstance;
    }

    public SensorEvent getSensorUtilEvent() {
        return this.sensorUtilEvent;
    }

    public void openSensor() {
        this.sensorManager = (SensorManager) this.context.getSystemService("sensor");
        Sensor sensor = this.sensorManager.getDefaultSensor(1);
        this.sensorEventListener = new MySensorEventListener(this);
        this.sensorManager.registerListener(this.sensorEventListener, sensor, 3);
    }

    public void setSensorCallback(ISensorCallback callback) {
        this.mSensorCallback = callback;
    }

    private void setSensorUtilEvent(SensorEvent event) {
        this.sensorUtilEvent = event;
    }
}
