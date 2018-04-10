package com.example.tal.minesweeper;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class GameRotationService extends Service implements SensorEventListener {
    private final IBinder mBinder = new LocalBinder();
    private SensorManager mSensorManager;
    private IRotationListener mRotationListener;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];
    private float[] mRotationMatrix = new float[9];
    private float[] mOrientationAngles = new float[3];
    private float[] mFirstOrientationAngles = new float[3];
    private boolean isFirst = true;
    private boolean isSecond = false;
    private boolean isWarning = false;
    private static final double SENSITIVITY = 0.7f;
    private Handler handler;
    private int mWaitTime = 2000;

    public interface IRotationListener{
        void onRotation();
    }
    public GameRotationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mSensorManager = (SensorManager) getSystemService(getBaseContext().SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometerSensor ,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticFieldSensor,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        handler.post(new Runnable() {
            @Override
            public void run() {
                isWarning = true;
                handler.postDelayed(this, mWaitTime);
            }
        });
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(this);
        return super.onUnbind(intent);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
        updateOrientationAngles();
        if (isWarning) {
            if (checkWithBaseOrientation() && isFirst == false && mRotationListener != null) {
                if(mWaitTime >= 200)
                    mWaitTime -= 200;
                mRotationListener.onRotation();
            }
            isWarning = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void updateOrientationAngles() {
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        if(isSecond) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isSecond = false;
            mSensorManager.getOrientation(mRotationMatrix, mFirstOrientationAngles);

        }
        if(isFirst) {
            isSecond = true;
            isFirst = false;
        }
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
    }
    private boolean checkWithBaseOrientation(){
        for(int i = 0 ; i < mFirstOrientationAngles.length-1 ; i++){
            if (mOrientationAngles[i] >  (mFirstOrientationAngles[i] + SENSITIVITY)
                    || mOrientationAngles[i] <  (mFirstOrientationAngles[i] - SENSITIVITY)) {
                return true;
            }
        }
        mWaitTime = 2000;
        return false;
    }
    public class LocalBinder extends Binder {
        public void registerListener (IRotationListener listener){
            mRotationListener = listener;
        }
    }

}
