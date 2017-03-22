package com.littlechoc.olddriver.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.littlechoc.olddriver.Application;
import com.littlechoc.olddriver.Constants;

/**
 * @author Junhao Zhou 2017/3/18
 */

public class SpUtils {

  private static final String DEFAULT_SP_NAME = "com.littlechoc.olddriver";

  public static SharedPreferences getPreferences() {
    return getPreferences(DEFAULT_SP_NAME);
  }

  public static SharedPreferences getPreferences(String name) {
    return getPreferences(name, Context.MODE_PRIVATE);
  }

  public static SharedPreferences getPreferences(String name, int mode) {
    return Application.getInstance().getSharedPreferences(name, mode);
  }

  public static void setAutoConnectBluetoothFlag(boolean flag) {
    SharedPreferences.Editor editor = getPreferences().edit();
    editor.putBoolean(Constants.KEY_AUTO_CONNECT_BLUETOOTH, flag);
    commit(editor);
  }

  public static boolean getAutoConnectBluetoothFlag() {
    return getPreferences().getBoolean(Constants.KEY_AUTO_CONNECT_BLUETOOTH, true);
  }

  public static void setLatestConnectedDevice(String address) {
    SharedPreferences.Editor editor = getPreferences().edit();
    editor.putString(Constants.KEY_LATEST_DEVICE, address);
    commit(editor);
  }

  public static String getLatestConnectedDevice() {
    return getPreferences().getString(Constants.KEY_LATEST_DEVICE, "");
  }

  public static void setSensorLog(boolean enable) {
    SharedPreferences.Editor editor = getPreferences().edit();
    editor.putBoolean(Constants.KEY_SENSOR_LOG, enable);
    commit(editor);
  }

  public static boolean getSensorLog() {
    return getPreferences().getBoolean(Constants.KEY_SENSOR_LOG, false);
  }

  public static void commit(SharedPreferences.Editor editor) {
    editor.commit();
  }
}
