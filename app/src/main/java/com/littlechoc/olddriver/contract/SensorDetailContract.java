package com.littlechoc.olddriver.contract;

import com.github.mikephil.charting.data.Entry;
import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public interface SensorDetailContract {

  interface View extends BaseView<Presenter> {

    void initChart();

    void initYAxis(float max, float min);

    void updateChart();
  }

  interface Presenter extends BasePresenter {

    void bindDataSet(List<Entry> dataSet);

    void analyseData(String folder, int type);
  }
}
