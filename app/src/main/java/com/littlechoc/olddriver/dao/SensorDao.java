package com.littlechoc.olddriver.dao;

import com.littlechoc.olddriver.model.sensor.AccelerometerModel;
import com.littlechoc.olddriver.utils.DateUtils;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class SensorDao {

  private static final String TAG = "SensorDao";

  public static final String SUFFIX = ".dat";

  public static final String FILE_ACCELEROMETER = "accelerometer";

  public static final String FILE_GYROSCOPE = "gyroscope";

  public static final String FILE_MAGNETIC = "magnetic";

  private String folder;

  public SensorDao() {
    folder = DateUtils.time2Date(DateUtils.PATTERN_DEFAULT, System.currentTimeMillis());

  }

  public void saveAccelerometerData(AccelerometerModel accelerometerModel) {
  }

}
