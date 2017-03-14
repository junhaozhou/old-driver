package com.littlechoc.olddriver.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.littlechoc.olddriver.utils.PermissionUtils;

import butterknife.ButterKnife;

/**
 * @author Junhao Zhou 2017/3/7
 */

public abstract class BaseActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(getRootView());
    ButterKnife.bind(this);
  }

  @LayoutRes
  public abstract int getRootView();

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionUtils.onPermissionResult(requestCode, permissions, grantResults);
  }
}
