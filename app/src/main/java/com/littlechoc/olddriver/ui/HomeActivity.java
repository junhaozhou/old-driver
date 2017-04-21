package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.TrackContract;
import com.littlechoc.olddriver.model.PatternCategory;
import com.littlechoc.olddriver.presenter.TrackPresenter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.base.BaseAdapter;
import com.littlechoc.olddriver.ui.view.CustomNavigationView;
import com.littlechoc.olddriver.ui.view.MarkBottomSheet;
import com.littlechoc.olddriver.utils.PermissionUtils;
import com.littlechoc.olddriver.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class HomeActivity extends BaseActivity implements TrackContract.View {

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

  private ActionBarDrawerToggle drawerToggle;

  private boolean isTracking = false;

  private TrackContract.Presenter trackPresenter;

  @Override
  public int getRootView() {
    return R.layout.activity_home;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    new TrackPresenter(this);

    initView();

  }


  private void initView() {
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        drawerLayout.openDrawer(Gravity.START);
      }
    });

    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
            R.string.open_drawer_content_desc, R.string.close_drawer_content_desc);
    drawerLayout.addDrawerListener(drawerToggle);

    navigationView.setDrawerLayout(drawerLayout);

    trackSwitch.setImageResource(R.drawable.ic_start_track);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.enable_sensor_log:
        trackPresenter.setIfLogSensor(true);
        break;
      case R.id.disable_sensor_log:
        trackPresenter.setIfLogSensor(false);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    trackPresenter.onDestroy();
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
                if (isTracking) {
                  trackPresenter.stopTrack();
                  toggleTrackState(true);
                } else {
                  toggleTrackState(false);
                  trackPresenter.startTrack();
                }
                isTracking = !isTracking;
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
    trackPresenter.openDisplayActivity();
  }

  @Override
  public void setPresenter(TrackContract.Presenter presenter) {
    trackPresenter = presenter;
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
  public void showMarkerBottomSheet() {
    MarkBottomSheet bottomSheet = MarkBottomSheet.newInstance(PatternCategory.COMMON);
    bottomSheet.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position) {
        Logger.d(TAG, "on mark click: " + position);
        trackPresenter.saveMarker(position, true);
      }
    });
    bottomSheet.show(getSupportFragmentManager(), "MarkBottomSheet");
  }
}
