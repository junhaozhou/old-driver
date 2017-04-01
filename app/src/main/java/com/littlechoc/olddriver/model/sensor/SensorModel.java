package com.littlechoc.olddriver.model.sensor;

import android.hardware.SensorEvent;

import com.littlechoc.olddriver.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Junhao Zhou 2017/3/12
 */

public abstract class SensorModel implements Serializable {

  protected static class SensorModelPool<M extends SensorModel> {

    private final List<M> sensorModels = new ArrayList<>(100);

    private final Object lock = new Object();

    public boolean isEmpty() {
      synchronized (lock) {
        return sensorModels.isEmpty();
      }
    }

    public void add(M model) {
      synchronized (lock) {
        sensorModels.add(model);
      }
    }

    public M get() {
      synchronized (lock) {
        if (!isEmpty()) {
          return sensorModels.remove(sensorModels.size() - 1);
        } else {
          return null;
        }
      }
    }

  }


  private long timestamp;

  public float x;

  public float y;

  public float z;

  private SensorWrapper sensor;

  public static SensorModel newInstance(Constants.SensorType type, float x, float y, float z, long timestamp) {
    switch (type) {
      case ACCELEROMETER:
        return AccelerometerModel.newInstance(x, y, z, timestamp);
      case MAGNETIC:
        return MagneticModel.newInstance(x, y, z, timestamp);
      case GYROSCOPE:
        return GyroscopeModel.newInstance(x, y, z, timestamp);
    }
    throw new IllegalArgumentException("Sensor not support");
  }

  void setData(float x, float y, float z, long timestamp) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.timestamp = timestamp;
  }

  void setData(SensorEvent event) {
    if (event != null) {
      timestamp = event.timestamp;
      x = event.values[0];
      y = event.values[1];
      z = event.values[2];
      sensor = new SensorWrapper(event.sensor);
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

  public SensorWrapper getSensor() {
    return sensor;
  }

  public abstract void reuse();

  @Override
  public String toString() {
    return String.format(Locale.CHINA, "%f,%f,%f,%d", x, y, z, timestamp);
  }
}
