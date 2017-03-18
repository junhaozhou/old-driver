package com.littlechoc.olddriver.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.BluetoothContract;
import com.littlechoc.olddriver.presenter.BluetoothPresenter;
import com.littlechoc.olddriver.ui.adapter.BluetoothListAdapter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.base.BaseAdapter;
import com.littlechoc.olddriver.ui.view.DividerItemDecoration;
import com.littlechoc.olddriver.ui.view.EmptyView;
import com.littlechoc.olddriver.ui.view.RecyclerViewEx;
import com.littlechoc.olddriver.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/18
 */

public class BluetoothActivity extends BaseActivity implements BluetoothContract.View {

  public static final String TAG = "BluetoothActivity";

  @BindView(R.id.root)
  View rootView;

  @BindView(R.id.title_bar)
  Toolbar titleBar;

  @BindView(R.id.auto_connect_switch)
  Switch autoConnectSwitch;

  @BindView(R.id.title_paired_devices)
  TextView pairedDevicesTitle;

  @BindView(R.id.paired_devices_list)
  RecyclerView pairedDevicesList;

  @BindView(R.id.search_progress)
  ProgressBar searchProgress;

  @BindView(R.id.found_devices_list)
  RecyclerViewEx foundDevicesList;

  @BindView(R.id.empty_view)
  EmptyView emptyView;

  @BindView(R.id.search_bluetooth)
  FloatingActionButton searchBluetooth;

  private BluetoothContract.Presenter bluetoothPresenter;

  private BluetoothListAdapter pairedDevicesAdapter;

  private BluetoothListAdapter foundDevicesAdapter;

  private List<BluetoothDevice> pairedDevices;

  private List<BluetoothDevice> foundDevices;

  @Override
  public int getRootView() {
    return R.layout.activity_bluetooth;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
    initView();
    bluetoothPresenter.init(pairedDevices, foundDevices);
    bluetoothPresenter.search(true);
  }

  private void init() {
    pairedDevices = new ArrayList<>();
    foundDevices = new ArrayList<>();
    new BluetoothPresenter(this);
  }

  private void initView() {
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    pairedDevicesAdapter = new BluetoothListAdapter(pairedDevices);
    pairedDevicesList.setLayoutManager(new LinearLayoutManager(this));
    pairedDevicesList.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
    pairedDevicesList.setAdapter(pairedDevicesAdapter);
    pairedDevicesAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position) {
        bluetoothPresenter.connect(pairedDevices.get(position));
      }
    });

    foundDevicesAdapter = new BluetoothListAdapter(foundDevices);
    foundDevicesList.setLayoutManager(new LinearLayoutManager(this));
    foundDevicesList.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
    foundDevicesList.setAdapter(foundDevicesAdapter);
    foundDevicesAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position) {
        bluetoothPresenter.connect(foundDevices.get(position));
      }
    });

    searchProgress.setVisibility(View.GONE);
    searchBluetooth.show();

    emptyView.setEmptyText("没有发现可用设备");
  }

  @OnClick(R.id.switch_container)
  void onSwitchContainerClick() {
    Logger.i(TAG, "switch container click");
    autoConnectSwitch.toggle();
    onToggleAutoConnectSwitch();
  }

  @OnClick(R.id.auto_connect_switch)
  void onToggleAutoConnectSwitch() {
    Logger.i(TAG, "switch is checked [%s]", autoConnectSwitch.isChecked());
    bluetoothPresenter.setAutoConnectEnable(autoConnectSwitch.isChecked());
  }

  @OnClick(R.id.search_bluetooth)
  void onSearchClick() {
    bluetoothPresenter.search(false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    bluetoothPresenter.onDestroy();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    bluetoothPresenter.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public void setPresenter(BluetoothContract.Presenter presenter) {
    bluetoothPresenter = presenter;
  }

  @Override
  public void onBluetoothDisable() {
    Snackbar.make(rootView, "蓝牙未开启", 0).setAction("开启", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        bluetoothPresenter.openBluetooth();
      }
    }).show();
  }

  @Override
  public void initAutoConnectSwitchState(boolean switched) {
    autoConnectSwitch.setChecked(switched);
  }

  @Override
  public void updatePairedDevicesList() {
    if (pairedDevices.size() == 0) {
      pairedDevicesTitle.setVisibility(View.GONE);
      pairedDevicesList.setVisibility(View.GONE);
    } else {
      pairedDevicesTitle.setVisibility(View.VISIBLE);
      pairedDevicesList.setVisibility(View.VISIBLE);
      pairedDevicesAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void updateFoundDevicesList() {
    foundDevicesAdapter.notifyDataSetChanged();
  }

  @Override
  public void onStartSearch() {
    searchProgress.setVisibility(View.VISIBLE);
    searchBluetooth.hide();
    emptyView.setEmptyText("正在搜索");
  }

  @Override
  public void onStopSearch() {
    searchBluetooth.show();
    searchProgress.setVisibility(View.GONE);
    emptyView.setEmptyText("没有发现可用设备");
  }

  @Override
  public void onStartConnect() {

  }

  @Override
  public void onRequestPassword() {

  }

  @Override
  public void onConnectError() {

  }
}
