/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.commands.protocol;

import com.littlechoc.olddriver.obd.commands.ObdCommand;

/**
 * This command will turn-off echo.
 */
public class EchoOffObdCommand extends ObdCommand {

  /**
   * @param command
   */
  public EchoOffObdCommand() {
    super("AT E0");
  }

  /**
   * @param other
   */
  public EchoOffObdCommand(ObdCommand other) {
    super(other);
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.lighthouselabs.MyCommand.commands.ObdCommand#getFormattedResult()
   */
  @Override
  public String getFormattedResult() {
    return getResult();
  }

  @Override
  public String getName() {
    return "Echo Off";
  }

}