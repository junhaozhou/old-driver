package com.littlechoc.olddriver.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junhao Zhou 2017/4/16
 */

public class PatternCategory implements Parcelable {

  public static final int COMMON = 0;

  public static final int SENSOR = 1;

  public static final int OBD = 2;

  private String name;

  private int id;

  private List<Pattern> patternList;

  public PatternCategory(String name, int id) {
    this.name = name;
    this.id = id;
    this.patternList = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Pattern> getPatternList() {
    return patternList;
  }

  public void setPatternList(List<Pattern> patternList) {
    this.patternList = patternList;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeInt(this.id);
    dest.writeTypedList(this.patternList);
  }

  public PatternCategory() {
  }

  protected PatternCategory(Parcel in) {
    this.name = in.readString();
    this.id = in.readInt();
    this.patternList = in.createTypedArrayList(Pattern.CREATOR);
  }

  public static final Parcelable.Creator<PatternCategory> CREATOR = new Parcelable.Creator<PatternCategory>() {
    @Override
    public PatternCategory createFromParcel(Parcel source) {
      return new PatternCategory(source);
    }

    @Override
    public PatternCategory[] newArray(int size) {
      return new PatternCategory[size];
    }
  };
}
