package com.littlechoc.olddriver;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class Constants {


  public static final String KEY_FOLDER_NAME = "key_folder_name";
  public static final String KEY_SENSOR_TYPE = "key_sensor_type";
  public static final String KEY_AUTO_CONNECT_BLUETOOTH = "key_auto_connect_bluetooth";
  public static final String KEY_LATEST_DEVICE = "key_latest_device";
  public static final String KEY_SENSOR_LOG = "key_sensor_log_switch";

  public static final String FILE_ACCELEROMETER = "accelerometer.dat";

  public static final String FILE_GYROSCOPE = "gyroscope.dat";

  public static final String FILE_MAGNETIC = "magnetic.dat";

  public static final String FILE_MARK = "mark.dat";

  public enum SensorType {
    ACCELEROMETER, GYROSCOPE, MAGNETIC
  }
}
