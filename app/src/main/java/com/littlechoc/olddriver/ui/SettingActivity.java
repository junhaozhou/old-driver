package com.littlechoc.olddriver.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.fragment.SettingFragment;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/4/17
 */

public class SettingActivity extends BaseActivity {

  @BindView(R.id.title_bar)
  Toolbar titleBar;

  @Override
  public int getRootView() {
    return R.layout.activity_setting;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initView();

  }

  private void initView() {
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    getFragmentManager()
            .beginTransaction()
            .add(R.id.container, SettingFragment.newInstance(), "tag")
            .commitAllowingStateLoss();
  }
}
