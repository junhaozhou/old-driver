package com.littlechoc.olddriver.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.ui.base.BaseAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/3/18
 */

public class BluetoothListAdapter extends BaseAdapter<BluetoothListAdapter.ViewHolder> {

  private final List<BluetoothDevice> deviceList;

  public BluetoothListAdapter(List<BluetoothDevice> deviceList) {
    this.deviceList = deviceList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_bluetooth, parent, false));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    BluetoothDevice device = deviceList.get(position);
    if (TextUtils.isEmpty(device.getName())) {
      holder.bluetoothName.setText(device.getAddress());
    } else {
      holder.bluetoothName.setText(device.getName());
    }
  }

  @Override
  public int getItemCount() {
    return deviceList == null ? 0 : deviceList.size();
  }

  class ViewHolder extends BaseAdapter.BaseViewHolder {

    @BindView(R.id.bluetooth_name)
    TextView bluetoothName;

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }

}
