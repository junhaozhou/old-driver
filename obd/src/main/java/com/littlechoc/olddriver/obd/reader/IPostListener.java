/*
 * TODO put header 
 */
package com.littlechoc.olddriver.obd.reader;

import com.littlechoc.olddriver.obd.reader.io.ObdCommandJob;

/**
 * TODO put description
 */
public interface IPostListener {

  void stateUpdate(ObdCommandJob job);

}