package com.littlechoc.olddriver.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @author Junhao Zhou 2017/3/17
 */

public abstract class BaseAdapter<VH extends BaseAdapter.BaseViewHolder> extends RecyclerView.Adapter<VH> {

  public interface OnItemClickListener {

    void onItemClick(int position);

  }

  public OnItemClickListener onItemClickListener;

  private boolean enableItemClick = true;

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public void setEnableItemClick(boolean enable) {
    this.enableItemClick = enable;
  }

  public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      if (enableItemClick) {
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (onItemClickListener != null) {
              onItemClickListener.onItemClick(getAdapterPosition());
            }
          }
        });
      } else {
        itemView.setOnClickListener(null);
      }
    }
  }
}
