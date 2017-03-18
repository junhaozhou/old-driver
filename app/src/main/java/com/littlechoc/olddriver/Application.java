package com.littlechoc.olddriver;

import android.os.Handler;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class Application extends android.app.Application {

  private static Application instance = null;

  public static Application getInstance() {
    return instance;
  }

  public Handler handler = new Handler();

  public Application() {
    instance = this;
  }

  @Override
  public void onCreate() {
    super.onCreate();

  }

  public void runOnUiThread(Runnable callback) {
    handler.post(callback);
  }

  public void runOnUiThreadDelayed(Runnable callback, long delay) {
    handler.postDelayed(callback, delay);
  }

  public void removeCallback(Runnable callback) {
    handler.removeCallbacks(callback);
  }

}
