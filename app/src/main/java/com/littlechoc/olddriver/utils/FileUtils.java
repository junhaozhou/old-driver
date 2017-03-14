package com.littlechoc.olddriver.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Junhao Zhou 2017/3/12
 */

public class FileUtils {

  private static final String SD_CARD = "/storage/emulated/0";

  private static final String BASE_FOLDER = SD_CARD + File.separator + ".0_old_driver";

  public static boolean createFolder(String folder) {
    if (TextUtils.isEmpty(folder)) {
      return false;
    }
    String path = getAbsoluteFolder(folder);
    File file = new File(path);
    if (file.exists()) {
      return file.isDirectory();
    } else {
      return file.mkdirs();
    }
  }

  public static File createFile(String folder, String fileName, String suffix) {
    return createFile(folder, fileName + "." + suffix);
  }

  public static File createFile(String folder, String fileName) {
    if (createFolder(folder)) {
      File file = new File(getAbsoluteFolder(folder), fileName);
      if (!file.exists()) {
        try {
          if (file.createNewFile()) {
            return file;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        return file;
      }
    }
    return null;
  }

  private static String getAbsoluteFolder(String relativeFolder) {
    return BASE_FOLDER + File.separator + relativeFolder;
  }

}
