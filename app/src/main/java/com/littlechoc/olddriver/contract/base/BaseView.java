package com.littlechoc.olddriver.contract.base;

import android.content.Context;

/**
 * @author Junhao Zhou 2017/3/12
 */

public interface BaseView<P extends BasePresenter> {

  Context getContext();

  void setPresenter(P presenter);
}
