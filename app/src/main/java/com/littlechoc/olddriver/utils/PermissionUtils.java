package com.littlechoc.olddriver.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.littlechoc.olddriver.Application;
import com.littlechoc.olddriver.ui.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * @author Junhao Zhou 2017/3/13
 */

public class PermissionUtils {

  private static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

  private static class PermissionWrapper {

    private String[] permissions;

    private int requestCode;

    private WeakReference<OnPermissionRequestCallback> permissionRequestCallback;

    PermissionWrapper(String[] permissions, int requestCode) {
      this.permissions = permissions;
      this.requestCode = requestCode;
    }

    void setPermissionRequestCallback(OnPermissionRequestCallback callback) {
      this.permissionRequestCallback = new WeakReference<>(callback);
    }

    OnPermissionRequestCallback getPermissionRequestCallback() {
      return permissionRequestCallback != null ? permissionRequestCallback.get() : null;
    }
  }

  public interface OnPermissionRequestCallback {

    void onPermissionGranted();

    void onPermissionDenied();
  }

  public static abstract class OnPermissionGranted implements OnPermissionRequestCallback {
    @Override
    public void onPermissionDenied() {
      // do nothing
    }
  }


  public static final PermissionWrapper STORAGE_PERMISSION
          = new PermissionWrapper(
          new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
          , 0x00000001);

  public static final PermissionWrapper LOCATION_PERMISSION
          = new PermissionWrapper(
          new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}
          , 0x00000002);

  public static final PermissionWrapper BLUETOOTH_PERMISSION
          = new PermissionWrapper(
          new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH}
          , 0x00000003);

  private static final PermissionWrapper[] PERMISSION_WRAPPER_ARRAY =
          new PermissionWrapper[]{STORAGE_PERMISSION, LOCATION_PERMISSION};

  public static void requestPermission(BaseActivity activity,
                                       PermissionWrapper permissionWrapper) {

    requestPermission(activity, permissionWrapper, null);
  }

  public static void requestPermission(BaseActivity activity,
                                       PermissionWrapper wrapper,
                                       OnPermissionRequestCallback callback) {
    if (Build.VERSION.SDK_INT < 23) {
      return;
    }
    if (activity == null) {
      return;
    }
    wrapper.setPermissionRequestCallback(callback);
    requestPermissions(activity, wrapper.permissions, wrapper.requestCode,
            callback);
  }

  private static void requestPermissions(Activity activity, String[] permissions,
                                         int requestCode, OnPermissionRequestCallback callback) {
    if (!checkPermissions(activity, permissions)) {
      ActivityCompat.requestPermissions(activity, permissions, requestCode);
    } else {
      if (callback != null) {
        callback.onPermissionGranted();
      }
    }
  }

  public static boolean checkPermission(Context context, PermissionWrapper permissionWrapper) {
    if (context == null) {
      throw new IllegalArgumentException("[PermissionUtils] context is null");
    }
    return checkPermissions(context, permissionWrapper.permissions);
  }

  private static boolean checkPermissions(Context context, String[] permissions) {
    for (String permission : permissions) {
      if (!checkPermission(context, permission)) {
        return false;
      }
    }
    return true;
  }

  private static boolean checkPermission(Context context, String permission) {
    return ActivityCompat.checkSelfPermission(
            context == null ? Application.getInstance() : context, permission)
            == PERMISSION_GRANTED;
  }

  public static void onPermissionResult(int requestCode,
                                        String[] permissions, int[] grantResults) {
    for (PermissionWrapper wrapper : PERMISSION_WRAPPER_ARRAY) {
      if (wrapper.requestCode == requestCode) {
        OnPermissionRequestCallback callback = wrapper.getPermissionRequestCallback();
        if (callback != null) {
          if (checkResult(grantResults)) {
            callback.onPermissionGranted();
          } else {
            callback.onPermissionDenied();
          }
        }
        return;
      }
    }
  }

  private static boolean checkResult(int[] grantResults) {
    if (grantResults == null || grantResults.length < 1) {
      return false;
    }
    for (int result : grantResults) {
      if (result != PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

}
