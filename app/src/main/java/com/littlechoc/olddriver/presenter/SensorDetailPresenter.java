package com.littlechoc.olddriver.presenter;

import android.os.Handler;
import android.os.Message;

import com.github.mikephil.charting.data.Entry;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.SensorDetailContract;
import com.littlechoc.olddriver.dao.SensorDao;
import com.littlechoc.olddriver.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class SensorDetailPresenter implements SensorDetailContract.Presenter {

  private SensorDetailContract.View sensorDetailView;

  private List<Entry> dataSet;

  private Handler handler = new Handler() {

    private float i = 0f;

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      List<Float> doubles = (List<Float>) msg.obj;
      List<Entry> entries = new ArrayList<>(doubles.size());
      for (float d : doubles) {
        Entry entry = new Entry();
        entry.setY(d);
        entry.setX(i++);
        entries.add(entry);
      }
      dataSet.addAll(entries);
      sensorDetailView.updateChart();
    }
  };

  public SensorDetailPresenter(SensorDetailContract.View sensorDetailView) {
    this.sensorDetailView = sensorDetailView;
    sensorDetailView.setPresenter(this);
  }

  @Override
  public void bindDataSet(List<Entry> dataSet) {
    this.dataSet = dataSet;
  }

  @Override
  public void analyseData(final String folder, final int type) {
    sensorDetailView.initChart();
    sensorDetailView.initYAxis(1, -1);
    new Thread(new Runnable() {
      @Override
      public void run() {
        String filePath = FileUtils.getAbsoluteFolder(folder) + File.separator + SensorDao.FILE_ACCELEROMETER + "." + SensorDao.SUFFIX;
        File file = new File(filePath);
        if (file.exists()) {
          try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            List<Float> floats = new ArrayList<>();
            while ((line = br.readLine()) != null) {
              String[] ss = line.split(",");
              float x = Float.valueOf(ss[0]);
              floats.add(x);
              if (floats.size() == 100) {
                sendMessage(floats);
                floats = new ArrayList<>();
              }
            }
            if (floats.size() != 0) {
              sendMessage(floats);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  private void sendMessage(List<Float> data) {
    Message message = handler.obtainMessage(1, data);
    handler.sendMessage(message);
  }

  @Override
  public void onDestroy() {

  }

  public static String getTitle(int type) {
    switch (type) {
      case Constants.TYPE_SENSOR_ACCELEROMETER:
        return "ACCELEROMETER";
      case Constants.TYPE_SENSOR_MAGNETIC:
        return "MAGNETIC";
      case Constants.TYPE_SENSOR_GYROSCOPE:
        return "GYROSCOPE";
      default:
        return "Unknown";
    }
  }
}
