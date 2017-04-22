package com.littlechoc.olddriver.contract;

import android.bluetooth.BluetoothDevice;

import com.littlechoc.olddriver.contract.base.BasePresenter;
import com.littlechoc.olddriver.contract.base.BaseView;
import com.littlechoc.olddriver.model.sensor.ObdModel;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/14
 */

public interface TrackContract {

  interface Presenter extends BasePresenter {

    void stopTrack();

    void startTrack();

    void selectBluetoothDevice();

    void connectBluetooth(BluetoothDevice device);

    void openDisplayActivity();

    void setIfLogSensor(boolean ifLog);

    void beginMark();

    void saveMarker(int type, boolean last);

    void attachObdModelList(List<ObdModel> obdModels);

    boolean isTracking();
  }

  interface View extends BaseView<Presenter> {

    void showAnalyseSnack();

    void showMarkerBottomSheet(boolean isLast);

    void showBluetoothDevice(List<BluetoothDevice> devices);

    void updateObdData();
  }
}
