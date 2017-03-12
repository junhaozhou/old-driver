package com.littlechoc.olddriver.utils;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * @author Junhao Zhou 2017/3/13
 */

public class PermissionUtils {

  private static final String[] PERMISSIONS = new String[] {
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.ACCESS_COARSE_LOCATION,
  };

  public static void requestPermission(Activity activity) {
    if (activity == null) {
      return;
    }
    ActivityCompat.requestPermissions(activity, PERMISSIONS, 0);
  }

}
