package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.presenter.TrackPresenter;
import com.littlechoc.olddriver.viewinterface.ITrackView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class TrackActivity extends BaseActivity implements ITrackView {

  @BindView(R.id.title_bar)
  public Toolbar titleBar;

  @BindView(R.id.track_switch)
  public Button trackSwitch;

  private boolean isTracking = false;


  private TrackPresenter trackPresenter;

  @Override
  public int getRootView() {
    return R.layout.activity_track;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    trackPresenter = new TrackPresenter(this);

    initView();

  }

  private void initView() {
    titleBar.setTitle(R.string.title);
    setSupportActionBar(titleBar);
    trackSwitch.setText(R.string.start_track);
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


  @Override
  public Context getContext() {
    return this;
  }
}
