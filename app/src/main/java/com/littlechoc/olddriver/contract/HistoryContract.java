package com.littlechoc.olddriver.contract;

import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;
import com.littlechoc.olddriver.model.RecordModel;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class HistoryContract {

  public interface View extends BaseView<Presenter> {

    void updateList();
  }

  public interface Presenter extends BasePresenter {

    void bindData(List<RecordModel> dataSet);

    void loadData();

    void clear();

    void openDisplayActivity(String folder);
  }
}
