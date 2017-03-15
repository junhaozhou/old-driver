package com.littlechoc.olddriver.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.littlechoc.olddriver.ui.base.BasePagerFragment;

import java.util.List;

/**
 * @author Junhao Zhou 2017/3/15
 */

public class DisplayFragmentAdapter extends FragmentPagerAdapter {

  private List<BasePagerFragment> fragments;

  public DisplayFragmentAdapter(FragmentManager fm, List<BasePagerFragment> fragments) {
    super(fm);
    this.fragments = fragments;
  }

  @Override
  public BasePagerFragment getItem(int position) {
    return fragments.get(position);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return fragments.get(position).getTitle();
  }
}
