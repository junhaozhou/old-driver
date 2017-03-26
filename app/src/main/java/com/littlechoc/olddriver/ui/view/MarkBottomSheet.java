package com.littlechoc.olddriver.ui.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.ViewGroup;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.ui.base.BaseAdapter;

/**
 * @author Junhao Zhou 2017/3/26
 */

public class MarkBottomSheet extends BottomSheetDialogFragment {

  public static final String TAG = "MarkBottomSheet";

  private ViewGroup rootView;

  private BaseAdapter.OnItemClickListener onItemClickListener;

  private int selectedType = Constants.MARK_UNKNOWN;

  private BaseAdapter.OnItemClickListener onItemClickListenerWrapper = new BaseAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(int position) {
      selectedType = position;
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(position);
      }
      dismiss();
    }
  };

  public static MarkBottomSheet newInstance() {

    Bundle args = new Bundle();
    MarkBottomSheet fragment = new MarkBottomSheet();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    BottomSheetDialog dialog = new BottomSheetDialog(getContext(), getTheme());
    dialog.setContentView(R.layout.bottom_sheet_mark);
    rootView = (ViewGroup) dialog.findViewById(R.id.bottom_sheet);
    return dialog;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    int size = Constants.MARK_LIST.length;
    for (int i = 0; i < size / 2; i++) {
      MarkerLineView view = new MarkerLineView(getContext());
      view.setOnItemClickListener(onItemClickListenerWrapper);
      if (2 * i + 1 > Constants.MARK_LIST.length - 1) {
        view.setMarker(Constants.MARK_LIST[2 * i], "", i);
      } else {
        view.setMarker(Constants.MARK_LIST[2 * i], Constants.MARK_LIST[2 * i + 1], i);
      }
      rootView.addView(view);
    }
  }

  public void setOnItemClickListener(BaseAdapter.OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    if (selectedType == Constants.MARK_UNKNOWN && onItemClickListener != null) {
      onItemClickListener.onItemClick(Constants.MARK_NONE);
    }
  }
}
