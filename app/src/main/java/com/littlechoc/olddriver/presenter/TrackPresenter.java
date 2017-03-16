package com.littlechoc.olddriver.presenter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextUtils;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.TrackContract;
import com.littlechoc.olddriver.dao.SensorDao;
import com.littlechoc.olddriver.model.sensor.AccelerometerModel;
import com.littlechoc.olddriver.model.sensor.GyroscopeModel;
import com.littlechoc.olddriver.model.sensor.MagneticModel;
import com.littlechoc.olddriver.ui.DisplayActivity;
import com.littlechoc.olddriver.ui.HistoryActivity;
import com.littlechoc.olddriver.utils.Logger;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class TrackPresenter implements TrackContract.Presenter, SensorEventListener {

  public static final String TAG = "TrackPresenter";

  private SensorManager sensorManager;

  private SensorDao sensorDao;

  private Sensor accelerometerSensor;

  private Sensor magneticSensor;

  private Sensor gyroscopeSensor;

  private TrackContract.View trackView;

  private String lastFolder;

  private boolean logSensor = false;

  public TrackPresenter(TrackContract.View trackView) {
    assert trackView != null;
    this.trackView = trackView;
    sensorDao = new SensorDao();
    initSensor();
    trackView.setPresenter(this);
  }

  private void initSensor() {
    if (sensorManager == null) {
      sensorManager = (SensorManager) trackView.getContext().getSystemService(Context.SENSOR_SERVICE);
    }
    if (magneticSensor == null) {
      magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    if (accelerometerSensor == null) {
      accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    if (gyroscopeSensor == null) {
      gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }
  }

  public void startTrack() {
    sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);

  }

  public void stopTrack() {
    sensorManager.unregisterListener(this, magneticSensor);
    sensorManager.unregisterListener(this, accelerometerSensor);
    sensorManager.unregisterListener(this, gyroscopeSensor);
    sensorDao.stop();
    lastFolder = sensorDao.getFolder();
    if (!TextUtils.isEmpty(lastFolder)) {
      trackView.showAnalyseButton();
    }
  }

  public void openDisplayActivity() {
    Intent intent = new Intent(trackView.getContext(), DisplayActivity.class);
    intent.putExtra(Constants.KEY_FOLDER_NAME, lastFolder);
    trackView.getContext().startActivity(intent);
  }

  @Override
  public void openHistoryActivity() {
    trackView.getContext()
            .startActivity(new Intent(trackView.getContext(), HistoryActivity.class));
  }

  @Override
  public void setIfLogSensor(boolean ifLog) {
    logSensor = ifLog;
  }

  @Override
  public void onDestroy() {
    sensorManager.unregisterListener(this, magneticSensor);
    sensorManager.unregisterListener(this, accelerometerSensor);
    sensorManager.unregisterListener(this, gyroscopeSensor);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      AccelerometerModel model = AccelerometerModel.newInstance(event);
      sensorDao.saveAccelerometerData(model);
      logAccelerometer(event);
    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
      MagneticModel model = MagneticModel.newInstance(event);
      sensorDao.saveMagnetic(model);
      logMagnetic(event);
    } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
      sensorDao.saveGyroscope(GyroscopeModel.newInstance(event));
      logGyroscope(event);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  private static final float NS2S = 1.0f / 1000000000.0f;
  private float timestamp;
  private float[] angle = new float[3];

  private long[] counts = new long[3];

  private void logGyroscope(SensorEvent event) {
    if (!logSensor) {
      return;
    }
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

  private void logMagnetic(SensorEvent event) {
    if (!logSensor) {
      return;
    }
    // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    Logger.i(TAG, "MAGNETIC_SENSOR_START");
    // 手机的磁场感应器从外部采集数据的时间间隔是10000微秒
    Logger.i(TAG, "minDelay ------------> %d", magneticSensor.getMinDelay());
    // 磁场感应器的最大量程
    Logger.i(TAG, "maximumRange --------> %f", event.sensor.getMaximumRange());
    Logger.i(TAG, "x--------------------> %f", x);
    Logger.i(TAG, "y--------------------> %f", y);
    Logger.i(TAG, "z--------------------> %f", z);
    Logger.i(TAG, "time-----------------> %d", event.timestamp);
    Logger.i(TAG, "MAGNETIC_SENSOR_STOP");
    counts[1]++;
  }

  private void logAccelerometer(SensorEvent event) {
    if (!logSensor) {
      return;
    }
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
  }
}