package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.HomeContract;
import com.littlechoc.olddriver.model.PatternCategory;
import com.littlechoc.olddriver.presenter.HomePresenter;
import com.littlechoc.olddriver.ui.adapter.RealTimeDisplayFragmentAdapter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.base.BaseAdapter;
import com.littlechoc.olddriver.ui.view.CustomNavigationView;
import com.littlechoc.olddriver.ui.view.MarkBottomSheet;
import com.littlechoc.olddriver.ui.view.ViewPagerEx;
import com.littlechoc.olddriver.utils.PermissionUtils;
import com.littlechoc.olddriver.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class HomeActivity extends BaseActivity implements HomeContract.View {

  public static final String TAG = "HomeActivity";

  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;

  @BindView(R.id.navigation_view)
  CustomNavigationView navigationView;

  @BindView(R.id.root)
  View rootView;

  @BindView(R.id.title_bar)
  Toolbar titleBar;

  @BindView(R.id.track_switch)
  FloatingActionButton trackSwitch;

  @BindView(R.id.tab_layout)
  TabLayout tabLayout;

  @BindView(R.id.container)
  ViewPagerEx realTimeViewPager;

  private boolean isRecording = false;

  private HomeContract.Presenter homePresenter;

  @Override
  public int getRootView() {
    return R.layout.activity_home;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
    initView();
  }

  private void init() {
    new HomePresenter(this);
  }

  private void initView() {
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isRecording) {
          drawerLayout.openDrawer(Gravity.START);
        }
      }
    });

    ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
            R.string.open_drawer_content_desc, R.string.close_drawer_content_desc);
    drawerLayout.addDrawerListener(drawerToggle);

    navigationView.setDrawerLayout(drawerLayout);

    trackSwitch.setImageResource(R.drawable.ic_start_track);

    RealTimeDisplayFragmentAdapter fragmentAdapter = new RealTimeDisplayFragmentAdapter(getSupportFragmentManager(), homePresenter.getRealDisplayFragment());
    realTimeViewPager.setAdapter(fragmentAdapter);

    tabLayout.setupWithViewPager(realTimeViewPager);
    tabLayout.setTabMode(TabLayout.MODE_FIXED);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_home_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.mark);
    if (item != null) {
      item.setVisible(homePresenter.isRecording());
    }
    item = menu.findItem(R.id.menu_display_style);
    if (item != null) {
      item.setVisible(homePresenter.isRecording());
    }
    item = menu.findItem(R.id.menu_sensor_log);
    if (item != null) {
      item.setVisible(homePresenter.isRecording());
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.mark:
        homePresenter.beginMark();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    homePresenter.onDestroy();
  }

  private static final long THRESHOLD = 1000 * 2;

  private long lastPressTime = 0L;

  @Override
  public void onBackPressed() {
    if (System.currentTimeMillis() - lastPressTime > THRESHOLD) {
      ToastUtils.show("Press Again to Quit");
      lastPressTime = System.currentTimeMillis();
    } else {
      super.onBackPressed();
    }
  }

  @OnClick(R.id.track_switch)
  public void onTrackSwitchClick() {
    PermissionUtils.requestPermission(this, PermissionUtils.STORAGE_PERMISSION,
            new PermissionUtils.OnPermissionGranted() {
              @Override
              public void onPermissionGranted() {
                if (isRecording) {
                  homePresenter.stop();
                } else {
                  homePresenter.start();
                }
                drawerLayout.setDrawerLockMode(isRecording ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                toggleTrackState(isRecording);
                isRecording = !isRecording;
                supportInvalidateOptionsMenu();
              }
            });
  }

  private void toggleTrackState(final boolean start) {
    trackSwitch.hide(new FloatingActionButton.OnVisibilityChangedListener() {
      @Override
      public void onHidden(FloatingActionButton fab) {
        trackSwitch.setImageResource(start ? R.drawable.ic_start_track : R.drawable.ic_stop_track);
        trackSwitch.show();
      }
    });
  }

  public void onAnalyseClick() {
    homePresenter.openDisplayActivity();
  }

  @Override
  public void setPresenter(HomeContract.Presenter presenter) {
    homePresenter = presenter;
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public void showAnalyseSnack() {
    Snackbar.make(rootView, R.string.track_success, 3000).setAction(R.string.show, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onAnalyseClick();
      }
    }).show();
  }

  @Override
  public void showMarkerBottomSheet(final boolean isLast) {
    MarkBottomSheet bottomSheet = MarkBottomSheet.newInstance(PatternCategory.COMMON);
    bottomSheet.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position) {
        Logger.d(TAG, "on mark click: " + position);
        homePresenter.saveMarker(position, isLast);
      }
    });
    bottomSheet.show(getSupportFragmentManager(), "MarkBottomSheet");
  }
}
