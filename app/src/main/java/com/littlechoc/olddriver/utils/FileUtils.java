package com.littlechoc.olddriver.utils;

import android.text.TextUtils;

import java.io.Closeable;
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

  public static String getAbsoluteFolder(String relativeFolder) {
    return BASE_FOLDER + File.separator + relativeFolder;
  }

  public static File getRootFile() {
    return new File(BASE_FOLDER);
  }

  public static boolean delete(File file) {
    boolean res = true;
    if (file != null && file.exists()) {
      if (file.isDirectory()) {
        File[] children = file.listFiles();
        for (File child : children) {
          delete(child);
        }
      }
      res = file.delete();
    }
    return res;
  }

  public static long getSize(File file) {
    if (file == null || !file.exists()) {
      return 0;
    }
    long size = 0;
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      for (File child : children) {
        size += getSize(child);
      }
    } else {
      size = file.length();
    }
    return size;
  }

  public static void safeCloseStream(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
