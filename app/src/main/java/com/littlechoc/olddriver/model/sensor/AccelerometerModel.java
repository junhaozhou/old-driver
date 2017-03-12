package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

/**
 * 加速度
 *
 * @author Junhao Zhou 2017/3/12
 */

public class AccelerometerModel extends SensorModel {

  private static final SensorModelPool<AccelerometerModel> pool = new SensorModelPool<>();

  public static AccelerometerModel newInstance(SensorEvent event) {
    AccelerometerModel model;
    if (pool.isEmpty()) {
      model = new AccelerometerModel();
    } else {
      model = pool.get();
    }
    model.setData(event);
    return model;
  }

  public static AccelerometerModel newInstance(float x, float y, float z, long timestamp) {
    AccelerometerModel model;
    if (pool.isEmpty()) {
      model = new AccelerometerModel();
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
