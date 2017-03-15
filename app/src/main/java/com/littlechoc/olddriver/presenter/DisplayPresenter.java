package com.littlechoc.olddriver.presenter;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.DisplayContract;
import com.littlechoc.olddriver.ui.base.BasePagerFragment;
import com.littlechoc.olddriver.ui.fragment.SensorDetailFragment;
import com.littlechoc.olddriver.ui.fragment.SummaryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class DisplayPresenter implements DisplayContract.Presenter {

  private DisplayContract.View displayView;

  public DisplayPresenter(DisplayContract.View displayView) {
    this.displayView = displayView;
    displayView.setPresenter(this);
  }

  @Override
  public List<BasePagerFragment> createFragments(String folder) {
    List<BasePagerFragment> fragments = new ArrayList<>();
    fragments.add(SummaryFragment.newInstance(folder));
    fragments.add(SensorDetailFragment.newInstance(folder, Constants.TYPE_SENSOR_ACCELEROMETER));
    fragments.add(SensorDetailFragment.newInstance(folder, Constants.TYPE_SENSOR_MAGNETIC));
    fragments.add(SensorDetailFragment.newInstance(folder, Constants.TYPE_SENSOR_GYROSCOPE));
    return fragments;
  }

  @Override
  public void onDestroy() {

  }
}
