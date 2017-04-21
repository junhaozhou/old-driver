package com.littlechoc.olddriver.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class RecordModel implements Parcelable {

  private String name;

  private long size;

  private String date;

  private int patternId;

  public int getPatternId() {
    return patternId;
  }

  public void setPatternId(int patternId) {
    this.patternId = patternId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

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


  public RecordModel() {
  }

  public static class Comparator implements java.util.Comparator<RecordModel> {

    @Override
    public int compare(RecordModel o1, RecordModel o2) {
      return -o1.getDate().compareTo(o2.getDate());
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeLong(this.size);
    dest.writeString(this.date);
    dest.writeInt(this.patternId);
  }

  protected RecordModel(Parcel in) {
    this.name = in.readString();
    this.size = in.readLong();
    this.date = in.readString();
    this.patternId = in.readInt();
  }

  public static final Creator<RecordModel> CREATOR = new Creator<RecordModel>() {
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
