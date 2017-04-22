package com.littlechoc.olddriver.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.TrackContract;
import com.littlechoc.olddriver.model.PatternCategory;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.presenter.TrackPresenter;
import com.littlechoc.olddriver.ui.adapter.ObdDataAdapter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.base.BaseAdapter;
import com.littlechoc.olddriver.ui.view.CustomNavigationView;
import com.littlechoc.olddriver.ui.view.MarkBottomSheet;
import com.littlechoc.olddriver.utils.PermissionUtils;
import com.littlechoc.olddriver.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class HomeActivity extends BaseActivity implements TrackContract.View {

  public static final String TAG = "HomeActivity";

  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;

  @BindView(R.id.navigation_view)
  CustomNavigationView navigationView;

  @BindView(R.id.root)
  View rootView;

  @BindView(R.id.title_bar)
  Toolbar titleBar;

  @BindView(R.id.track_switch)
  FloatingActionButton trackSwitch;

  @BindView(R.id.obd_data_list)
  RecyclerView obdDataList;

  private ObdDataAdapter obdDataAdapter;

  private ActionBarDrawerToggle drawerToggle;

  private boolean isTracking = false;

  private TrackContract.Presenter trackPresenter;

  private List<ObdModel> obdModelList;

  @Override
  public int getRootView() {
    return R.layout.activity_home;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    new TrackPresenter(this);

    obdModelList = new ArrayList<>();
    trackPresenter.attachObdModelList(obdModelList);
    initView();
  }


  private void initView() {
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        drawerLayout.openDrawer(Gravity.START);
      }
    });

    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
            R.string.open_drawer_content_desc, R.string.close_drawer_content_desc);
    drawerLayout.addDrawerListener(drawerToggle);

    navigationView.setDrawerLayout(drawerLayout);

    trackSwitch.setImageResource(R.drawable.ic_start_track);

    obdDataAdapter = new ObdDataAdapter(obdModelList);
    obdDataList.setLayoutManager(new LinearLayoutManager(getContext()));
    obdDataList.setAdapter(obdDataAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.mark);
    if (item != null) {
      item.setVisible(trackPresenter.isTracking());
      return true;
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.enable_sensor_log:
        trackPresenter.setIfLogSensor(true);
        break;
      case R.id.disable_sensor_log:
        trackPresenter.setIfLogSensor(false);
        break;
      case R.id.mark:
        trackPresenter.beginMark();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    trackPresenter.onDestroy();
  }

  private static final long THRESHOLD = 1000 * 2;

  private long lastPressTime = 0L;

  @Override
  public void onBackPressed() {
    if (System.currentTimeMillis() - lastPressTime > THRESHOLD) {
      ToastUtils.show("Press Again to Quit");
      lastPressTime = System.currentTimeMillis();
    } else {
      super.onBackPressed();
    }
  }

  @OnClick(R.id.track_switch)
  public void onTrackSwitchClick() {
    PermissionUtils.requestPermission(this, PermissionUtils.STORAGE_PERMISSION,
            new PermissionUtils.OnPermissionGranted() {
              @Override
              public void onPermissionGranted() {
                if (isTracking) {
                  trackPresenter.stopTrack();
                  toggleTrackState(true);
                  isTracking = false;
                  supportInvalidateOptionsMenu();
                } else {
                  trackPresenter.selectBluetoothDevice();
                }
              }
            });
  }

  private void toggleTrackState(final boolean start) {
    trackSwitch.hide(new FloatingActionButton.OnVisibilityChangedListener() {
      @Override
      public void onHidden(FloatingActionButton fab) {
        trackSwitch.setImageResource(start ? R.drawable.ic_start_track : R.drawable.ic_stop_track);
        trackSwitch.show();
      }
    });
  }

  public void onAnalyseClick() {
    trackPresenter.openDisplayActivity();
  }

  @Override
  public void setPresenter(TrackContract.Presenter presenter) {
    trackPresenter = presenter;
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public void showAnalyseSnack() {
    Snackbar.make(rootView, R.string.track_success, 3000).setAction(R.string.show, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onAnalyseClick();
      }
    }).show();
  }

  @Override
  public void showMarkerBottomSheet(final boolean isLast) {
    MarkBottomSheet bottomSheet = MarkBottomSheet.newInstance(PatternCategory.COMMON);
    bottomSheet.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(int position) {
        Logger.d(TAG, "on mark click: " + position);
        trackPresenter.saveMarker(position, isLast);
      }
    });
    bottomSheet.show(getSupportFragmentManager(), "MarkBottomSheet");
  }

  private List<BluetoothDevice> deviceList;

  @Override
  public void showBluetoothDevice(List<BluetoothDevice> devices) {
    this.deviceList = devices;
    String[] devicesArray = new String[deviceList.size()];
    for (int i = 0; i < deviceList.size(); i++) {
      devicesArray[i] = deviceList.get(i).getName();
    }
    AlertDialog.Builder builder
            = new AlertDialog.Builder(getContext())
            .setTitle("选择蓝牙设备")
            .setItems(devicesArray, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                trackPresenter.connectBluetooth(deviceList.get(which));
                toggleTrackState(false);
                isTracking = true;
                trackPresenter.startTrack();
                supportInvalidateOptionsMenu();
              }
            });
    builder.show();
  }

  @Override
  public void updateObdData() {
    obdDataAdapter.notifyDataSetChanged();
  }
}
