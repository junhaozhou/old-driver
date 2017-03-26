package com.littlechoc.olddriver.utils;

import com.google.gson.Gson;

/**
 * @author Junhao Zhou 2017/3/26
 */

public class JsonUtils {

  private static final Gson SINGLE = new Gson();

  public static Gson newInstance() {
    return SINGLE;
  }
}
