package com.littlechoc.olddriver.contract;

import com.littlechoc.olddriver.contract.base.BaseView;
import com.littlechoc.olddriver.presenter.RecordPresenter;

/**
 * @author Junhao Zhou 2017/4/28
 */

public interface SensorRecordContract {

  interface Presenter extends RecordPresenter {

    void toggleLogSensor();

    boolean ifLogSensor();

  }

  interface View extends BaseView<Presenter> {

    void clear();

    void onNewData(float x, float y, float z);
  }

}
