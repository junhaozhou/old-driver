package com.littlechoc.olddriver.dao;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.littlechoc.commonutils.Logger;
import com.littlechoc.olddriver.Application;
import com.littlechoc.olddriver.model.sensor.ObdModel;
import com.littlechoc.olddriver.obd.commands.ObdCommandInterval;
import com.littlechoc.olddriver.obd.commands.ObdCommandProxy;
import com.littlechoc.olddriver.obd.commands.RawObdCommand;
import com.littlechoc.olddriver.obd.commands.SpeedObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.EngineRPMObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.EngineRuntimeObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.MassAirFlowObdCommand;
import com.littlechoc.olddriver.obd.commands.pressure.FuelPressureObdCommand;
import com.littlechoc.olddriver.utils.FileUtils;
import com.littlechoc.olddriver.utils.JsonUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * @author Junhao Zhou 2017/4/22
 */

public class ObdDao {

  public interface Callback {

    void onError(String msg);

    void onCommandResult(ObdModel obdModel);
  }

  public static final String TAG = "ObdDao";

  private static final UUID MY_UUID = UUID
          .fromString("00001101-0000-1000-8000-00805F9B34FB");

  private static final long INTERVAL_READ = 50;

  private static final long INTERVAL_GENERATE = 10;

  private final BlockingQueue<ObdCommandProxy> commandList;

  private final List<ObdCommandInterval> toReadList;

  private BluetoothDevice device;

  private BluetoothSocket socket;

  private String folder;

  private BufferedOutputStream bos;

  private InternalCallback internalCallback;

  private WorkThread workThread;

  private GenerateThread generateThread;

  private Handler workHandler;

  private Handler generateHandler;

  private ReadRunnable readRunnable;

  private PublishSubject<ObdModel> subject;

  public ObdDao() {
    commandList = new LinkedBlockingQueue<>();
    toReadList = new ArrayList<>();
  }

  public void start(BluetoothDevice device, String folder, Callback callback) {
    // init
    this.folder = folder;
    this.device = device;
    internalCallback = new InternalCallback(callback);

    generateThread = new GenerateThread();
    generateThread.start();
    generateHandler = new Handler(generateThread.getLooper());

    workThread = new WorkThread();
    workThread.start();
    workHandler = new Handler(workThread.getLooper());
    workHandler.post(new StartRunnable());
  }

  public void stop() {
    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    workHandler.removeCallbacksAndMessages(null);
    readRunnable = null;
    workThread.quitSafely();
    generateHandler.removeCallbacksAndMessages(null);
    generateThread.quitSafely();
    if (subject != null) {
      subject.onComplete();
    }
  }

  private class WorkThread extends HandlerThread {

    public WorkThread() {
      super("");
    }
  }

  private class GenerateThread extends HandlerThread {
    public GenerateThread() {
      super("");
    }
  }

  private class StartRunnable implements Runnable {

    @Override
    public void run() {

      if (!createFile()) {
        return;
      }

      initProcess();

      //
      loadCommands();
      generateHandler.post(new GenerateRunnable());

      // connect
      if (connect()) {
        readRunnable = new ReadRunnable();
        workHandler.post(readRunnable);
      }
    }

    private boolean createFile() {
      File obdFile = FileUtils.createFile(folder, "obd.dat");
      if (obdFile == null) {
        internalCallback.onError("");
        return false;
      } else {
        Logger.i(TAG, "#create obd file success");
      }
      try {
        bos = new BufferedOutputStream(new FileOutputStream(obdFile));
        return true;
      } catch (FileNotFoundException e) {
        Logger.e(TAG, Log.getStackTraceString(e));
        internalCallback.onError(Log.getStackTraceString(e));
        return false;
      }
    }

    private void initProcess() {
      subject = PublishSubject.create();
      subject.subscribeOn(Schedulers.newThread())
              .map(new Function<ObdModel, String>() {
                @Override
                public String apply(ObdModel obdModel) throws Exception {
                  return JsonUtils.newInstance().toJson(obdModel);
                }
              }).subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
          bos.write((s + "\n").getBytes());
        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
          Logger.e(TAG, "Save obd command error: %s", throwable.getMessage());
        }
      }, new Action() {
        @Override
        public void run() throws Exception {
          bos.flush();
          FileUtils.safeCloseStream(bos);
        }
      });
    }

    private void loadCommands() {
      toReadList.clear();

      toReadList.add(new ObdCommandInterval(new SpeedObdCommand(), 300));
      toReadList.add(new ObdCommandInterval(new EngineRPMObdCommand(), 300));
      toReadList.add(new ObdCommandInterval(new MassAirFlowObdCommand(), 1000));
      toReadList.add(new ObdCommandInterval(new FuelPressureObdCommand(), 1000));
      toReadList.add(new ObdCommandInterval(new EngineRuntimeObdCommand(), 1000));
      toReadList.add(new ObdCommandInterval(new RawObdCommand("01 33"), 5000));
    }

    private boolean connect() {
      try {
        socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        socket.connect();
        return true;
      } catch (IOException e) {
        Logger.e(TAG, Log.getStackTraceString(e));
        internalCallback.onError(Log.getStackTraceString(e));
        return false;
      }
    }
  }

  private class ReadRunnable implements Runnable {

    @Override
    public void run() {

      //
      ObdCommandProxy command = getNextCommand();
      if (command != null) {
        try {
          command.run(socket.getInputStream(), socket.getOutputStream());
          long time = System.currentTimeMillis();
          ObdModel obdModel = convert2ObdModel(command, time);
          internalCallback.onCommandResult(obdModel);
          saveResult(obdModel);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      //
      workHandler.postDelayed(readRunnable, INTERVAL_READ);
    }

    private ObdModel convert2ObdModel(ObdCommandProxy command, long time) {
      ObdModel obdModel = new ObdModel();
      obdModel.command = command.getCommand();
      obdModel.data = command.getResult();
      obdModel.name = command.getName();
      obdModel.time = time;
      obdModel.nanoTime = System.nanoTime();
      obdModel.formattedData = command.getFormattedResult();
      return obdModel;
    }

    private ObdCommandProxy getNextCommand() {
      ObdCommandProxy command = null;
      try {
        command = commandList.take();
        Logger.i(TAG, "#getNextCommand[%s]", command.getName());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return command;
    }

    private void saveResult(ObdModel obdModel) {
      subject.onNext(obdModel);
    }
  }

  private class GenerateRunnable implements Runnable {

    @Override
    public void run() {

      long currentTime = System.currentTimeMillis();
      try {
        for (ObdCommandInterval command : toReadList) {
          if (command.canAdd(currentTime)) {
            command.setLastTime(currentTime);

            Logger.d(TAG, "#add new command[%s]", command.getName());
            commandList.put(new ObdCommandProxy(command.getOriginCommand()));
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      generateHandler.postDelayed(this, INTERVAL_GENERATE);
    }
  }

  private class InternalCallback implements Callback {

    private Callback callback;

    public InternalCallback(Callback callback) {
      this.callback = callback;
    }

    @Override
    public void onError(final String msg) {
      if (callback != null) {
        Application.getInstance().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            callback.onError(msg);
          }
        });
      }
    }

    @Override
    public void onCommandResult(final ObdModel obdModel) {
      if (callback != null) {
        Application.getInstance().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            callback.onCommandResult(obdModel);
          }
        });
      }
    }
  }

}
