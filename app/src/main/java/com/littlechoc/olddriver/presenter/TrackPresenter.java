package com.littlechoc.olddriver.presenter;

import com.littlechoc.olddriver.dao.SensorDao;
import com.littlechoc.olddriver.viewinterface.ITrackView;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class TrackPresenter extends BasePresenter {

  private SensorDao sensorDao;

  private ITrackView trackView;

  public TrackPresenter(ITrackView trackView) {
    assert trackView != null;
    this.trackView = trackView;
  }

  public void startTrack() {
    if (sensorDao == null) {
      sensorDao = new SensorDao(trackView.getContext());
    }
    sensorDao.start();
  }

  public void stopTrack() {
    if (sensorDao != null) {
      sensorDao.stop();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (sensorDao != null) {
      sensorDao.start();
    }
  }
}