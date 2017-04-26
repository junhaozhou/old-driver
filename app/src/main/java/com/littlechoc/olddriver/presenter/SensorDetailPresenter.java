package com.littlechoc.olddriver.presenter;

import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.data.Entry;
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
    sensorDetailView.initXAxis(sensor.maxRange, -sensor.maxRange, msg.arg1 / 50);
    sensorDetailView.initYAxis(sensor.maxRange, -sensor.maxRange, msg.arg1 / 50);
    sensorDetailView.initZAxis(sensor.maxRange, -sensor.maxRange, msg.arg1 / 50);
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
      entry.setX(i / 50);
      xEntries.add(entry);

      entry = new Entry();
      entry.setY(d.getY());
      entry.setX(i / 50);
      yEntries.add(entry);

      entry = new Entry();
      entry.setY(d.getZ());
      entry.setX(i++ / 50);
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
//        if (type == Constants.SensorType.ACCELEROMETER) {
//          List<SensorModel> other = SensorDao.loadSensorData(folder, Constants.SensorType.MAGNETIC);
//          matrix(other);
//        }
        sendMessage(data);
      }
    }).start();
  }

  private void matrix(List<SensorModel> gyroscopeList) {
    int offset = data.size() > gyroscopeList.size() ? data.size() - gyroscopeList.size() : 0;
    for (int i = offset; i < data.size(); i++) {
      SensorModel gyroscope = gyroscopeList.get(i - offset);
      SensorModel acc = data.get(i);
      float[] accArray = new float[]{acc.x, acc.y, acc.z};
      float[] gyArray = new float[]{gyroscope.x, gyroscope.y, gyroscope.z};
      float[] ori = new float[9];
      float[] res = new float[3];
      if (SensorManager.getRotationMatrix(ori, null, accArray, gyArray)) {
        SensorManager.getOrientation(ori, res);
        double ddx = Math.toDegrees(res[0]);
        double dx = res[0];
        double ddy = Math.toDegrees(res[1]);
        double dy = res[1];
        double ddz = Math.toDegrees(res[2]);
        double dz = res[2];
        acc.x = calX(acc.x, dx, dy, dz);
        acc.y = calY(acc.y, dx, dy, dz);
        acc.z = calZ(acc.z, dy, dz);
      }
    }
  }

  private float calX(float g, double dx, double dy, double dz) {
    return (float) (-Math.cos(dz) * Math.sin(dy) * Math.cos(dx) * g - Math.sin(dz) * Math.sin(dx) * g);
  }

  private float calY(float g, double dx, double dy, double dz) {
    return (float) (-Math.cos(dz) * Math.sin(dy) * Math.cos(dx) * g + Math.sin(dz) * Math.cos(dx) * g);
  }

  private float calZ(float g, double dy, double dz) {
    return (float) (-Math.cos(dz) * Math.cos(dy) * g);
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
      ret.add(SensorModel.newInstance(type, x, y, z, data.get(i).getTimestamp()));
    }
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
