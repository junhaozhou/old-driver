package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.TrackContract;
import com.littlechoc.olddriver.presenter.TrackPresenter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.utils.PermissionUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class HomeActivity extends BaseActivity implements TrackContract.View {

  @BindView(R.id.root)
  public View rootView;

  @BindView(R.id.title_bar)
  public Toolbar titleBar;

  @BindView(R.id.track_switch)
  public FloatingActionButton trackSwitch;

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
    titleBar.setTitle(R.string.title);
    setSupportActionBar(titleBar);
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
      case R.id.history:
        trackPresenter.openHistoryActivity();
        break;
      case R.id.bluetooth_setting:
        trackPresenter.openBluetoothActivity();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    trackPresenter.onDestroy();
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
}
