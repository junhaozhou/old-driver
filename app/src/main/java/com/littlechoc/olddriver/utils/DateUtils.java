package com.littlechoc.olddriver.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class DateUtils {

  public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";

  public static final String PATTERN_ = "";

  public static String time2Date(String pattern, long time) {
    return time2Date(pattern, time, TimeUnit.MILLISECONDS);
  }

  public static String time2Date(String pattern, long time, TimeUnit timeUnit) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
    return sdf.format(new Date(timeUnit.toMillis(time)));
  }

  public static long date2Time(String pattern, String date) {
    return date2Time(pattern, date, TimeUnit.MILLISECONDS);
  }

  public static long date2Time(String pattern, String date, TimeUnit timeUnit) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
    try {
      return timeUnit.convert(sdf.parse(date).getTime(), TimeUnit.MILLISECONDS);
    } catch (ParseException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
