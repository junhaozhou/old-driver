package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

/**
 * 加速度
 *
 * @author Junhao Zhou 2017/3/12
 */

public class AccelerometerModel extends SensorModel {

  public AccelerometerModel(float x, float y, float z, long timestamp) {
    super(x, y, z, timestamp);
  }

  public AccelerometerModel(SensorEvent event) {
    super(event);
  }
}
