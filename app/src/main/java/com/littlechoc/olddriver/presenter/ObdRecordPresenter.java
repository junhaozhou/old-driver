package com.littlechoc.olddriver.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.littlechoc.olddriver.contract.ObdRecordContract;
import com.littlechoc.olddriver.dao.ObdDao;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.utils.SpUtils;
import com.littlechoc.olddriver.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Junhao Zhou 2017/4/28
 */

public class ObdRecordPresenter implements ObdRecordContract.Presenter {

  private ObdRecordContract.View recordView;

  private List<ObdModel> obdModelList;

  private ObdDao obdDao;

  private String folder;

  private BluetoothAdapter bluetoothAdapter;

  private BluetoothDevice bluetoothDevice;

  public ObdRecordPresenter(ObdRecordContract.View recordView) {
    this.recordView = recordView;
    recordView.setPresenter(this);
    obdDao = new ObdDao();
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  }

  @Override
  public void attachObdModelList(List<ObdModel> obdModels) {
    this.obdModelList = obdModels;
  }

  @Override
  public void connectBluetooth(BluetoothDevice device) {
    bluetoothDevice = device;
    if (bluetoothDevice != null) {
      SpUtils.saveBluetoothAddress(bluetoothDevice.getAddress());
      start();
    }
  }

  @Override
  public void prepare(String folder) {
    this.folder = folder;
  }

  @Override
  public void start() {
    if (bluetoothDevice == null) {
      String address = SpUtils.getSavedBluetoothAddress();
      if (TextUtils.isEmpty(address)) {
        selectBluetoothDevice();
        return;
      }
      if (bluetoothAdapter != null) {
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
      }
    }
    if (bluetoothDevice == null) {
      return;
    }
    obdModelList.clear();
    recordView.updateObdData();
    if (bluetoothDevice != null && obdDao != null) {
      obdDao.start(bluetoothDevice, folder, new ObdDao.Callback() {
        @Override
        public void onError(String msg) {
          ToastUtils.show(msg);
        }

        @Override
        public void onCommandResult(ObdModel obdModel) {
          if (obdModel == null) {
            return;
          }
          boolean hasExist = false;
          for (ObdModel model : obdModelList) {
            if (model.command.equals(obdModel.command)) {
              model.data = obdModel.data;
              model.formattedData = obdModel.formattedData;
              model.time = obdModel.time;
              hasExist = true;
              break;
            }
          }
          if (!hasExist) {
            obdModelList.add(obdModel);
          }
          recordView.updateObdData();
        }
      });
    }
  }

  private void selectBluetoothDevice() {
    if (bluetoothAdapter != null) {
      Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
      List<BluetoothDevice> deviceList = new ArrayList<>(devices.size());
      deviceList.addAll(devices);
      recordView.showBluetoothDevice(deviceList);
    }
  }

  @Override
  public void stop() {
    if (bluetoothDevice != null && obdDao != null) {
      obdDao.stop();
    }
  }

  @Override
  public void onDestroy() {

  }
}
