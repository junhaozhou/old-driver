package com.littlechoc.olddriver.ui.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.model.RecordModel;
import com.littlechoc.olddriver.ui.base.BaseAdapter;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;


/**
 * @author Junhao Zhou 2017/3/17
 */

public class HistoryAdapter extends BaseAdapter<HistoryAdapter.ViewHolder> {

  public interface OnHistoryItemClickListener {
    void onClickHistoryItem(int position);
  }

  private final List<RecordModel> records;

  private OnHistoryItemClickListener listener;

  public HistoryAdapter(List<RecordModel> records) {
    this.records = records;
  }

  @Override
  public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false));
  }

  @Override
  public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
    Context context = holder.itemView.getContext();
    RecordModel record = records.get(position);
    holder.name.setText(record.getName());
    StringBuilder detail = new StringBuilder();
    detail.append(String.format(Locale.CHINA, "size : %s",
            Formatter.formatFileSize(context, record.getSize())));
    holder.detail.setText(detail);
  }

  @Override
  public int getItemCount() {
    return records == null ? 0 : records.size();
  }

  public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
    this.listener = listener;
  }

  class ViewHolder extends BaseAdapter.BaseViewHolder {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.detail)
    TextView detail;

    public ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (listener != null) {
            listener.onClickHistoryItem(getAdapterPosition());
          }
        }
      });
    }
  }
}
