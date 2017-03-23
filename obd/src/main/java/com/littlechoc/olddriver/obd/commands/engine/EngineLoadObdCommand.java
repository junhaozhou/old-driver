/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.commands.engine;

import com.littlechoc.olddriver.obd.commands.ObdCommand;
import com.littlechoc.olddriver.obd.commands.PercentageObdCommand;
import com.littlechoc.olddriver.obd.enums.AvailableCommandNames;

/**
 * Calculated Engine Load value.
 */
public class EngineLoadObdCommand extends PercentageObdCommand {

  /**
   * @param command
   */
  public EngineLoadObdCommand() {
    super("01 04");
  }

  /**
   * @param other
   */
  public EngineLoadObdCommand(ObdCommand other) {
    super(other);
  }

  /* (non-Javadoc)
   * @see eu.lighthouselabs.MyCommand.commands.ObdCommand#getName()
   */
  @Override
  public String getName() {
    return AvailableCommandNames.ENGINE_LOAD.getValue();
  }

}