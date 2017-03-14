package com.littlechoc.olddriver;

import android.app.Application;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class BaseApplication extends Application {

  private static BaseApplication instance = null;

  public static BaseApplication getInstance() {
    return instance;
  }

  public BaseApplication() {
    instance = this;
  }

  @Override
  public void onCreate() {
    super.onCreate();

  }

}
