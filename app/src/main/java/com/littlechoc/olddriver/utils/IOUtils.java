package com.littlechoc.olddriver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * @author Junhao Zhou 2017/3/25
 */

public class IOUtils {

  public static void safeCloseStream(Reader reader) {
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void safeCloseStream(OutputStream outputStream) {
    if (outputStream != null) {
      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void safeCloseStream(InputStream inputStream) {
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
