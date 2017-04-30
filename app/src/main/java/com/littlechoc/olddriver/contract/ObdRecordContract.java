package com.littlechoc.olddriver.contract;

import android.bluetooth.BluetoothDevice;

import com.littlechoc.olddriver.contract.base.BaseView;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.presenter.RecordPresenter;

import java.util.List;

/**
 * @author Junhao Zhou 2017/4/28
 */

public interface ObdRecordContract {

  interface Presenter extends RecordPresenter {

    void attachObdModelList(List<ObdModel> obdModels);

    void connectBluetooth(BluetoothDevice device);
  }

  interface View extends BaseView<Presenter> {

    void updateObdData();

    void showBluetoothDevice(List<BluetoothDevice> devices);
  }

}
