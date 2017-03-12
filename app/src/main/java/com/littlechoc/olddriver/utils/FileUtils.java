package com.littlechoc.olddriver.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class FileUtils {

  private static final String SD_CARD =
          Environment.getExternalStorageDirectory().getAbsolutePath();

  private static final String BASE_FOLDER = SD_CARD + File.pathSeparator + "old_driver";

  public static boolean createFolder(String folder) {
    if (TextUtils.isEmpty(folder)) {
      return false;
    }
    String path = BASE_FOLDER + File.separator + folder;
    File file = new File(path);
    if (file.exists()) {
      return file.isDirectory();
    } else {
      return file.mkdirs();
    }
  }

  public static boolean createFile(String folder, String file) {
    return false;
  }

}
