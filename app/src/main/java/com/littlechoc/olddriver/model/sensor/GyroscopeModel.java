package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 * 陀螺仪
 *
 * @author Junhao Zhou 2017/3/7
 */

public class GyroscopeModel extends SensorModel implements Serializable {

  public GyroscopeModel(float x, float y, float z, long timestamp) {
    super(x, y, z, timestamp);
  }

  public GyroscopeModel(SensorEvent event) {
    super(event);
  }
}
