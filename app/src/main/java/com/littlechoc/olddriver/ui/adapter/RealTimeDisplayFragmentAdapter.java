package com.littlechoc.olddriver.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.littlechoc.olddriver.ui.fragment.RealTimeDisplayFragment;

import java.util.List;

/**
 * @author Junhao Zhou 2017/4/28
 */

public class RealTimeDisplayFragmentAdapter extends FragmentPagerAdapter {

  private List<RealTimeDisplayFragment> fragments;

  public RealTimeDisplayFragmentAdapter(FragmentManager fm, List<RealTimeDisplayFragment> fragments) {
    super(fm);
    this.fragments = fragments;
  }

  @Override
  public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override
  public int getCount() {
    return fragments == null ? 0 : fragments.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return fragments.get(position).getTitle();
  }
}
