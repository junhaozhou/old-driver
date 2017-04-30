package com.littlechoc.olddriver.presenter;

import com.littlechoc.olddriver.contract.base.BasePresenter;

/**
 * @author Junhao Zhou 2017/4/28
 */

public interface RecordPresenter extends BasePresenter {

  /**
   * init
   *
   * @param folder 文件夹
   */
  void prepare(String folder);

  /**
   * 开始记录
   */
  void start();

  /**
   * 停止记录
   */
  void stop();
}
