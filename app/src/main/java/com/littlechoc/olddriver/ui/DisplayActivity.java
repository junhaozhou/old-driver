package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.DisplayContract;
import com.littlechoc.olddriver.presenter.DisplayPresenter;
import com.littlechoc.olddriver.ui.adapter.DisplayFragmentAdapter;
import com.littlechoc.olddriver.ui.base.BaseActivity;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class DisplayActivity extends BaseActivity implements DisplayContract.View {

  @BindView(R.id.title_bar)
  public Toolbar titleBar;

  @BindView(R.id.tab_layout)
  public TabLayout tabLayout;

  @BindView(R.id.view_pager)
  public ViewPager viewPager;

  private DisplayFragmentAdapter displayFragmentAdapter;

  private String folderName;

  private DisplayContract.Presenter displayPresenter;

  @Override
  public int getRootView() {
    return R.layout.activity_display;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    parseData(getIntent());
    init();
    initView();
  }

  private void init() {
    new DisplayPresenter(this);
  }

  private void parseData(Intent intent) {
    if (intent == null) {
      finish();
      return;
    }
    folderName = intent.getStringExtra(Constants.KEY_FOLDER_NAME);
    if (TextUtils.isEmpty(folderName)) {
      finish();
    }
  }

  private void initView() {
    titleBar.setTitle(R.string.title);
    titleBar.setNavigationIcon(R.drawable.ic_back);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    setSupportActionBar(titleBar);

    displayFragmentAdapter = new DisplayFragmentAdapter(getSupportFragmentManager(), displayPresenter.createFragments(folderName));
    viewPager.setAdapter(displayFragmentAdapter);

    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public void setPresenter(DisplayContract.Presenter presenter) {
    displayPresenter = presenter;
  }
}
