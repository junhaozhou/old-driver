package com.littlechoc.olddriver.ui.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.ObdRecordContract;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.presenter.ObdRecordPresenter;
import com.littlechoc.olddriver.presenter.RecordPresenter;
import com.littlechoc.olddriver.ui.adapter.ObdDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/4/28
 */

public class RealTimeObdDisplayFragment extends RealTimeDisplayFragment implements ObdRecordContract.View {

  @BindView(R.id.obd_data_list)
  RecyclerView obdDataList;

  private ObdDataAdapter obdDataAdapter;

  private List<ObdModel> obdModelList;

  private ObdRecordContract.Presenter recordPresenter;

  public static RealTimeObdDisplayFragment newInstance() {

    Bundle args = new Bundle();

    RealTimeObdDisplayFragment fragment = new RealTimeObdDisplayFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
  }

  private void init() {
    new ObdRecordPresenter(this);
    obdModelList = new ArrayList<>();
    recordPresenter.attachObdModelList(obdModelList);
  }

  @Override
  public int getRootView() {
    return R.layout.fragment_obd_display;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    obdDataAdapter = new ObdDataAdapter(obdModelList);
    obdDataList.setLayoutManager(new LinearLayoutManager(getContext()));
    obdDataList.setAdapter(obdDataAdapter);
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
  public void setPresenter(ObdRecordContract.Presenter presenter) {
    this.recordPresenter = presenter;
  }

  @Override
  public void updateObdData() {
    obdDataAdapter.notifyDataSetChanged();
  }

  private List<BluetoothDevice> deviceList;

  @Override
  public void showBluetoothDevice(List<BluetoothDevice> devices) {
    this.deviceList = devices;
    String[] devicesArray = new String[deviceList.size()];
    for (int i = 0; i < deviceList.size(); i++) {
      devicesArray[i] = deviceList.get(i).getName();
    }
    AlertDialog.Builder builder
            = new AlertDialog.Builder(getContext())
            .setTitle("选择蓝牙设备")
            .setItems(devicesArray, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                recordPresenter.connectBluetooth(deviceList.get(which));
              }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialog) {
                recordPresenter.connectBluetooth(null);
              }
            });
    builder.show();
  }

  @Override
  public String getTitle() {
    return "OBD";
  }
}
