package com.littlechoc.olddriver.model.sensor;

import android.hardware.Sensor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;

/**
 * @author Junhao Zhou 2017/3/24
 */

public class SensorWrapper implements Serializable, Parcelable {

  public float maxRange;

  public int maxDelay;

  public int minDelay;

  public int version;

  public String name;

  public float resolution;

  public String vendor;

  public SensorWrapper(Sensor sensor) {
    maxRange = sensor.getMaximumRange();
//    maxDelay = sensor.getMaxDelay();
    minDelay = sensor.getMinDelay();
    version = sensor.getVersion();
    name = sensor.getName();
    resolution = sensor.getResolution();
    vendor = sensor.getVendor();
  }

  public SensorWrapper() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(this.maxRange);
    dest.writeInt(this.maxDelay);
    dest.writeInt(this.minDelay);
    dest.writeInt(this.version);
    dest.writeString(this.name);
    dest.writeFloat(this.resolution);
    dest.writeString(this.vendor);
  }

  protected SensorWrapper(Parcel in) {
    this.maxRange = in.readFloat();
    this.maxDelay = in.readInt();
    this.minDelay = in.readInt();
    this.version = in.readInt();
    this.name = in.readString();
    this.resolution = in.readFloat();
    this.vendor = in.readString();
  }

  public static final Creator<SensorWrapper> CREATOR = new Creator<SensorWrapper>() {
    @Override
    public SensorWrapper createFromParcel(Parcel source) {
      return new SensorWrapper(source);
    }

    @Override
    public SensorWrapper[] newArray(int size) {
      return new SensorWrapper[size];
    }
  };

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static SensorWrapper parseFromJson(String json) {
    try {
      return new Gson().fromJson(json, SensorWrapper.class);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return new SensorWrapper();
    }
  }
}
