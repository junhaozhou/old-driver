package com.littlechoc.olddriver.utils;

import android.util.Log;

import com.littlechoc.olddriver.BuildConfig;

import java.util.Locale;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class Logger {

  private static final boolean DEBUG = BuildConfig.DEBUG;

  public static int v(String tag, String format, Object... params) {
    return DEBUG ? Log.v(tag, createMessage(format, params)) : 0;
  }

  public static int i(String tag, String format, Object... params) {
    return DEBUG ? Log.i(tag, createMessage(format, params)) : 0;
  }

  public static int d(String tag, String format, Object... params) {
    return DEBUG ? Log.d(tag, createMessage(format, params)) : 0;
  }

  public static int w(String tag, String format, Object... params) {
    return DEBUG ? Log.w(tag, createMessage(format, params)) : 0;
  }

  public static int e(String tag, String format, Object... params) {
    return DEBUG ? Log.e(tag, createMessage(format, params)) : 0;
  }


  private static String createMessage(String format, Object... params) {
    return String.format(Locale.CHINA, format, params);
  }
}
