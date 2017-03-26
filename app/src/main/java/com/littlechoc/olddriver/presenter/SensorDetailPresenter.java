package com.littlechoc.olddriver.presenter;

import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.data.Entry;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.SensorDetailContract;
import com.littlechoc.olddriver.model.sensor.SensorModel;
import com.littlechoc.olddriver.model.sensor.SensorWrapper;
import com.littlechoc.olddriver.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
    sensorDetailView.initXAxis(sensor.maxRange, -sensor.maxRange, msg.arg1);
    sensorDetailView.initYAxis(sensor.maxRange, -sensor.maxRange, msg.arg1);
    sensorDetailView.initZAxis(sensor.maxRange, -sensor.maxRange, msg.arg1);
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
      entry.setX(i);
      xEntries.add(entry);

      entry = new Entry();
      entry.setY(d.getY());
      entry.setX(i);
      yEntries.add(entry);

      entry = new Entry();
      entry.setY(d.getZ());
      entry.setX(i++);
      zEntries.add(entry);

      d.reuse();
    }
    xSet.addAll(xEntries);
    ySet.addAll(yEntries);
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

  @Override
  public void analyseData(final String folder, final Constants.SensorType type) {
    init();
    new Thread(new Runnable() {
      @Override
      public void run() {
        String filePath = contactFilePath(folder, type);
        File file = new File(filePath);
        BufferedReader br = null;
        LineNumberReader lnr = null;
        if (file.exists()) {
          try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file)));

            String line;
            line = br.readLine();
            SensorWrapper sensor = SensorWrapper.parseFromJson(line);

            long length = file.length();
            lnr.skip(length);
            int lines = lnr.getLineNumber();
            sendMessage(sensor, lines);

            List<SensorModel> data = new ArrayList<>();
            while ((line = br.readLine()) != null) {
              String[] ss = line.split(",");
              float x = Float.valueOf(ss[0]);
              float y = Float.valueOf(ss[1]);
              float z = Float.valueOf(ss[2]);
              long timestamp = Long.valueOf(ss[3]);
              SensorModel sensorModel = SensorModel.newInstance(type, x, y, z, timestamp);
              data.add(sensorModel);
              if (data.size() == 100) {
                sendMessage(data);
                data = new ArrayList<>();
              }
            }
            if (data.size() != 0) {
              sendMessage(data);
            }
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            FileUtils.safeCloseStream(br);
          }
        }
      }
    }).start();
  }

  private void init() {
    i = 0f;
    sensorDetailView.initChart();
  }

  private String contactFilePath(String folder, Constants.SensorType type) {
    StringBuilder path = new StringBuilder();
    path.append(FileUtils.getAbsoluteFolder(folder)).append(File.separator);
    switch (type) {
      case ACCELEROMETER:
        path.append(Constants.FILE_ACCELEROMETER);
        break;
      case GYROSCOPE:
        path.append(Constants.FILE_GYROSCOPE);
        break;
      case MAGNETIC:
        path.append(Constants.FILE_MAGNETIC);
        break;
      default:
        return "";
    }
    return path.toString();
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
