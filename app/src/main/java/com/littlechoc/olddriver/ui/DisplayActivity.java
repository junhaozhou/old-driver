package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.DisplayContract;
import com.littlechoc.olddriver.presenter.DisplayPresenter;
import com.littlechoc.olddriver.ui.adapter.DisplayFragmentAdapter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.fragment.SensorDetailFragment;
import com.littlechoc.olddriver.ui.view.ViewPagerEx;

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
  public ViewPagerEx viewPager;

  private DisplayFragmentAdapter displayFragmentAdapter;

  private String folderName;

  private DisplayContract.Presenter displayPresenter;

  private boolean styleSplit = true;

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
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    displayFragmentAdapter = new DisplayFragmentAdapter(getSupportFragmentManager(), displayPresenter.createFragments(folderName));
    viewPager.setAdapter(displayFragmentAdapter);

    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem lock = menu.findItem(R.id.menu_lock);
    if (lock != null) {
      lock.setIcon(viewPager.isCanScroll() ?
              R.drawable.ic_lock_open : R.drawable.ic_lock);
      lock.setTitle(viewPager.isCanScroll() ?
              R.string.menu_disable_scroll : R.string.menu_enable_scroll);
      return true;
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_display_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_lock:
        onLockClick();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void onLockClick() {
    viewPager.toggleScroll();
    supportInvalidateOptionsMenu();

    displayPresenter.changeChartDisplayStyle(viewPager.isCanScroll() ?
            SensorDetailFragment.STYLE_UNLIMITED : SensorDetailFragment.STYLE_LIMIT);
  }

  private void onDisplayStyleClick() {
    styleSplit = !styleSplit;
    Fragment fragment = getCurrentFragment();
    if (fragment != null && fragment instanceof SensorDetailFragment) {
      if (styleSplit) {

      } else {

      }
    }

    supportInvalidateOptionsMenu();
  }

  private Fragment getCurrentFragment() {
    return displayPresenter
            .getFragmentsAtPos(viewPager.getCurrentItem());
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
