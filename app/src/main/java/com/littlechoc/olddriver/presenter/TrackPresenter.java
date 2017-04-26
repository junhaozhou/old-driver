package com.littlechoc.olddriver.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.text.TextUtils;

import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.TrackContract;
import com.littlechoc.olddriver.dao.MarkDao;
import com.littlechoc.olddriver.dao.ObdDao;
import com.littlechoc.olddriver.dao.SensorDao;
import com.littlechoc.olddriver.model.MarkModel;
import com.littlechoc.olddriver.model.sensor.AccelerometerModel;
import com.littlechoc.olddriver.model.sensor.GyroscopeModel;
import com.littlechoc.olddriver.model.sensor.MagneticModel;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.ui.DisplayActivity;
import com.littlechoc.olddriver.utils.DateUtils;
import com.littlechoc.olddriver.utils.SpUtils;
import com.littlechoc.olddriver.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class TrackPresenter implements TrackContract.Presenter, SensorEventListener {

  public static final String TAG = "TrackPresenter";

  private SensorManager sensorManager;

  private SensorDao sensorDao;

  private MarkDao markDao;

  private ObdDao obdDao;

  private Sensor accelerometerSensor;

  private Sensor magneticSensor;

  private Sensor gyroscopeSensor;

  private TrackContract.View trackView;

  private String folder;

  private boolean logSensor;

  private long startTime = 0;

  private long endTime = 0;

  private BluetoothAdapter bluetoothAdapter;

  private BluetoothDevice bluetoothDevice;

  private PowerManager.WakeLock wakeLock;

  private List<ObdModel> obdModelList;

  private boolean isTracking;

  public TrackPresenter(TrackContract.View trackView) {
    assert trackView != null;
    this.trackView = trackView;
    sensorDao = new SensorDao();
    markDao = new MarkDao();
    obdDao = new ObdDao();

    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    initSensor();
    trackView.setPresenter(this);
    logSensor = SpUtils.getSensorLog();

    PowerManager powerManager = (PowerManager) trackView.getContext().getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake tag");
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
    startTime = System.currentTimeMillis();
    folder = DateUtils.time2Date(DateUtils.PATTERN_DEFAULT, startTime);
    isTracking = true;

    markDao.prepare(folder);
    startSensorTrack();
    startObdTrack();

    if (wakeLock != null) {
      wakeLock.acquire();
    }
  }

  private void startSensorTrack() {
    sensorDao.prepare(folder);
    sensorDao.saveSensorInfo(magneticSensor, Constants.SensorType.MAGNETIC);
    sensorDao.saveSensorInfo(accelerometerSensor, Constants.SensorType.ACCELEROMETER);
    sensorDao.saveSensorInfo(gyroscopeSensor, Constants.SensorType.GYROSCOPE);
    sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
  }

  private void startObdTrack() {
    obdModelList.clear();
    trackView.updateObdData();
    if (bluetoothDevice != null && obdDao != null) {
      obdDao.start(bluetoothDevice, folder, new ObdDao.Callback() {
        @Override
        public void onError(String msg) {
          ToastUtils.show(msg);
        }

        @Override
        public void onCommandResult(ObdModel obdModel) {
          if (obdModel == null) {
            return;
          }
          boolean hasExist = false;
          for (ObdModel model : obdModelList) {
            if (model.command.equals(obdModel.command)) {
              model.data = obdModel.data;
              model.formattedData = obdModel.formattedData;
              model.time = obdModel.time;
              hasExist = true;
              break;
            }
          }
          if (!hasExist) {
            obdModelList.add(obdModel);
          }
          trackView.updateObdData();
        }
      });
    }
  }

  public void stopTrack() {
    isTracking = false;
    stopSensorTrack();
    stopObdTrack();
    if (!TextUtils.isEmpty(folder)) {
      trackView.showMarkerBottomSheet(true);
    }
    endTime = System.currentTimeMillis();

    if (wakeLock != null) {
      wakeLock.release();
    }
  }

  private void stopSensorTrack() {
    sensorManager.unregisterListener(this, magneticSensor);
    sensorManager.unregisterListener(this, accelerometerSensor);
    sensorManager.unregisterListener(this, gyroscopeSensor);
    sensorDao.stop();
  }

  private void stopObdTrack() {
    if (bluetoothDevice != null && obdDao != null) {
      obdDao.stop();
    }
  }

  @Override
  public void selectBluetoothDevice() {
    if (bluetoothAdapter != null) {
      Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
      List<BluetoothDevice> deviceList = new ArrayList<>(devices.size());
      deviceList.addAll(devices);
      trackView.showBluetoothDevice(deviceList);
    }
  }

  @Override
  public void connectBluetooth(BluetoothDevice device) {
    bluetoothDevice = device;
  }

  public void openDisplayActivity() {
    Intent intent = new Intent(trackView.getContext(), DisplayActivity.class);
    intent.putExtra(Constants.KEY_FOLDER_NAME, folder);
    trackView.getContext().startActivity(intent);
  }

  @Override
  public void setIfLogSensor(boolean ifLog) {
    logSensor = ifLog;
    SpUtils.setSensorLog(ifLog);
  }

  private long markTime = 0L;

  @Override
  public void beginMark() {
    markTime = System.currentTimeMillis();
    trackView.showMarkerBottomSheet(false);
  }

  @Override
  public void saveMarker(int type, boolean last) {
    MarkModel markModel = new MarkModel();
    if (last) {
      markModel.begin = startTime;
      markModel.end = endTime;
    } else {
      markModel.begin = markTime;
      markModel.end = System.currentTimeMillis();
    }
    markModel.type = type;
    markDao.saveMark(markModel);
    if (last) {
      markDao.stop();
      trackView.showAnalyseSnack();
    }
  }

  @Override
  public void attachObdModelList(List<ObdModel> obdModels) {
    this.obdModelList = obdModels;
  }

  @Override
  public boolean isTracking() {
    return isTracking;
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