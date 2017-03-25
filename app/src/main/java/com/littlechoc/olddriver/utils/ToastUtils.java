package com.littlechoc.olddriver.utils;

import android.content.Context;
import android.widget.Toast;

import com.littlechoc.olddriver.Application;

/**
 * @author Junhao Zhou 2017/3/25
 */

public class ToastUtils {

  public static void show(String text) {
    show(Application.getInstance(), text, Toast.LENGTH_SHORT);
  }

  public static void show(Context context, String text, int duration) {
    Toast.makeText(context, text, duration).show();
  }

}
