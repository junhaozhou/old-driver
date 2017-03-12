package com.littlechoc.olddriver.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class DateUtils {

  public static final String PATTERN_DEFAULT = "YYYY-MM-dd hh:mm:ss";

  public static final String PATTERN_ = "";

  public static String time2Date(String pattern, long time) {
    return time2Date(pattern, time, TimeUnit.MILLISECONDS);
  }

  public static String time2Date(String pattern, long time, TimeUnit timeUnit) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
    return sdf.format(new Date(timeUnit.toMillis(time)));
  }
}
