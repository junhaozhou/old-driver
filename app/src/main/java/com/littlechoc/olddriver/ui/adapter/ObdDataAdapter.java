package com.littlechoc.olddriver.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.ui.base.BaseAdapter;
import com.littlechoc.olddriver.utils.DateUtils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/4/23
 */

public class ObdDataAdapter extends BaseAdapter<ObdDataAdapter.ViewHolder> {

  private List<ObdModel> obdModelList;

  public ObdDataAdapter(List<ObdModel> obdModelList) {
    this.obdModelList = obdModelList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_obd_data, parent, false));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    ObdModel obdModel = obdModelList.get(position);
    holder.commandName.setText(obdModel.name);
    holder.commandValue.setText(String.format(Locale.CHINA, "%s(%s)\n%s",
            obdModel.formattedData, obdModel.data,
            DateUtils.time2Date(DateUtils.PATTERN_DEFAULT, obdModel.time)));
  }

  @Override
  public int getItemCount() {
    return obdModelList == null ? 0 : obdModelList.size();
  }

  public class ViewHolder extends BaseAdapter.BaseViewHolder {

    @BindView(R.id.name)
    TextView commandName;

    @BindView(R.id.value)
    TextView commandValue;

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
