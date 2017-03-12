package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

import java.io.Serializable;

/**
 * @author Junhao Zhou 2017/3/12
 */

public abstract class SensorModel implements Serializable {

  private long timestamp;

  private float x;

  private float y;

  private float z;

  private float range;

  private int accuracy;

  public SensorModel(float x, float y, float z, long timestamp) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.timestamp = timestamp;
  }

  public SensorModel(SensorEvent event) {
    if (event != null) {
      timestamp = event.timestamp;
      x = event.values[0];
      y = event.values[1];
      z = event.values[2];
      range = event.sensor.getMaximumRange();
      accuracy = event.accuracy;
    }
  }

  public long getTimestamp() {
    return timestamp;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getZ() {
    return z;
  }

  public float getRange() {
    return range;
  }

  public int getAccuracy() {
    return accuracy;
  }
}
