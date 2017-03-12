package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 * 陀螺仪
 *
 * @author Junhao Zhou 2017/3/7
 */

public class GyroscopeModel extends SensorModel implements Serializable {

  private static final SensorModelPool<GyroscopeModel> pool = new SensorModelPool<>();

  public static GyroscopeModel newInstance(SensorEvent event) {
    GyroscopeModel model;
    if (pool.isEmpty()) {
      model = new GyroscopeModel();
    } else {
      model = pool.get();
    }
    model.setData(event);
    return model;
  }

  public static GyroscopeModel newInstance(float x, float y, float z, long timestamp) {
    GyroscopeModel model;
    if (pool.isEmpty()) {
      model = new GyroscopeModel();
    } else {
      model = pool.get();
    }
    model.setData(x, y, z, timestamp);
    return model;
  }

  @Override
  public void reuse() {
    pool.add(this);
  }
}
