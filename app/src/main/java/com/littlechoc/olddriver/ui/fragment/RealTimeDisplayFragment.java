package com.littlechoc.olddriver.ui.fragment;

import com.littlechoc.olddriver.presenter.RecordPresenter;
import com.littlechoc.olddriver.ui.base.BasePagerFragment;

/**
 * @author Junhao Zhou 2017/4/28
 */

public abstract class RealTimeDisplayFragment extends BasePagerFragment {

  public abstract RecordPresenter getPresenter();
}
