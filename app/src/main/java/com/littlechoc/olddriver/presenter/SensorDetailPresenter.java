package com.littlechoc.olddriver.presenter;

import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.data.Entry;
import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.SensorDetailContract;
import com.littlechoc.olddriver.dao.SensorDao;
import com.littlechoc.olddriver.model.sensor.DataSet;
import com.littlechoc.olddriver.model.sensor.SensorModel;
import com.littlechoc.olddriver.model.sensor.SensorWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class SensorDetailPresenter implements SensorDetailContract.Presenter {

  public static final String TAG = "SensorDetailPresenter";

  private SensorDetailContract.View sensorDetailView;

  private static final int WHAT_SENSOR_INFO = 1;

  private static final int WHAT_SENSOR_DATA = 2;

  private List<Entry> xSet;
  private List<Entry> ySet;
  private List<Entry> zSet;

  private Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      switch (msg.what) {
        case WHAT_SENSOR_INFO:
          handleSensorInfo(msg);
          break;
        case WHAT_SENSOR_DATA:
          handleSensorData(msg);
          break;
      }
    }
  };

  private void handleSensorInfo(Message msg) {
    SensorWrapper sensor = (SensorWrapper) msg.obj;
    sensorDetailView.initXAxis(sensor.maxRange, -sensor.maxRange, msg.arg1 / Constants.SENSOR_SIMPLE_RATE);
    sensorDetailView.initYAxis(sensor.maxRange, -sensor.maxRange, msg.arg1 / Constants.SENSOR_SIMPLE_RATE);
    sensorDetailView.initZAxis(sensor.maxRange, -sensor.maxRange, msg.arg1 / Constants.SENSOR_SIMPLE_RATE);
  }

  private float i = 0f;

  private void handleSensorData(Message msg) {
    List<SensorModel> data = (List<SensorModel>) msg.obj;
    List<Entry> xEntries = new ArrayList<>(data.size());
    List<Entry> yEntries = new ArrayList<>(data.size());
    List<Entry> zEntries = new ArrayList<>(data.size());
    for (SensorModel d : data) {
      Entry entry = new Entry();
      entry.setY(d.getX());
      entry.setX(i / Constants.SENSOR_SIMPLE_RATE);
      xEntries.add(entry);

      entry = new Entry();
      entry.setY(d.getY());
      entry.setX(i / Constants.SENSOR_SIMPLE_RATE);
      yEntries.add(entry);

      entry = new Entry();
      entry.setY(d.getZ());
      entry.setX(i++ / Constants.SENSOR_SIMPLE_RATE);
      zEntries.add(entry);

      d.reuse();
    }
    xSet.clear();
    xSet.addAll(xEntries);
    ySet.clear();
    ySet.addAll(yEntries);
    zSet.clear();
    zSet.addAll(zEntries);

    sensorDetailView.updateDataSet();
  }

  public SensorDetailPresenter(SensorDetailContract.View sensorDetailView) {
    this.sensorDetailView = sensorDetailView;
    sensorDetailView.setPresenter(this);
  }

  @Override
  public void bindDataSet(List<Entry> xSet, List<Entry> ySet, List<Entry> zSet) {
    this.xSet = xSet;
    this.ySet = ySet;
    this.zSet = zSet;
  }

  List<SensorModel> data = new ArrayList<>();

  @Override
  public void analyseData(final String folder, final Constants.SensorType type) {
    init();
    new Thread(new Runnable() {
      @Override
      public void run() {
        DataSet dataSet = SensorDao.getInfo(folder, type);
        if (dataSet != null) {
          sendMessage(dataSet.sensorWrapper, dataSet.lines);
        } else {
          return;
        }
        data.addAll(SensorDao.loadSensorData(folder, type));
        if (type == Constants.SensorType.ACCELEROMETER) {
          List<SensorModel> other = SensorDao.loadSensorData(folder, Constants.SensorType.MAGNETIC);
          transfer(other);
        }
        sendMessage(data);
      }
    }).start();
  }

  private void transfer(List<SensorModel> magneticList) {
    if (data.size() == 0 || magneticList == null || magneticList.size() == 0) {
      return;
    }
    int offset = magneticList.size() > data.size() ? magneticList.size() - data.size() : 0;
//    while (offset < magneticList.size() && !isLegal(data.get(0), magneticList.get(offset))) {
//      offset++;
//    }
    Logger.i(TAG, "offset = %d", offset);
    for (int i = 0; i < data.size() && i + offset < magneticList.size(); i++) {
      SensorModel magnetic = magneticList.get(i + offset);
      SensorModel acc = data.get(i);
      matrix(magnetic, acc);
    }
  }

  private boolean isLegal(SensorModel accelerometer, SensorModel magnetic) {
    if (Math.abs(accelerometer.getTimestamp() - magnetic.getTimestamp()) < 20 * 1000 * 1000) {
      return true;
    } else {
      return false;
    }
  }

  private void matrix(SensorModel magnetic, SensorModel accelerometer) {
    float[] accArray = new float[]{accelerometer.x, accelerometer.y, accelerometer.z};
    float[] gyArray = new float[]{magnetic.x, magnetic.y, magnetic.z};
    float[] ori = new float[9];
    float[] res = new float[3];
    if (SensorManager.getRotationMatrix(ori, null, accArray, gyArray)) {
      SensorManager.getOrientation(ori, res);
//      float[][] accMatrix = MatrixUtils.toMatrix(accArray, 1, 3);
//      MatrixUtils.multi(accMatrix, MatrixUtils.toMatrix(ori, 3, 3), accMatrix);
//      float[][] right = new float[3][3];
//      double degree = Math.toDegrees(res[0]);
//      right[0][0] = (float) Math.cos(-degree);
//      right[0][1] = (float) -Math.sin(degree);
//      right[1][0] = (float) Math.sin(-degree);
//      right[1][1] = (float) Math.cos(-degree);
//      right[2][2] = 1f;
//      MatrixUtils.multi(accMatrix, right, accMatrix);
//      double ddx = Math.toDegrees(res[0]);
//      double ddy = Math.toDegrees(res[1]);
//      double ddz = Math.toDegrees(res[2]);
//      MatrixUtils.multi(accMatrix, createMatrix(ddx, ddx, ddx), accMatrix);
//      accelerometer.x = accMatrix[0][0];
//      accelerometer.y = accMatrix[0][1];
//      accelerometer.z = accMatrix[0][2];

      double dx = res[0];
      double dy = res[1];
      double dz = res[2];
      accelerometer.x = calX(9.8f, dy, dz, dx);
      accelerometer.y = calY(9.8f, dy, dz, dx);
      accelerometer.z = calZ(9.8f, dy, dz);
    }
  }

  private float[][] createMatrix(double a, double b, double r) {
    float[][] matrix = new float[3][3];
    matrix[0][0] = (float) (cos(a) * cos(r) - cos(b) * sin(a) * sin(r));
    matrix[0][1] = (float) (-cos(b) * cos(r) * sin(a) - cos(a) * sin(r));
    matrix[0][2] = (float) (sin(a) * sin(b));
    matrix[1][0] = (float) (cos(r) * sin(a) + cos(a) * cos(b) * sin(r));
    matrix[1][1] = (float) (cos(a) * cos(b) * cos(r) - sin(a) * sin(r));
    matrix[1][2] = (float) (-cos(a) * sin(b));
    matrix[2][0] = (float) (sin(b) * sin(r));
    matrix[2][1] = (float) (cos(r) * sin(b));
    matrix[2][2] = (float) cos(b);

    return matrix;
  }

  private double cos(double degree) {
    return Math.cos(degree);
  }

  private double sin(double degree) {
    return Math.sin(degree);
  }

  private float calX(float g, double dx, double dy, double dz) {
    return (float) (-Math.cos(dx) * Math.sin(dy) * Math.cos(dz) * g - Math.sin(dx) * Math.sin(dz) * g);
  }

  private float calY(float g, double dx, double dy, double dz) {
    return (float) (-Math.cos(dx) * Math.sin(dy) * Math.sin(dz) * g + Math.sin(dx) * Math.cos(dz) * g);
  }

  private float calZ(float g, double dx, double dy) {
    return (float) (-Math.cos(dx) * Math.cos(dy) * g);
  }

  @Override
  public void filterData(boolean filter, Constants.SensorType type) {
    List<SensorModel> res;
    if (filter) {
      res = filter(data, type);
    } else {
      res = data;
    }
    i = 0;
    sendMessage(res);
  }

  private static final int M = 5;

  private static final int p = (M - 1) / 2;

  private static final int q = p + 1;

  private static float y = 0;

  private List<SensorModel> filter(List<SensorModel> data, Constants.SensorType type) {
    List<SensorModel> ret = new ArrayList<>(data.size());
    for (int i = 0; i < data.size(); i++) {
      if (i < q) {
        ret.add(SensorModel.newInstance(type, data.get(i).x, data.get(i).y, data.get(i).z, data.get(i).getTimestamp()));
        continue;
      }
      if (i > data.size() - p - 1) {
        ret.add(SensorModel.newInstance(type, data.get(i).x, data.get(i).y, data.get(i).z, data.get(i).getTimestamp()));
        continue;
      }
      float x = filter(ret.get(i - 1).x, data.get(i + p).x, data.get(i - q).x);
      float y = filter(ret.get(i - 1).y, data.get(i + p).y, data.get(i - q).y);
      float z = filter(ret.get(i - 1).z, data.get(i + p).z, data.get(i - q).z);
//      float[] xyz = filter(i - q, i + p);
//      float x = xyz[0];
//      float y = xyz[1];
//      float z = xyz[2];
      ret.add(SensorModel.newInstance(type, x, y, z, data.get(i).getTimestamp()));
    }
    return ret;
  }

  private float[] filter(int begin, int end) {
    float[] ret = new float[3];
    for (int i = begin; i <= end; i++) {
      ret[0] += data.get(i).x;
      ret[1] += data.get(i).y;
      ret[2] += data.get(i).z;
    }
    ret[0] /= M;
    ret[1] /= M;
    ret[2] /= M;
    return ret;
  }

  private float filter(float y, float x1, float x2) {
    return y + (x1 - x2) / M;
  }

  private void init() {
    i = 0f;
  }

  private void sendMessage(SensorWrapper sensor, int lines) {
    Message message = handler.obtainMessage(WHAT_SENSOR_INFO, sensor);
    message.arg1 = lines;
    handler.sendMessage(message);
  }

  private void sendMessage(List<SensorModel> data) {
    Message message = handler.obtainMessage(WHAT_SENSOR_DATA, data);
    handler.sendMessage(message);
  }

  @Override
  public void onDestroy() {

  }

  public static String getTitle(Constants.SensorType type) {
    switch (type) {
      case ACCELEROMETER:
        return "ACCELEROMETER";
      case MAGNETIC:
        return "MAGNETIC";
      case GYROSCOPE:
        return "GYROSCOPE";
      default:
        return "Unknown";
    }
  }
}
