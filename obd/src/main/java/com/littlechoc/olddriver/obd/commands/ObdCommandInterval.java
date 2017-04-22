package com.littlechoc.olddriver.obd.commands;

/**
 * @author Junhao Zhou 2017/4/22
 */

public class ObdCommandInterval extends ObdCommandProxy {

  private static final long DEFAULT_INTERVAL = 1000;

  private long lastTime;

  private long interval;

  public ObdCommandInterval(ObdCommand other) {
    this(other, DEFAULT_INTERVAL);
  }

  public ObdCommandInterval(ObdCommand other, long interval) {
    super(other);
    lastTime = 0L;
    this.interval = interval;
  }

  public boolean canAdd(long currentTime) {
    return currentTime - lastTime > interval;
  }

  public void setLastTime(long lastTime) {
    this.lastTime = lastTime;
  }

}
