package com.littlechoc.olddriver.ui.fragment;

import android.os.Bundle;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.ui.base.BasePagerFragment;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class SummaryFragment extends BasePagerFragment {

  public static SummaryFragment newInstance(String folder) {

    Bundle args = new Bundle();
    args.putString(Constants.KEY_FOLDER_NAME, folder);
    SummaryFragment fragment = new SummaryFragment();
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public int getRootView() {
    return R.layout.fragment_summary;
  }

  @Override
  public String getTitle() {
    return "Summary";
  }
}
