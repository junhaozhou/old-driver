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

  private List<BasePagerFragment> fragments = new ArrayList<>();

  public DisplayPresenter(DisplayContract.View displayView) {
    this.displayView = displayView;
    displayView.setPresenter(this);
  }

  @Override
  public List<BasePagerFragment> createFragments(String folder) {
    fragments.clear();
    fragments.add(SummaryFragment.newInstance(folder));
    fragments.add(SensorDetailFragment.newInstance(folder, Constants.SensorType.ACCELEROMETER));
    fragments.add(SensorDetailFragment.newInstance(folder, Constants.SensorType.MAGNETIC));
    fragments.add(SensorDetailFragment.newInstance(folder, Constants.SensorType.GYROSCOPE));
    return fragments;
  }

  @Override
  public BasePagerFragment getFragmentsAtPos(int pos) {
    return fragments == null ? null :
            pos < 0 || pos >= fragments.size() ? null : fragments.get(pos);
  }

  @Override
  public void changeChartDisplayStyle(int style) {
    for (BasePagerFragment fragment : fragments) {
      if (fragment instanceof SensorDetailFragment) {
        ((SensorDetailFragment) fragment).changeChartDisplayStyle(style);
      }
    }
  }


  @Override
  public void onDestroy() {

  }
}
