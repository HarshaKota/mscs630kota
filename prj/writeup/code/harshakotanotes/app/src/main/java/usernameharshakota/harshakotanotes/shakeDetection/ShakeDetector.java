package usernameharshakota.harshakotanotes.shakeDetection;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/*
This class implements the SensorEventListener that provides methods to listen for on shake and
perform actions when the sensor values change.
 */
public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 3.25F;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILL = 500;

    private OnShakeListener mListener;
    private long mShakeTimestamp;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        void onShake();
    }

    /*
    The sensor values are taken and divided by the gravitational constant to convert them to a
    g-force value and then checked on each change if the values are far enough to be considered as a
     shake.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + MIN_TIME_BETWEEN_SHAKES_MILL > now) {
                    return;
                }

                mShakeTimestamp = now;

                mListener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do Nothing
    }
}
