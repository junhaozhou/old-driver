package com.littlechoc.olddriver.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Junhao Zhou 2017/3/18
 */

public class DeviceModel implements Parcelable {

  private String deviceName;

  private String deviceMac;

  public DeviceModel() {
  }

  public DeviceModel(String deviceName, String deviceMac) {
    this.deviceName = deviceName;
    this.deviceMac = deviceMac;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getDeviceMac() {
    return deviceMac;
  }

  public void setDeviceMac(String deviceMac) {
    this.deviceMac = deviceMac;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.deviceName);
    dest.writeString(this.deviceMac);
  }

  protected DeviceModel(Parcel in) {
    this.deviceName = in.readString();
    this.deviceMac = in.readString();
  }

  public static final Parcelable.Creator<DeviceModel> CREATOR = new Parcelable.Creator<DeviceModel>() {
    @Override
    public DeviceModel createFromParcel(Parcel source) {
      return new DeviceModel(source);
    }

    @Override
    public DeviceModel[] newArray(int size) {
      return new DeviceModel[size];
    }
  };
}
