package com.littlechoc.olddriver.obd.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @author Junhao Zhou 2017/4/22
 */

public class ObdCommandProxy extends ObdCommand {

  private ObdCommand proxy;

  public ObdCommandProxy(ObdCommand other) {
    super(other);
    proxy = other;
  }

  @Override
  public String getFormattedResult() {
    return proxy.getFormattedResult();
  }

  @Override
  public String getName() {
    return proxy.getName();
  }

  @Override
  public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
    proxy.run(in, out);
  }

  @Override
  protected void sendCommand(OutputStream out) throws IOException, InterruptedException {
    proxy.sendCommand(out);
  }

  @Override
  protected void resendCommand(OutputStream out) throws IOException, InterruptedException {
    proxy.resendCommand(out);
  }

  @Override
  protected void readResult(InputStream in) throws IOException {
    super.readResult(in);
  }

  @Override
  public String getResult() {
    return proxy.getResult();
  }

  @Override
  public ArrayList<Integer> getBuffer() {
    return proxy.getBuffer();
  }

  @Override
  public String getCommand() {
    return proxy.getCommand();
  }

  @Override
  public boolean useImperialUnits() {
    return proxy.useImperialUnits();
  }

  @Override
  public void useImperialUnits(boolean isImperial) {
    proxy.useImperialUnits(isImperial);
  }

  public ObdCommand getOriginCommand() {
    return proxy;
  }

  @Override
  public String getString() {
    return proxy.getString();
  }
}
