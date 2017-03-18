package com.littlechoc.olddriver.contract;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/18
 */

public class BluetoothContract {

  public interface View extends BaseView<Presenter> {

    void onBluetoothDisable();

    void initAutoConnectSwitchState(boolean switched);

    void updatePairedDevicesList();

    void updateFoundDevicesList();

    void onStartSearch();

    void onStopSearch();

    void onStartConnect();

    void onRequestPassword();

    void onConnectError();
  }

  public interface Presenter extends BasePresenter {

    void init(List<BluetoothDevice> pairedDevices, List<BluetoothDevice> foundDevices);

    void search(boolean auto);

    void connect(BluetoothDevice device);

    void connectWithPassword(BluetoothDevice device, String password);

    void setAutoConnectEnable(boolean enable);

    void openBluetooth();

    void onActivityResult(int request, int result, Intent data);
  }
}
