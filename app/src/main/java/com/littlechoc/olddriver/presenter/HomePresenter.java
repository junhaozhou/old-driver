package com.littlechoc.olddriver.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.HomeContract;
import com.littlechoc.olddriver.dao.MarkDao;
import com.littlechoc.olddriver.model.MarkModel;
import com.littlechoc.olddriver.ui.DisplayActivity;
import com.littlechoc.olddriver.ui.fragment.RealTimeDisplayFragment;
import com.littlechoc.olddriver.ui.fragment.RealTimeObdDisplayFragment;
import com.littlechoc.olddriver.ui.fragment.RealTimeSensorDisplayFragment;
import com.littlechoc.olddriver.utils.DateUtils;
import com.littlechoc.olddriver.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class HomePresenter implements HomeContract.Presenter {

  public static final String TAG = "HomePresenter";

  private MarkDao markDao;

  private HomeContract.View trackView;

  private String folder;

  private long startTime = 0;

  private long endTime = 0;

  private PowerManager.WakeLock wakeLock;

  private boolean isRecording;

  public HomePresenter(HomeContract.View trackView) {
    assert trackView != null;
    this.trackView = trackView;
    trackView.setPresenter(this);

    markDao = new MarkDao();

    PowerManager powerManager = (PowerManager) trackView.getContext().getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wake tag");
  }

  public void start() {

    if (fragmentList != null) {
      startTime = System.currentTimeMillis();
      isRecording = true;

      folder = DateUtils.time2Date(DateUtils.PATTERN_DEFAULT, startTime);
      if (!FileUtils.createFolder(folder)) {
        return;
      }
      for (RealTimeDisplayFragment fragment : fragmentList) {
        RecordPresenter presenter = fragment.getPresenter();
        if (presenter != null) {
          presenter.prepare(folder);
          presenter.start();
        }
      }

      markDao.prepare(folder);

//      if (wakeLock != null) {
//        wakeLock.acquire();
//      }
    }
  }

  public void stop() {
    if (fragmentList != null) {
      endTime = System.currentTimeMillis();
      isRecording = false;

      for (RealTimeDisplayFragment realTimeDisplayFragment : fragmentList) {
        RecordPresenter presenter = realTimeDisplayFragment.getPresenter();
        if (presenter != null) {
          presenter.stop();
        }
      }

      if (!TextUtils.isEmpty(folder)) {
        trackView.showMarkerBottomSheet(true);
      }

//      if (wakeLock != null) {
//        wakeLock.release();
//      }
    }

  }

  public void openDisplayActivity() {
    Intent intent = new Intent(trackView.getContext(), DisplayActivity.class);
    intent.putExtra(Constants.KEY_FOLDER_NAME, folder);
    trackView.getContext().startActivity(intent);
  }

  private long markTime = 0L;

  @Override
  public void beginMark() {
    markTime = System.currentTimeMillis();
    trackView.showMarkerBottomSheet(false);
  }

  @Override
  public void saveMarker(int type, boolean last) {
    MarkModel markModel = new MarkModel();
    if (last) {
      markModel.begin = startTime;
      markModel.end = endTime;
    } else {
      markModel.begin = markTime;
      markModel.end = System.currentTimeMillis();
    }
    markModel.type = type;
    markDao.saveMark(markModel);
    if (last) {
      markDao.stop();
      trackView.showAnalyseSnack();
    }
  }

  @Override
  public boolean isRecording() {
    return isRecording;
  }

  private List<RealTimeDisplayFragment> fragmentList = new ArrayList<>();

  @Override
  public List<RealTimeDisplayFragment> getRealDisplayFragment() {
    fragmentList.clear();
    fragmentList.add(RealTimeObdDisplayFragment.newInstance());
    fragmentList.add(RealTimeSensorDisplayFragment.newInstance());
    return fragmentList;
  }

  @Override
  public void onDestroy() {
  }
}