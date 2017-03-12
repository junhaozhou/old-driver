package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 *
 *
 * @author Junhao Zhou 2017/3/7
 */

public class MagneticModel extends SensorModel implements Serializable {

  public MagneticModel(float x, float y, float z, long timestamp) {
    super(x, y, z, timestamp);
  }

  public MagneticModel(SensorEvent event) {
    super(event);
  }
}
