package com.littlechoc.olddriver.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.littlechoc.olddriver.Application;
import com.littlechoc.olddriver.contract.BluetoothContract;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.utils.Logger;
import com.littlechoc.olddriver.utils.PermissionUtils;
import com.littlechoc.olddriver.utils.SpUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Junhao Zhou 2017/3/18
 */

public class BluetoothPresenter implements BluetoothContract.Presenter {

  public static final String TAG = "BluetoothPresenter";

  private List<BluetoothDevice> pairedDevices;

  private List<BluetoothDevice> foundDevices;

  private BluetoothContract.View bluetoothView;

  private BluetoothAdapter bluetoothAdapter;

  public BluetoothPresenter(BluetoothContract.View bluetoothView) {
    this.bluetoothView = bluetoothView;
    bluetoothView.setPresenter(this);
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothDevice.ACTION_UUID);
    filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
    bluetoothView.getContext().registerReceiver(bluetoothReceiver, filter);
  }

  @Override
  public void init(List<BluetoothDevice> pairedDevices, List<BluetoothDevice> foundDevices) {
    this.pairedDevices = pairedDevices;
    this.foundDevices = foundDevices;


    bluetoothView.initAutoConnectSwitchState(SpUtils.getAutoConnectBluetoothFlag());
    if (bluetoothAdapter == null) {
      return;
    }
    if (!bluetoothAdapter.isEnabled()) {
      bluetoothView.onBluetoothDisable();
    }

    checkBondedDevices();
  }

  private void checkBondedDevices() {
    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
    pairedDevices.clear();
    for (BluetoothDevice device : devices) {
      pairedDevices.add(device);
    }
    bluetoothView.updatePairedDevicesList();
  }

  @Override
  public void search(boolean first) {
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
      bluetoothView.onBluetoothDisable();
      return;
    }
    if (first && !SpUtils.getAutoConnectBluetoothFlag()) {
      return;
    }
    PermissionUtils.requestPermission((BaseActivity) bluetoothView.getContext(),
            PermissionUtils.BLUETOOTH_PERMISSION,
            new PermissionUtils.OnPermissionRequestCallback() {

              @Override
              public void onPermissionDenied() {
                bluetoothView.onStopSearch();
              }

              @Override
              public void onPermissionGranted() {
                if (!bluetoothAdapter.isDiscovering()) {
                  foundDevices.clear();
                  checkBondedDevices();
                  if (bluetoothAdapter.startDiscovery()) {
                    bluetoothView.onStartSearch();
                    Application.getInstance().runOnUiThreadDelayed(cancelDiscovery, DISCOVERY_TIMEOUT);
                  } else {
                    bluetoothView.onStopSearch();
                  }
                }
              }
            });
  }

  private static final UUID MY_UUID = UUID
          .fromString("00001101-0000-1000-8000-00805F9B34FB");

  @Override
  public void connect(final BluetoothDevice device) {
    if (device == null) {
      return;
    }
    if (!bluetoothAdapter.isEnabled()) {
      bluetoothView.onBluetoothDisable();
      return;
    }
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          bluetoothView.onStartConnect();
          device.setPin("1234".getBytes());
          BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
          socket.connect();
          Logger.i(TAG, "connect success");
        } catch (IOException e) {
          e.printStackTrace();
          bluetoothView.onConnectError();
        }
      }
    }).start();
  }

  @Override
  public void setAutoConnectEnable(boolean enable) {
    SpUtils.setAutoConnectBluetoothFlag(enable);
  }

  @Override
  public void openBluetooth() {
    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    ((BaseActivity) bluetoothView.getContext()).startActivityForResult(intent, 100);
  }

  @Override
  public void onActivityResult(int request, int result, Intent data) {
    if (request == 100 && result == Activity.RESULT_OK) {
      Logger.i(TAG, "open bluetooth success");
    }
  }

  @Override
  public void onDestroy() {
    if (bluetoothReceiver != null) {
      bluetoothView.getContext().unregisterReceiver(bluetoothReceiver);
      bluetoothReceiver = null;
    }
    Application.getInstance().removeCallback(cancelDiscovery);
  }

  private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Logger.d(TAG, "action = %s", action);
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (!foundDevices.contains(device) && !pairedDevices.contains(device)) {
          foundDevices.add(device);
        }
        bluetoothView.updateFoundDevicesList();
      }
    }
  };

  private static final long DISCOVERY_TIMEOUT = 13 * 1000;

  private Runnable cancelDiscovery = new Runnable() {
    @Override
    public void run() {
      if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
        Logger.i(TAG, "bluetooth is discovering before cancel");
        bluetoothAdapter.cancelDiscovery();
      }
      bluetoothView.onStopSearch();
    }
  };
}
