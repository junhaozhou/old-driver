package com.littlechoc.olddriver.contract;

import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;

/**
 * @author Junhao Zhou 2017/3/14
 */

public interface TrackContract {

  interface Presenter extends BasePresenter {

    void stopTrack();

    void startTrack();

    void openDisplayActivity();

    void setIfLogSensor(boolean ifLog);
  }

  interface View extends BaseView<Presenter> {

    void showAnalyseSnack();
  }
}
