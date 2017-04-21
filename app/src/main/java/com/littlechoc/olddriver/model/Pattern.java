package com.littlechoc.olddriver.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Junhao Zhou 2017/4/16
 */

public class Pattern implements Parcelable {


  public static final Pattern UNKNOWN = new Pattern();

  static {
    UNKNOWN.setId(-1);
    UNKNOWN.setName("Unknown");
    UNKNOWN.setCategoryId(-1);
  }

  private String name;

  private int id;

  private int categoryId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public Pattern() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeInt(this.id);
    dest.writeInt(this.categoryId);
  }

  protected Pattern(Parcel in) {
    this.name = in.readString();
    this.id = in.readInt();
    this.categoryId = in.readInt();
  }

  public static final Creator<Pattern> CREATOR = new Creator<Pattern>() {
    @Override
    public Pattern createFromParcel(Parcel source) {
      return new Pattern(source);
    }

    @Override
    public Pattern[] newArray(int size) {
      return new Pattern[size];
    }
  };
}
