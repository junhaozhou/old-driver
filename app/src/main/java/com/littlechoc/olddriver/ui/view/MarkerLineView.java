package com.littlechoc.olddriver.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.ui.base.BaseAdapter;
import com.littlechoc.olddriver.utils.MobileUtils;

/**
 * @author Junhao Zhou 2017/3/26
 */

public class MarkerLineView extends LinearLayout {

  private BaseAdapter.OnItemClickListener onItemClickListener;

  private TextView leftMarker;

  private TextView rightMarker;

  public MarkerLineView(Context context) {
    this(context, null);

  }

  public MarkerLineView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MarkerLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setOrientation(HORIZONTAL);
    setPadding(MobileUtils.dp2px(getContext(), 8), 0, MobileUtils.dp2px(getContext(), 8), 0);

    LayoutInflater.from(getContext()).inflate(R.layout.item_marker, this);
    leftMarker = (TextView) findViewById(R.id.left_mark_name);
    rightMarker = (TextView) findViewById(R.id.right_mark_name);

  }

  public void setOnItemClickListener(BaseAdapter.OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public void setMarker(String left, String right, final int row) {
    leftMarker.setText(left);
    rightMarker.setText(right);
    leftMarker.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(2 * row);
        }
      }
    });

    rightMarker.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(2 * row + 1);
        }
      }
    });
  }
}
