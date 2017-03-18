package com.littlechoc.olddriver.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.littlechoc.olddriver.R;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class RecyclerViewEx extends RecyclerView {

  private View emptyView;

  private AdapterDataObserver emptyObserver = new AdapterDataObserver() {

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      super.onItemRangeChanged(positionStart, itemCount);
      toggleEmptyView();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
      super.onItemRangeChanged(positionStart, itemCount, payload);
      toggleEmptyView();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      super.onItemRangeInserted(positionStart, itemCount);
      toggleEmptyView();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      super.onItemRangeRemoved(positionStart, itemCount);
      toggleEmptyView();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      super.onItemRangeMoved(fromPosition, toPosition, itemCount);
      toggleEmptyView();
    }

    @Override
    public void onChanged() {
      toggleEmptyView();
    }

    private void toggleEmptyView() {
      Adapter<?> adapter = getAdapter();
      if (adapter != null && emptyView != null) {
        if (adapter.getItemCount() == 0) {
          emptyView.setVisibility(View.VISIBLE);
          RecyclerViewEx.this.setVisibility(View.GONE);
        } else {
          emptyView.setVisibility(View.GONE);
          RecyclerViewEx.this.setVisibility(View.VISIBLE);
        }
      }
    }
  };

  public RecyclerViewEx(Context context) {
    super(context);
  }

  public RecyclerViewEx(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerViewEx(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    ViewGroup parent = (ViewGroup) getParent();
    if (parent != null) {
      emptyView = parent.findViewById(R.id.empty_view);
      if (emptyView == null) {
        throw new IllegalStateException("please check you have set a empty view");
      }
    }
    emptyObserver.onChanged();
  }

  @Override
  public void setAdapter(Adapter adapter) {
    super.setAdapter(adapter);

    if (adapter != null) {
      adapter.registerAdapterDataObserver(emptyObserver);
    }

    emptyObserver.onChanged();
  }

}
