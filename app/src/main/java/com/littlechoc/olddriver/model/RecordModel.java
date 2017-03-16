package com.littlechoc.olddriver.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class RecordModel implements Parcelable {

  private String name;

  private long size;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeLong(this.size);
  }

  public RecordModel() {
  }

  protected RecordModel(Parcel in) {
    this.name = in.readString();
    this.size = in.readLong();
  }

  public static final Parcelable.Creator<RecordModel> CREATOR = new Parcelable.Creator<RecordModel>() {
    @Override
    public RecordModel createFromParcel(Parcel source) {
      return new RecordModel(source);
    }

    @Override
    public RecordModel[] newArray(int size) {
      return new RecordModel[size];
    }
  };
}
