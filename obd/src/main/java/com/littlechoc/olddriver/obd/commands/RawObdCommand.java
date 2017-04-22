package com.littlechoc.olddriver.obd.commands;

/**
 * @author Junhao Zhou 2017/4/23
 */

public class RawObdCommand extends ObdCommand {

  public RawObdCommand(String command) {
    super(command);
  }

  @Override
  public String getFormattedResult() {
    return getResult();
  }

  @Override
  public String getName() {
    return getCommand();
  }
}
