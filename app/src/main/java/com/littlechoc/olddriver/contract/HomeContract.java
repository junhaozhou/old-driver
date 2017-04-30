package com.littlechoc.olddriver.contract;

import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;
import com.littlechoc.olddriver.ui.fragment.RealTimeDisplayFragment;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/14
 */

public interface HomeContract {

  interface Presenter extends BasePresenter {

    void stop();

    void start();

    void openDisplayActivity();

    void beginMark();

    void saveMarker(int type, boolean last);

    boolean isRecording();

    List<RealTimeDisplayFragment> getRealDisplayFragment();
  }

  interface View extends BaseView<Presenter> {

    void showAnalyseSnack();

    void showMarkerBottomSheet(boolean isLast);
  }
}
