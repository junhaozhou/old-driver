package com.littlechoc.olddriver.dao;

import android.hardware.Sensor;

import com.littlechoc.olddriver.model.sensor.AccelerometerModel;
import com.littlechoc.olddriver.model.sensor.GyroscopeModel;
import com.littlechoc.olddriver.model.sensor.MagneticModel;
import com.littlechoc.olddriver.model.sensor.SensorModel;
import com.littlechoc.olddriver.utils.DateUtils;
import com.littlechoc.olddriver.utils.FileUtils;
import com.littlechoc.olddriver.utils.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.disposables.Disposable;
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

  public static final String SUFFIX = "dat";

  public static final String FILE_ACCELEROMETER = "accelerometer";

  public static final String FILE_GYROSCOPE = "gyroscope";

  public static final String FILE_MAGNETIC = "magnetic";

  private static final int BUFFER_SIZE = 8192;

  private String folder;

  private File acceleFile;

  private BufferedOutputStream acceleBos;

  private File magneticFile;

  private BufferedOutputStream magneticBos;

  private File gyroscopeFile;

  private BufferedOutputStream gyroscopeBos;

  private boolean isFilesCreate = false;

  public SensorDao() {
  }

  private void createFiles() {
    if (isFilesCreate) {
      return;
    }
    folder = DateUtils.time2Date(DateUtils.PATTERN_DEFAULT, System.currentTimeMillis());
    if (!FileUtils.createFolder(folder)) {
      throw new IllegalStateException(folder + " create failure");
    } else {
      Logger.d(TAG, "CREATE FOLDER [%s]", folder);
    }
    if ((acceleFile = FileUtils.createFile(folder, FILE_ACCELEROMETER, SUFFIX)) == null) {
      throw new IllegalStateException(FILE_ACCELEROMETER + " create failure");
    } else {
      Logger.d(TAG, "CREATE FILE [%s]", acceleFile.getName());
    }
    if ((magneticFile = FileUtils.createFile(folder, FILE_GYROSCOPE, SUFFIX)) == null) {
      throw new IllegalStateException(FILE_ACCELEROMETER + " create failure");
    } else {
      Logger.d(TAG, "CREATE FILE [%s]", magneticFile.getName());
    }
    if ((gyroscopeFile = FileUtils.createFile(folder, FILE_MAGNETIC, SUFFIX)) == null) {
      throw new IllegalStateException(FILE_ACCELEROMETER + " create failure");
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

  public void saveSensorInfo(Sensor sensor, SensorModel.Type type) {
    createFiles();
  }

  public void parseSensorInfo() {
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

  private PublishProcessor<DataWrapper> processor = PublishProcessor.create();

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

  private Disposable disposable
          = processor.subscribeOn(Schedulers.newThread())
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
            }
          });

  private void save(SensorModel model) {
    createFiles();
    String data = model.toString();
    model.reuse();
    processor.onNext(new DataWrapper(data, model));
  }

  public void stop() {
    processor.onComplete();
    isFilesCreate = false;
  }

  public String getFolder() {
    return folder;
  }

  private static class DataWrapper {
    String data;

    SensorModel model;

    public DataWrapper(String data, SensorModel model) {
      this.data = data;
      this.model = model;
    }
  }

}
