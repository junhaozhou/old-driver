/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.commands.engine;

import com.littlechoc.olddriver.obd.commands.PercentageObdCommand;
import com.littlechoc.olddriver.obd.enums.AvailableCommandNames;

/**
 * Read the throttle position in percentage.
 */
public class ThrottlePositionObdCommand extends PercentageObdCommand {

  /**
   * Default ctor.
   */
  public ThrottlePositionObdCommand() {
    super("01 11");
  }

  /**
   * Copy ctor.
   *
   * @param other
   */
  public ThrottlePositionObdCommand(ThrottlePositionObdCommand other) {
    super(other);
  }

  /**
   *
   */
  @Override
  public String getName() {
    return AvailableCommandNames.THROTTLE_POS.getValue();
  }

}