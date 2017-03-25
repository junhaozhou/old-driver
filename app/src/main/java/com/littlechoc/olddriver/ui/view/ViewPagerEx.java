package com.littlechoc.olddriver.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Junhao Zhou 2017/3/25
 */

public class ViewPagerEx extends ViewPager {

  public ViewPagerEx(Context context) {
    super(context);
  }

  public ViewPagerEx(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  private boolean canScroll = true;

  public void setCanScroll(boolean canScroll) {
    this.canScroll = canScroll;
  }

  public boolean isCanScroll() {
    return canScroll;
  }

  public void toggleScroll() {
    canScroll = !canScroll;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return canScroll && super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return canScroll && super.onTouchEvent(ev);
  }
}
