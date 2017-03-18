package com.littlechoc.olddriver.utils;

import android.content.Context;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class MobileUtils {

  public static int dp2px(Context context, int dp) {
    if (context == null || dp < 0) {
      throw new IllegalArgumentException("context is null or dp < 0");
    }

    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }

  public static int px2dp(Context context, float px) {
    if (context == null || px < 0) {
      throw new IllegalArgumentException("context is null or px < 0");
    }

    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (px / scale + 0.5f);
  }
}
