package com.littlechoc.olddriver.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.SensorRecordContract;
import com.littlechoc.olddriver.presenter.RecordPresenter;
import com.littlechoc.olddriver.presenter.SensorRecordPresenter;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/4/28
 */

public class RealTimeSensorDisplayFragment extends RealTimeDisplayFragment implements SensorRecordContract.View {

  public static final String TAG = "RealTimeSensorDisplayFragment";

  private SensorRecordContract.Presenter recordPresenter;

  private static final float RATE = 1.1f;

  private static final float MAX = 0.1f;

  private static final int WINDOW = 5;

  private static final int RANGE = WINDOW * Constants.SENSOR_SIMPLE_RATE;

  @BindView(R.id.chart_combine)
  public LineChart chartCombine;

  @BindView(R.id.chart_x)
  public LineChart chartX;

  @BindView(R.id.chart_y)
  public LineChart chartY;

  @BindView(R.id.chart_z)
  public LineChart chartZ;

  private boolean displayCombine = false;

  public static RealTimeSensorDisplayFragment newInstance() {

    Bundle args = new Bundle();

    RealTimeSensorDisplayFragment fragment = new RealTimeSensorDisplayFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
  }

  private void init() {
    new SensorRecordPresenter(this);
    setHasOptionsMenu(true);
  }

  @Override
  public int getRootView() {
    return R.layout.fragment_sensor_display;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    initChart();
  }

  @Override
  public void onDestroy() {
    recordPresenter.onDestroy();
    super.onDestroy();
  }

  @Override
  public RecordPresenter getPresenter() {
    return recordPresenter;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_sensor_display_fragment, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.menu_display_style);
    if (item != null) {
      if (displayCombine) {
        item.setTitle(R.string.menu_display_split);
        item.setIcon(R.drawable.ic_split);
      } else {
        item.setTitle(R.string.menu_display_merge);
        item.setIcon(R.drawable.ic_merge);
      }
    }

    item = menu.findItem(R.id.menu_sensor_log);
    if (item != null) {
      if (recordPresenter.ifLogSensor()) {
        item.setTitle(R.string.menu_disable_sensor_log);
        item.setIcon(R.drawable.ic_log_off);
      } else {
        item.setTitle(R.string.menu_enable_sensor_log);
        item.setIcon(R.drawable.ic_log);
      }
    }
    super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_sensor_log:
        recordPresenter.toggleLogSensor();
        getActivity().supportInvalidateOptionsMenu();
        break;
      case R.id.menu_display_style:
        displayCombine = !displayCombine;
        update();
        getActivity().supportInvalidateOptionsMenu();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void setPresenter(SensorRecordContract.Presenter presenter) {
    recordPresenter = presenter;
  }

  @Override
  public String getTitle() {
    return "Sensor";
  }

  @Override
  public void clear() {
    setX.clear();
    setY.clear();
    setZ.clear();
    chartCombine.clear();
    chartCombine.moveViewToX(0);
    chartX.clear();
    chartX.getXAxis().resetAxisMaximum();
    chartX.moveViewToX(0);
    chartY.clear();
    chartY.moveViewToX(0);
    chartZ.clear();
    chartZ.moveViewToX(0);
    initData(chartCombine, setX, setY, setZ);
    initData(chartX, setX);
    initData(chartY, setY);
    initData(chartZ, setZ);
    chartCombine.notifyDataSetChanged();
    chartX.notifyDataSetChanged();
    chartY.notifyDataSetChanged();
    chartZ.notifyDataSetChanged();
    chartCombine.invalidate();
    chartX.invalidate();
    chartY.invalidate();
    chartZ.invalidate();
//    initChart();
  }

  @Override
  public void onNewData(float x, float y, float z) {
    if (!displayCombine) {
      addEntry(chartX, x);
      addEntry(chartY, y);
      addEntry(chartZ, z);
    } else {
      addEntry(x, y, z);
    }
  }

  private void update() {
    if (displayCombine) {
      chartCombine.setVisibility(View.VISIBLE);
      chartCombine.notifyDataSetChanged();
      chartX.setVisibility(View.GONE);
      chartY.setVisibility(View.GONE);
      chartZ.setVisibility(View.GONE);
    } else {
      chartCombine.setVisibility(View.GONE);
      chartX.setVisibility(View.VISIBLE);
      chartX.notifyDataSetChanged();
      chartY.setVisibility(View.VISIBLE);
      chartY.notifyDataSetChanged();
      chartZ.setVisibility(View.VISIBLE);
      chartZ.notifyDataSetChanged();
    }
  }

  private void initChart() {
    initChart(chartX, setX);
    initChart(chartY, setY);
    initChart(chartZ, setZ);
    initCombineChart();
    update();
  }

  private final ILineDataSet setX = createSet("x", ColorTemplate.MATERIAL_COLORS[0]);
  private final ILineDataSet setY = createSet("y", ColorTemplate.MATERIAL_COLORS[1]);
  private final ILineDataSet setZ = createSet("z", ColorTemplate.MATERIAL_COLORS[2]);

  private void initChart(LineChart chart, ILineDataSet dataSet) {
    initChartNormal(chart);

    LineData data = new LineData();
    data.setValueTextColor(Color.WHITE);

    // add empty data
    initData(chart, dataSet);

    // get the legend (only possible after setting data)
    Legend l = chart.getLegend();


    // modify the legend ...
    l.setForm(Legend.LegendForm.LINE);
    l.setTextColor(Color.WHITE);
  }

  private void initData(LineChart chart, ILineDataSet... dataSets) {
    LineData data = new LineData();
    data.setValueTextColor(Color.WHITE);
    for (ILineDataSet dataSet : dataSets) {
      data.addDataSet(dataSet);
    }
    chart.setData(data);
  }

  private void initCombineChart() {
    initChartNormal(chartCombine);

    LineData data = new LineData();
    data.setValueTextColor(Color.WHITE);

    // add empty data
    initData(chartCombine, setX, setY, setZ);

    // get the legend (only possible after setting data)
    Legend l = chartCombine.getLegend();

    // modify the legend ...
    l.setForm(Legend.LegendForm.LINE);
    l.setTextColor(Color.WHITE);
  }

  private void initChartNormal(LineChart chart) {
    //    mChart.setOnChartValueSelectedListener(this);

    // enable description text
    chart.getDescription().setEnabled(false);

    // enable touch gestures
    chart.setTouchEnabled(true);

    // enable scaling and dragging
    chart.setDragEnabled(true);
    chart.setScaleEnabled(true);
    chart.setDrawGridBackground(false);

    // if disabled, scaling can be done on x- and y-axis separately
    chart.setPinchZoom(true);

    // set an alternative background color
    chart.setBackgroundColor(Color.LTGRAY);

    XAxis xl = chart.getXAxis();
    xl.setTextColor(Color.WHITE);
    xl.setDrawGridLines(false);
    xl.setAvoidFirstLastClipping(true);
    xl.setEnabled(true);
    xl.setValueFormatter(new IAxisValueFormatter() {
      @Override
      public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf(value / Constants.SENSOR_SIMPLE_RATE);
      }
    });

    YAxis leftAxis = chart.getAxisLeft();
    leftAxis.setTextColor(Color.WHITE);
    leftAxis.setAxisMaximum(MAX);
    leftAxis.setAxisMinimum(-MAX);
    leftAxis.setDrawGridLines(true);

    YAxis rightAxis = chart.getAxisRight();
    rightAxis.setEnabled(false);
  }

  private void addEntry(LineChart chart, float value) {

    LineData data = chart.getData();

    if (data != null) {
      Logger.d(TAG, "#addEntry");
      ILineDataSet set = data.getDataSetByIndex(0);

      YAxis leftAxis = chart.getAxisLeft();
      float max = leftAxis.getAxisMaximum();
      if (Math.abs(value) * RATE > max) {
        leftAxis.setAxisMaximum(Math.abs(value) * RATE);
        leftAxis.setAxisMinimum(-(Math.abs(value) * RATE));
      }

      data.addEntry(new Entry(set.getEntryCount(), value), 0);
      data.notifyDataChanged();
      // let the chart know it's data has changed
      chart.notifyDataSetChanged();

      // limit the number of visible entries
      chart.setVisibleXRangeMaximum(RANGE);
      // mChart.setVisibleYRange(30, AxisDependency.LEFT);

      // move to the latest entry
      chart.moveViewToX(data.getEntryCount());
      Logger.d(TAG, "#addEntry finish");
    }
  }

  private void addEntry(float x, float y, float z) {
    LineData data = chartCombine.getData();

    if (data != null) {

      YAxis leftAxis = chartCombine.getAxisLeft();
      float max = leftAxis.getAxisMaximum();
      if (Math.abs(z) * RATE > max) {
        leftAxis.setAxisMaximum(Math.abs(z) * RATE);
        leftAxis.setAxisMinimum(-(Math.abs(z) * RATE));
      }

      data.addEntry(new Entry(setX.getEntryCount(), x), 0);
      data.addEntry(new Entry(setY.getEntryCount(), y), 1);
      data.addEntry(new Entry(setZ.getEntryCount(), z), 2);
      data.notifyDataChanged();

      // let the chart know it's data has changed
      chartCombine.notifyDataSetChanged();

      // limit the number of visible entries
      chartCombine.setVisibleXRangeMaximum(RANGE);
      // mChart.setVisibleYRange(30, AxisDependency.LEFT);

      // move to the latest entry
      chartCombine.moveViewToX(data.getEntryCount());
    }
  }

  private LineDataSet createSet(String name, int color) {
    LineDataSet set = new LineDataSet(null, name);
    set.setAxisDependency(YAxis.AxisDependency.LEFT);
    set.setColor(color);
    set.setLineWidth(1f);
    set.setDrawCircles(false);
    set.setFillAlpha(65);
    set.setFillColor(color);
    set.setHighLightColor(Color.rgb(244, 117, 117));
    set.setDrawValues(false);
    return set;
  }
}
