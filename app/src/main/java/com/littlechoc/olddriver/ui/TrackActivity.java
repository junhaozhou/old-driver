package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.TrackContract;
import com.littlechoc.olddriver.presenter.TrackPresenter;
import com.littlechoc.olddriver.utils.PermissionUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class TrackActivity extends BaseActivity implements TrackContract.View {

  @BindView(R.id.title_bar)
  public Toolbar titleBar;

  @BindView(R.id.track_switch)
  public Button trackSwitch;

  @BindView(R.id.analyse)
  public Button analyseButton;

  private boolean isTracking = false;


  private TrackContract.Presenter trackPresenter;

  @Override
  public int getRootView() {
    return R.layout.activity_track;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    new TrackPresenter(this);

    PermissionUtils.requestPermission(this);
    initView();

  }


  private void initView() {
    titleBar.setTitle(R.string.title);
    setSupportActionBar(titleBar);
    trackSwitch.setText(R.string.start_track);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_track_activity, menu);
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

  @OnClick(R.id.track_switch)
  public void onTrackSwitchClick() {
    if (isTracking) {
      trackPresenter.stopTrack();
      trackSwitch.setText(R.string.start_track);
    } else {
      trackPresenter.startTrack();
      trackSwitch.setText(R.string.stop_track);
    }
    isTracking = !isTracking;
  }

  @OnClick(R.id.track_switch)
  public void onAnalyseClick() {
    trackPresenter.openAnalyseActivity();
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
  public void showAnalyseButton() {
    analyseButton.setVisibility(View.VISIBLE);
  }
}
