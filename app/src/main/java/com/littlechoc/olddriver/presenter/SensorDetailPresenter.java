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
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      List<Float> doubles = (List<Float>) msg.obj;
      List<Entry> entries = new ArrayList<>(doubles.size());
      for (float d : doubles) {
        Entry entry = new Entry();
        entry.setX(d);
        entries.add(entry);
      }
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
    new Thread(new Runnable() {
      @Override
      public void run() {
        String filePath = FileUtils.getAbsoluteFolder(folder) + File.separator + SensorDao.FILE_ACCELEROMETER + "." + SensorDao.SUFFIX;
        File file = new File(filePath);
        if (file.exists()) {
          try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = "";
            List<Double> doubles = new ArrayList<>();
            while ((line = br.readLine()) != null) {
              String[] ss = line.split(",");
              double x = Double.valueOf(ss[0]);
              doubles.add(x);
              if (doubles.size() == 100) {

                doubles = new ArrayList<>();
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
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
