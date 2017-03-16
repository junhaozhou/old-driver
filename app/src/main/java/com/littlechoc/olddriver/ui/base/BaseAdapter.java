package com.littlechoc.olddriver.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @author Junhao Zhou 2017/3/17
 */

public abstract class BaseAdapter<VH extends BaseAdapter.BaseViewHolder> extends RecyclerView.Adapter<VH> {

  public static class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
