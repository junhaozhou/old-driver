/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.commands.control;

import com.littlechoc.olddriver.obd.commands.PercentageObdCommand;
import com.littlechoc.olddriver.obd.enums.AvailableCommandNames;

/**
 * TODO put description
 * <p>
 * Timing Advance
 */
public class TimingAdvanceObdCommand extends PercentageObdCommand {

  public TimingAdvanceObdCommand() {
    super("01 0E");
  }

  public TimingAdvanceObdCommand(TimingAdvanceObdCommand other) {
    super(other);
  }

  @Override
  public String getName() {
    return AvailableCommandNames.TIMING_ADVANCE.getValue();
  }
}