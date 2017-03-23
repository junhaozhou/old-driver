/*
 * TODO put header 
 */
package com.littlechoc.olddriver.obd.reader;

import com.littlechoc.olddriver.obd.reader.io.ObdCommandJob;

/**
 * TODO put description
 */
public interface IPostMonitor {
  void setListener(IPostListener callback);

  boolean isRunning();

  void executeQueue();

  void addJobToQueue(ObdCommandJob job);
}