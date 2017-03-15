package com.littlechoc.olddriver.contract;

import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;
import com.littlechoc.olddriver.ui.base.BasePagerFragment;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public interface DisplayContract {

  interface View extends BaseView<Presenter> {

  }

  interface Presenter extends BasePresenter {

    List<BasePagerFragment> createFragments(String folder);


  }
}
