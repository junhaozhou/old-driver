package com.littlechoc.olddriver.contract;

import com.github.mikephil.charting.data.Entry;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public interface SensorDetailContract {

  interface View extends BaseView<Presenter> {

    void initXAxis(float max, float min, float range);

    void initYAxis(float max, float min, float range);

    void initZAxis(float max, float min, float range);

    void updateDataSet();
  }

  interface Presenter extends BasePresenter {

    void bindDataSet(List<Entry> xSet, List<Entry> ySet, List<Entry> zSet);

    void analyseData(String folder, Constants.SensorType type);

    void filterData(boolean filter, Constants.SensorType type);
  }
}
