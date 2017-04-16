package com.littlechoc.olddriver.model.sensor;

/**
 * @author Junhao Zhou 2017/4/1
 */

public class DataSet {
  public SensorWrapper sensorWrapper;

  public int lines;

  public DataSet(SensorWrapper sensorWrapper, int lines) {
    this.sensorWrapper = sensorWrapper;
    this.lines = lines;
  }
}
