package com.littlechoc.olddriver.ui.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.ViewGroup;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.dao.PatternDao;
import com.littlechoc.olddriver.model.Pattern;
import com.littlechoc.olddriver.ui.base.BaseAdapter;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/26
 */

public class MarkBottomSheet extends BottomSheetDialogFragment {

  public static final String TAG = "MarkBottomSheet";

  private static final String KEY_PATTERN_CATEGORY = "key_pattern_category";

  private ViewGroup rootView;

  private BaseAdapter.OnItemClickListener onItemClickListener;

  private int selectedType = Pattern.UNKNOWN.getId();

  private List<Pattern> patternList;

  private BaseAdapter.OnItemClickListener onItemClickListenerWrapper = new BaseAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(int position) {
      int id = patternList.get(position).getId();
      selectedType = id;
      if (onItemClickListener != null) {
        onItemClickListener.onItemClick(id);
      }
      dismiss();
    }
  };

  public static MarkBottomSheet newInstance(int patternCategory) {

    Bundle args = new Bundle();
    args.putInt(KEY_PATTERN_CATEGORY, patternCategory);
    MarkBottomSheet fragment = new MarkBottomSheet();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int patternCategory = getArguments().getInt(KEY_PATTERN_CATEGORY);
    patternList = PatternDao.getInstance().getPatternListByCategory(patternCategory);
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

    int size = patternList.size();
    for (int i = 0; i < size / 2; i++) {
      MarkerLineView view = new MarkerLineView(getContext());
      view.setOnItemClickListener(onItemClickListenerWrapper);
      if (2 * i + 1 > patternList.size() - 1) {
        view.setMarker(patternList.get(2 * i).getName(), "", i);
      } else {
        view.setMarker(patternList.get(2 * i).getName(), patternList.get(2 * i + 1).getName(), i);
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
    if (selectedType == Pattern.UNKNOWN.getId() && onItemClickListener != null) {
      int id = patternList.size() > 0 ? patternList.get(patternList.size() - 1).getId() : Pattern.UNKNOWN.getId();
      onItemClickListener.onItemClick(id);
    }
  }
}
