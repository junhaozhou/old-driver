package com.littlechoc.olddriver.dao;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.littlechoc.olddriver.utils.Logger;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class SensorDao implements SensorEventListener {

  private static final String TAG = "SensorDao";

  private Context context;

  public SensorDao(@NonNull Context context) {
    this.context = context;
    initSensorManager();
  }

  private SensorManager sensorManager;


  private Sensor accelerometerSensor;

  private Sensor magneticSensor;

  private Sensor gyroscopeSensor;

  private void initSensorManager() {
    sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
  }

  public boolean start() {
    sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    counts = new long[3];
    return true;
  }

  public boolean stop() {
    if (sensorManager == null) {
      return false;
    }
    if (accelerometerSensor != null) {
      sensorManager.unregisterListener(this, accelerometerSensor);
    }
    if (magneticSensor != null) {
      sensorManager.unregisterListener(this, magneticSensor);
    }
    if (gyroscopeSensor != null) {
      sensorManager.unregisterListener(this, gyroscopeSensor);
    }
    Logger.v(TAG, "{Accelerometer, Magnetic, Gyroscope} = {%d, %d, %d}"
            , counts[0], counts[1], counts[2]);
    return true;
  }

  private static final float NS2S = 1.0f / 1000000000.0f;
  private float timestamp;
  private float[] angle = new float[3];
  private long[] counts = new long[3];


  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      Logger.i(TAG, "ACCELEROMETER_SENSOR_START");
      // x,y,z分别存储坐标轴x,y,z上的加速度
      float x = event.values[0];
      float y = event.values[1];
      float z = event.values[2];
      // 根据三个方向上的加速度值得到总的加速度值a
      float a = (float) Math.sqrt(x * x + y * y + z * z);
      // 传感器从外界采集数据的时间间隔为10000微秒
      Logger.d(TAG, "minDelay ------------> %d", magneticSensor.getMinDelay());
      // 加速度传感器的最大量程
      Logger.d(TAG, "maximumRange --------> %f", event.sensor.getMaximumRange());
      Logger.d(TAG, "a -------------------> %f", a);
      Logger.d(TAG, "x -------------------> %f", x);
      Logger.d(TAG, "y -------------------> %f", y);
      Logger.d(TAG, "z -------------------> %f", z);
      Logger.d(TAG, "time-----------------> %d", event.timestamp);
      Logger.d(TAG, "ACCELEROMETER_SENSOR_STOP");
      counts[0]++;

    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
      // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
      float x = event.values[0];
      float y = event.values[1];
      float z = event.values[2];

      Logger.i(TAG, "MAGNETIC_SENSOR_START");
      // 手机的磁场感应器从外部采集数据的时间间隔是10000微秒
      Logger.i(TAG, "minDelay ------------> %d", magneticSensor.getMinDelay());
      // 磁场感应器的最大量程
      Logger.i(TAG, "maximumRange --------> %f" , event.sensor.getMaximumRange());
      Logger.i(TAG, "x--------------------> %f", x);
      Logger.i(TAG, "y--------------------> %f", y);
      Logger.i(TAG, "z--------------------> %f", z);
      Logger.i(TAG, "time-----------------> %d", event.timestamp);
      Logger.i(TAG, "MAGNETIC_SENSOR_STOP");
      counts[1]++;

    } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
      Logger.e(TAG, "GYROSCOPE_SENSOR_START");
      //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
      if (timestamp != 0) {
        // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
        final float dT = (event.timestamp - timestamp) * NS2S;
        // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
        angle[0] += event.values[0] * dT;
        angle[1] += event.values[1] * dT;
        angle[2] += event.values[2] * dT;
        // 将弧度转化为角度
        float anglex = (float) Math.toDegrees(angle[0]);
        float angley = (float) Math.toDegrees(angle[1]);
        float anglez = (float) Math.toDegrees(angle[2]);

        Logger.e(TAG, "minDelay ----------> %d", gyroscopeSensor.getMinDelay());
        Logger.e(TAG, "angleX ------------> %f", anglex);
        Logger.e(TAG, "angleY ------------> %f", angley);
        Logger.e(TAG, "angleZ ------------> %f", anglez);
        Logger.e(TAG, "time-----------------> %d", event.timestamp);
        Logger.e(TAG, "GYROSCOPE_SENSOR_STOP");
      }

      //将当前时间赋值给timestamp
      timestamp = event.timestamp;
      counts[2]++;
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do nothing in parent
  }
}
