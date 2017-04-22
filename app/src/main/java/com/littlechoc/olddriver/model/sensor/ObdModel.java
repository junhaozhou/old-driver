package com.littlechoc.olddriver.model.sensor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Junhao Zhou 2017/4/23
 */

public class ObdModel implements Parcelable {

  public String command;

  public String data;

  public long time;

  public String name;

  public String formattedData;

  public ObdModel() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.command);
    dest.writeString(this.data);
    dest.writeLong(this.time);
    dest.writeString(this.name);
    dest.writeString(this.formattedData);
  }

  protected ObdModel(Parcel in) {
    this.command = in.readString();
    this.data = in.readString();
    this.time = in.readLong();
    this.name = in.readString();
    this.formattedData = in.readString();
  }

  public static final Creator<ObdModel> CREATOR = new Creator<ObdModel>() {
    @Override
    public ObdModel createFromParcel(Parcel source) {
      return new ObdModel(source);
    }

    @Override
    public ObdModel[] newArray(int size) {
      return new ObdModel[size];
    }
  };
}
