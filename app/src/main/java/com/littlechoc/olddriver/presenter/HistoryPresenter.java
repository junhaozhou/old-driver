package com.littlechoc.olddriver.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.contract.HistoryContract;
import com.littlechoc.olddriver.dao.PatternDao;
import com.littlechoc.olddriver.dao.SensorDao;
import com.littlechoc.olddriver.model.MarkModel;
import com.littlechoc.olddriver.model.Pattern;
import com.littlechoc.olddriver.model.RecordModel;
import com.littlechoc.olddriver.ui.DisplayActivity;
import com.littlechoc.olddriver.utils.FileUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class HistoryPresenter implements HistoryContract.Presenter {

  private HistoryContract.View historyView;

  private List<RecordModel> records;

  public HistoryPresenter(HistoryContract.View historyView) {
    this.historyView = historyView;
    historyView.setPresenter(this);
  }

  @Override
  public void bindData(List<RecordModel> dataSet) {
    records = dataSet;
  }

  @Override
  public void loadData() {
    File root = FileUtils.getRootFile();
    records.clear();
    if (root.exists() && root.isDirectory()) {
      File[] children = root.listFiles();
      for (File child : children) {
        RecordModel recordModel = new RecordModel();
        recordModel.setDate(child.getName());
        recordModel.setSize(FileUtils.getSize(child));
        MarkModel markModel = SensorDao.getMarkByFolder(child);
        recordModel.setPatternId(markModel == null ?
                Pattern.UNKNOWN.getId() : markModel.type);
        recordModel.setName(PatternDao.getInstance().getPatternById(recordModel.getPatternId()).getName());
        records.add(recordModel);
      }
    }
    Collections.sort(records, new RecordModel.Comparator());
    historyView.updateList();
  }

  @Override
  public void clear() {
    File root = FileUtils.getRootFile();
    FileUtils.delete(root);
    records.clear();
    historyView.updateList();
  }

  @Override
  public void openDisplayActivity(String folder) {
    if (TextUtils.isEmpty(folder)) {
      return;
    }
    Intent intent = new Intent(historyView.getContext(), DisplayActivity.class);
    intent.putExtra(Constants.KEY_FOLDER_NAME, folder);
    historyView.getContext().startActivity(intent);
  }

  @Override
  public void onDestroy() {

  }
}
