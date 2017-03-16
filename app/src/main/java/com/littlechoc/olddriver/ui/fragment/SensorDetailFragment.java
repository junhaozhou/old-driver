package com.littlechoc.olddriver.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.SensorDetailContract;
import com.littlechoc.olddriver.presenter.SensorDetailPresenter;
import com.littlechoc.olddriver.ui.base.BasePagerFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class SensorDetailFragment extends BasePagerFragment implements SensorDetailContract.View {

  public static SensorDetailFragment newInstance(String folder, int type) {

    Bundle args = new Bundle();
    args.putString(Constants.KEY_FOLDER_NAME, folder);
    args.putInt(Constants.KEY_SENSOR_TYPE, type);
    SensorDetailFragment fragment = new SensorDetailFragment();
    fragment.setArguments(args);
    fragment.sensorType = type;
    return fragment;
  }

  private int sensorType;

  private String folderName;

  private SensorDetailContract.Presenter sensorDetailPresenter;

  @BindView(R.id.chart)
  public LineChart chart;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    parseData();
    init();
  }

  private void parseData() {
    Bundle bundle = getArguments();
    if (bundle != null) {
      sensorType = bundle.getInt(Constants.KEY_SENSOR_TYPE);
      folderName = bundle.getString(Constants.KEY_FOLDER_NAME);
    }
  }

  private void init() {
    new SensorDetailPresenter(this);
  }

  @Override
  public int getRootView() {
    return R.layout.fragment_sensor_detail;
  }

  @Override
  public String getTitle() {
    return SensorDetailPresenter.getTitle(sensorType);
  }

  @Override
  public void setPresenter(SensorDetailContract.Presenter presenter) {
    sensorDetailPresenter = presenter;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    sensorDetailPresenter.analyseData(folderName, sensorType);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private List<Entry> values;

  public void initChart() {
    chart.getDescription().setEnabled(false);
    chart.setTouchEnabled(true);
    chart.setDragEnabled(true);
    chart.setScaleEnabled(true);
//    chart.setPinchZoom(true);

    initSet();
  }

  private void initSet() {
    values = new ArrayList<>();
    sensorDetailPresenter.bindDataSet(values);
    LineDataSet set = new LineDataSet(values, "test");
    set.setColor(Color.BLACK);
    set.setLineWidth(1f);
    set.setFormLineWidth(2);

    List<ILineDataSet> dataSets = new ArrayList<>();
    dataSets.add(set);

    LineData data = new LineData(dataSets);

    chart.setData(data);
  }

  public void initYAxis(float max, float min) {

    LimitLine llXAxis = new LimitLine(10f, "Index 10");
    llXAxis.setLineWidth(4f);
    llXAxis.enableDashedLine(10f, 10f, 0f);
    llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
    llXAxis.setTextSize(10f);

    XAxis xAxis = chart.getXAxis();
    xAxis.setAxisMinimum(0);
    xAxis.setAxisMaximum(200);
    xAxis.enableGridDashedLine(10f, 10f, 0f);

    LimitLine ll1 = new LimitLine(150f, "Upper Limit");
    ll1.setLineWidth(4f);
    ll1.enableDashedLine(10f, 10f, 0f);
    ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
    ll1.setTextSize(10f);

    LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
    ll2.setLineWidth(4f);
    ll2.enableDashedLine(10f, 10f, 0f);
    ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
    ll2.setTextSize(10f);

    YAxis leftAxis = chart.getAxisLeft();
    leftAxis.removeAllLimitLines();
    leftAxis.addLimitLine(ll1);
    leftAxis.addLimitLine(ll2);
    leftAxis.setAxisMaximum(max);
    leftAxis.setAxisMinimum(min);
    leftAxis.setDrawZeroLine(true);

    chart.getAxisRight().setEnabled(false);
  }

  @Override
  public void updateChart() {
    chart.getData().notifyDataChanged();
    chart.notifyDataSetChanged();
  }
}
