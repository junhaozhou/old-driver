package com.littlechoc.olddriver.dao;

import com.google.gson.Gson;
import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.model.MarkModel;
import com.littlechoc.olddriver.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * @author Junhao Zhou 2017/4/21
 */

public class MarkDao {

  public static final String TAG = "MarkDao";

  private PublishSubject<MarkModel> subject;

  private BufferedOutputStream markFos;

  private String folderName;

  public void prepare(String folderName) {
    this.folderName = folderName;
    createFile();
    initProcess();
  }

  private void createFile() {
    File markFile;
    if ((markFile = FileUtils.createFile(folderName, Constants.FILE_MARK)) == null) {
      throw new IllegalArgumentException(Constants.FILE_MARK + " create failure");
    } else {
      Logger.d(TAG, "CREATE FILE [%s]", markFile.getName());
    }
    try {
      markFos = new BufferedOutputStream(new FileOutputStream(markFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void initProcess() {
    subject = PublishSubject.create();
    subject.subscribeOn(Schedulers.newThread())
            .map(new Function<MarkModel, String>() {
              @Override
              public String apply(MarkModel markModel) throws Exception {
                return new Gson().toJson(markModel);
              }
            }).subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        markFos.write((s + "\n").getBytes());
      }
    }, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        Logger.e(TAG, "Save mark error: %s", throwable.getMessage());
      }
    }, new Action() {
      @Override
      public void run() throws Exception {
        markFos.flush();
        FileUtils.safeCloseStream(markFos);
      }
    });
  }

  public void saveMark(MarkModel markModel) {
    subject.onNext(markModel);
  }

  public void stop() {
    subject.onComplete();
  }
}
