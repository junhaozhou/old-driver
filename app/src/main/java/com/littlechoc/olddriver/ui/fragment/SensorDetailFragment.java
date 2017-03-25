package com.littlechoc.olddriver.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
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

  public static final int STYLE_LIMIT = 0x00000001;

  public static final int STYLE_UNLIMITED = 0x00000000;

  public static final int STYLE_MERGE = 0x00000011;

  public static final int STYLE_SPLIT = 0x00000010;

  public static SensorDetailFragment newInstance(String folder, Constants.SensorType type) {

    Bundle args = new Bundle();
    args.putString(Constants.KEY_FOLDER_NAME, folder);
    args.putSerializable(Constants.KEY_SENSOR_TYPE, type);
    SensorDetailFragment fragment = new SensorDetailFragment();
    fragment.setArguments(args);
    fragment.sensorType = type;
    return fragment;
  }

  private Constants.SensorType sensorType;

  private String folderName;

  private SensorDetailContract.Presenter sensorDetailPresenter;

  private int currentStyle = STYLE_UNLIMITED;

  @BindView(R.id.chart_x)
  public LineChart chartX;

  @BindView(R.id.chart_y)
  public LineChart chartY;

  @BindView(R.id.chart_z)
  public LineChart chartZ;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    parseData();
    init();
  }

  private void parseData() {
    Bundle bundle = getArguments();
    if (bundle != null) {
      sensorType = (Constants.SensorType) bundle.getSerializable(Constants.KEY_SENSOR_TYPE);
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

  private List<Entry> xValues;

  private List<Entry> yValues;

  private List<Entry> zValues;

  public void initChart() {
    // x
    initChart(chartX);
    initChart(chartY);
    initChart(chartZ);

    initSet();
  }

  private void initChart(LineChart chart) {
    chart.getDescription().setEnabled(false);
    chart.setTouchEnabled(true);
    chart.setDragEnabled(true);
    chart.setScaleEnabled(true);
    chart.setPinchZoom(false);
    chart.setDrawGridBackground(false);

    chart.getAxisLeft().setDrawGridLines(false);
    chart.getAxisRight().setEnabled(false);
    chart.getXAxis().setDrawGridLines(true);
    chart.getXAxis().setDrawAxisLine(false);

    chart.invalidate();
  }

  private void initSet() {
    xValues = new ArrayList<>();
    yValues = new ArrayList<>();
    zValues = new ArrayList<>();
    sensorDetailPresenter.bindDataSet(xValues, yValues, zValues);

    initSet(xValues, "x", chartX);
    initSet(yValues, "y", chartY);
    initSet(zValues, "z", chartZ);
  }

  private void initSet(List<Entry> dataSet, String label, LineChart chart) {
    LineDataSet set = new LineDataSet(dataSet, label);
    set.setColor(Color.BLACK);
    set.setLineWidth(0.5f);
    set.setDrawValues(false);
    set.setDrawCircles(false);
    set.setMode(LineDataSet.Mode.LINEAR);
    set.setDrawFilled(false);

    List<ILineDataSet> dataSets = new ArrayList<>();
    dataSets.add(set);

    LineData data = new LineData(dataSets);

    chart.setData(data);
  }

  @Override
  public void initXAxis(float rangeMax, float rangeMin, float range) {
    initAxis(chartX, rangeMax, rangeMin, range);
  }

  public void initYAxis(float rangeMax, float rangeMin, float range) {
    initAxis(chartY, rangeMax, rangeMin, range);
  }

  public void initZAxis(float rangeMax, float rangeMin, float range) {
    initAxis(chartZ, rangeMax, rangeMin, range);
  }

  private void initAxis(LineChart chart, float max, float min, float range) {
    XAxis xAxis = chart.getXAxis();
    xAxis.setAxisMinimum(0);
    xAxis.setAxisMaximum(range);
    xAxis.enableGridDashedLine(10f, 10f, 0f);

    YAxis leftAxis = chart.getAxisLeft();
    leftAxis.removeAllLimitLines();
    leftAxis.setAxisMaximum(max);
    leftAxis.setAxisMinimum(min);
    leftAxis.setDrawZeroLine(true);

    chart.getAxisRight().setEnabled(false);
  }

  @Override
  public void updateDataSet() {
    updateDataSet(chartX);
    updateDataSet(chartY);
    updateDataSet(chartZ);
    applyStyle();
  }

  public void updateDataSet(LineChart chart) {
    chart.getData().notifyDataChanged();
    chart.notifyDataSetChanged();
  }

  public void changeChartDisplayStyle(int style) {
    if (currentStyle == style) {
      return;
    }
    currentStyle = style;
    applyStyle();
  }

  private void applyStyle() {
    if (currentStyle == STYLE_LIMIT) {
      limit();
    } else if (currentStyle == STYLE_UNLIMITED) {
      reset();
    } else if (currentStyle == STYLE_MERGE) {
      mergeChart();
    } else if (currentStyle == STYLE_SPLIT) {
      splitChart();
    }
  }

  private void mergeChart() {

  }

  private void splitChart() {

  }

  private void limit() {
    chartX.setVisibleXRangeMaximum(500);
    chartY.setVisibleXRangeMaximum(500);
    chartZ.setVisibleXRangeMaximum(500);
    invalidateChart();
  }

  private void reset() {
    chartX.fitScreen();
    chartY.fitScreen();
    chartZ.fitScreen();
    invalidateChart();
  }

  private void invalidateChart() {
    chartX.invalidate();
    chartY.invalidate();
    chartZ.invalidate();
  }
}
