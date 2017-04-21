package com.littlechoc.olddriver.dao;

import android.hardware.Sensor;

import com.google.gson.JsonSyntaxException;
import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.Constants;
import com.littlechoc.olddriver.model.MarkModel;
import com.littlechoc.olddriver.model.sensor.AccelerometerModel;
import com.littlechoc.olddriver.model.sensor.DataSet;
import com.littlechoc.olddriver.model.sensor.GyroscopeModel;
import com.littlechoc.olddriver.model.sensor.MagneticModel;
import com.littlechoc.olddriver.model.sensor.SensorModel;
import com.littlechoc.olddriver.model.sensor.SensorWrapper;
import com.littlechoc.olddriver.utils.FileUtils;
import com.littlechoc.olddriver.utils.JsonUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Junhao Zhou 2017/3/7
 */

public class SensorDao {

  private static final String TAG = "SensorDao";

  private static final int STATE_IDLE = 1;

  private static final int STATE_PREPARED = 2;

  private static final int STATE_RECORDING = 3;

  private static final int BUFFER_SIZE = 8192;

  private String folderName;

  private File acceleFile;

  private BufferedOutputStream acceleBos;

  private File magneticFile;

  private BufferedOutputStream magneticBos;

  private File gyroscopeFile;

  private BufferedOutputStream gyroscopeBos;

  private boolean isFilesCreate = false;

  private int state = STATE_IDLE;

  public SensorDao() {
  }

  private void createFiles() {
    Logger.d(TAG, "#createFiles");
    if (isFilesCreate) {
      return;
    }
    if (!FileUtils.createFolder(folderName)) {
      throw new IllegalStateException(folderName + " create failure");
    } else {
      Logger.d(TAG, "CREATE FOLDER [%s]", folderName);
    }
    if ((acceleFile = FileUtils.createFile(folderName, Constants.FILE_ACCELEROMETER)) == null) {
      throw new IllegalStateException(Constants.FILE_ACCELEROMETER + " create failure");
    } else {
      Logger.d(TAG, "CREATE FILE [%s]", acceleFile.getName());
    }
    if ((magneticFile = FileUtils.createFile(folderName, Constants.FILE_MAGNETIC)) == null) {
      throw new IllegalStateException(Constants.FILE_MAGNETIC + " create failure");
    } else {
      Logger.d(TAG, "CREATE FILE [%s]", magneticFile.getName());
    }
    if ((gyroscopeFile = FileUtils.createFile(folderName, Constants.FILE_GYROSCOPE)) == null) {
      throw new IllegalStateException(Constants.FILE_GYROSCOPE + " create failure");
    } else {
      Logger.d(TAG, "CREATE FILE [%s]", gyroscopeFile.getName());
    }
    try {
      acceleBos = new BufferedOutputStream(new FileOutputStream(acceleFile), BUFFER_SIZE);
      magneticBos = new BufferedOutputStream(new FileOutputStream(magneticFile), BUFFER_SIZE);
      gyroscopeBos = new BufferedOutputStream(new FileOutputStream(gyroscopeFile), BUFFER_SIZE);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    }
    isFilesCreate = true;
  }

  public void prepare(String folderName) {
    Logger.d(TAG, "#prepare folder = %s", folderName);
    this.folderName = folderName;
    if (state != STATE_IDLE) {
      Logger.e(TAG, "prepare failure: state(%d) illegal", state);
      return;
    }
    createFiles();
    initProcessor();
    state = STATE_PREPARED;
  }

  public void stop() {
    Logger.d(TAG, "#stop");
    processor.onComplete();
    isFilesCreate = false;
  }

  public void saveSensorInfo(Sensor sensor, Constants.SensorType sensorType) {
    Logger.d(TAG, "saveSensorInfo[%s, %s]", sensor, sensorType);
    if (state != STATE_PREPARED) {
      Logger.e(TAG, "saveSensorInfo failure: not prepared");
      return;
    }
    switch (sensorType) {
      case ACCELEROMETER:
        saveSensorInfoInternal(acceleBos, sensor);
        break;
      case MAGNETIC:
        saveSensorInfoInternal(magneticBos, sensor);
        break;
      case GYROSCOPE:
        saveSensorInfoInternal(gyroscopeBos, sensor);
        break;
    }
  }

  private void saveSensorInfoInternal(BufferedOutputStream bos, Sensor sensor) {
    String info = (new SensorWrapper(sensor)).toString() + "\n";
    try {
      bos.write(info.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void parseSensorInfo() {
  }

  public String getFolder() {
    return folderName;
  }

  public void saveAccelerometerData(AccelerometerModel accelerometerModel) {
    save(accelerometerModel);
  }

  public void saveMagnetic(MagneticModel magneticModel) {
    save(magneticModel);
  }

  public void saveGyroscope(GyroscopeModel gyroscopeModel) {
    save(gyroscopeModel);
  }

  private void save(SensorModel model) {
    if (state != STATE_PREPARED) {
      Logger.e(TAG, "saveSensorInfo failure: not prepared");
      return;
    }
    String data = model.toString();
    model.reuse();
    processor.onNext(new DataWrapper(data, model));
  }

  private PublishProcessor<DataWrapper> processor;

  private Consumer<GroupedFlowable<BufferedOutputStream, DataWrapper>> onNext
          = new Consumer<GroupedFlowable<BufferedOutputStream, DataWrapper>>() {
    @Override
    public void accept(GroupedFlowable<BufferedOutputStream, DataWrapper> stringDataWrapper) throws Exception {
      final BufferedOutputStream bos = stringDataWrapper.getKey();
      stringDataWrapper.subscribe(new Consumer<DataWrapper>() {
        @Override
        public void accept(DataWrapper dataWrapper) throws Exception {
          bos.write((dataWrapper.data + "\n").getBytes());
        }
      });
    }
  };

  private void initProcessor() {
    Logger.d(TAG, "initProcessor");
    processor = PublishProcessor.create();
    processor.subscribeOn(Schedulers.newThread())
            .groupBy(new Function<DataWrapper, BufferedOutputStream>() {
              @Override
              public BufferedOutputStream apply(DataWrapper dataWrapper) throws Exception {
                if (dataWrapper.model instanceof AccelerometerModel) {
                  return acceleBos;
                } else if (dataWrapper.model instanceof MagneticModel) {
                  return magneticBos;
                } else {
                  return gyroscopeBos;
                }
              }
            }).subscribe(onNext, new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
      }
    }, new Action() {
      @Override
      public void run() throws Exception {
        acceleBos.flush();
        magneticBos.flush();
        gyroscopeBos.flush();
        acceleBos.close();
        magneticBos.close();
        gyroscopeBos.close();
        state = STATE_IDLE;
      }
    });
  }

  public static MarkModel getMarkByFolder(File folder) {
    if (folder == null || !folder.exists() || !folder.isDirectory()) {
      return null;
    }
    File markFile = new File(folder.getAbsolutePath(), Constants.FILE_MARK);
    if (!markFile.exists()) {
      return null;
    }
    try {
      BufferedReader br = new BufferedReader(new FileReader(markFile));
      String line = br.readLine();
      return JsonUtils.newInstance().fromJson(line, MarkModel.class);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static class DataWrapper {
    String data;

    SensorModel model;

    public DataWrapper(String data, SensorModel model) {
      this.data = data;
      this.model = model;
    }
  }

  public static DataSet getInfo(String folder, Constants.SensorType sensorType) {
    String filePath = contactFilePath(folder, sensorType);
    File file = new File(filePath);
    LineNumberReader lnr;
    if (file.exists()) {
      try {
        lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file)));
        long length = file.length();

        String line = lnr.readLine();
        SensorWrapper sensor = SensorWrapper.parseFromJson(line);

        lnr.skip(length);
        int lines = lnr.getLineNumber();
        return new DataSet(sensor, lines);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  public static List<SensorModel> loadSensorData(String folder, Constants.SensorType sensorType) {
    List<SensorModel> data = new ArrayList<>();
    String filePath = contactFilePath(folder, sensorType);
    File file = new File(filePath);
    BufferedReader br = null;
    LineNumberReader lnr = null;
    if (file.exists()) {
      try {
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        String line = br.readLine();
        while ((line = br.readLine()) != null) {
          String[] ss = line.split(",");
          float x = Float.valueOf(ss[0]);
          float y = Float.valueOf(ss[1]);
          float z = Float.valueOf(ss[2]);
          long timestamp = Long.valueOf(ss[3]);
          SensorModel sensorModel = SensorModel.newInstance(sensorType, x, y, z, timestamp);
          data.add(sensorModel);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        FileUtils.safeCloseStream(br);
      }
    }
    return data;
  }

  private static String contactFilePath(String folder, Constants.SensorType type) {
    StringBuilder path = new StringBuilder();
    path.append(FileUtils.getAbsoluteFolder(folder)).append(File.separator);
    switch (type) {
      case ACCELEROMETER:
        path.append(Constants.FILE_ACCELEROMETER);
        break;
      case GYROSCOPE:
        path.append(Constants.FILE_GYROSCOPE);
        break;
      case MAGNETIC:
        path.append(Constants.FILE_MAGNETIC);
        break;
      default:
        return "";
    }
    return path.toString();
  }

}
