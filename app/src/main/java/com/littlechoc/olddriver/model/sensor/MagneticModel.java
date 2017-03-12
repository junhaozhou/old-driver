package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class MagneticModel extends SensorModel implements Serializable {

  private static final SensorModelPool<MagneticModel> pool = new SensorModelPool<>();

  public static MagneticModel newInstance(SensorEvent event) {
    MagneticModel model;
    if (pool.isEmpty()) {
      model = new MagneticModel();
    } else {
      model = pool.get();
    }
    model.setData(event);
    return model;
  }

  public static MagneticModel newInstance(float x, float y, float z, long timestamp) {
    MagneticModel model;
    if (pool.isEmpty()) {
      model = new MagneticModel();
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
