package com.littlechoc.olddriver.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.obd.reader.activity.ConfigActivity;
import com.littlechoc.olddriver.obd.reader.activity.MainActivity;
import com.littlechoc.olddriver.ui.BluetoothActivity;
import com.littlechoc.olddriver.ui.HistoryActivity;

/**
 * @author Junhao Zhou 2017/3/19
 */

public class CustomNavigationView extends NavigationView implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

  private static final int ACTION_NONE = 0;

  private static final int ACTION_HISTORY = 1;

  private static final int ACTION_BLUETOOTH = 2;

  private static final int ACTION_LIB_CONFIG = 9;

  private static final int ACTION_LIB_MAIN = 10;

  private View headerView;

  private DrawerLayout drawerLayout;

  private int action = ACTION_NONE;

  public CustomNavigationView(Context context) {
    this(context, null);
  }

  public CustomNavigationView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init();
  }

  private void init() {
    setNavigationItemSelectedListener(this);
    headerView = getHeaderView(0);
    drawerLayout = (DrawerLayout) getParent();
  }

  public void setDrawerLayout(DrawerLayout drawerLayout) {
    this.drawerLayout = drawerLayout;
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_history:
        closeDrawer();
        action = ACTION_HISTORY;
        return true;
      case R.id.menu_bluetooth:
        closeDrawer();
        action = ACTION_BLUETOOTH;
        return true;
      case R.id.lib_config:
        closeDrawer();
        action = ACTION_LIB_CONFIG;
        return true;
      case R.id.lib_main:
        closeDrawer();
        action = ACTION_LIB_MAIN;
        return true;

    }
    return false;

  }

  private void closeDrawer() {
    if (drawerLayout != null) {
      drawerLayout.closeDrawer(Gravity.START);
      drawerLayout.removeDrawerListener(this);
      drawerLayout.addDrawerListener(this);
    }
  }

  @Override
  public void onDrawerSlide(View drawerView, float slideOffset) {

  }

  @Override
  public void onDrawerOpened(View drawerView) {
  }

  @Override
  public void onDrawerClosed(View drawerView) {
    if (ACTION_BLUETOOTH == action) {
      getContext().startActivity(new Intent(getContext(), BluetoothActivity.class));
    } else if (ACTION_HISTORY == action) {
      getContext().startActivity(new Intent(getContext(), HistoryActivity.class));
    } else if (ACTION_LIB_CONFIG == action) {
      getContext().startActivity(new Intent(getContext(), ConfigActivity.class));
    } else if (ACTION_LIB_MAIN == action) {
      getContext().startActivity(new Intent(getContext(), MainActivity.class));
    }
    action = ACTION_NONE;
  }

  @Override
  public void onDrawerStateChanged(int newState) {
  }
}
