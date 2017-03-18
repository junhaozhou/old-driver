package com.littlechoc.olddriver.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.littlechoc.olddriver.R;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class EmptyView extends RelativeLayout {

  private TextView emptyText;

  public EmptyView(Context context) {
    this(context, null);
  }

  public EmptyView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setId(R.id.empty_view);
    LayoutInflater.from(getContext()).inflate(R.layout.view_empty, this, true);
    setVisibility(GONE);

    emptyText = (TextView) findViewById(R.id.empty_text);
  }

  public void setEmptyText(CharSequence text) {
    emptyText.setText(text);
  }

  public void setEmptyText(int stringRes) {
    emptyText.setText(stringRes);
  }


}
