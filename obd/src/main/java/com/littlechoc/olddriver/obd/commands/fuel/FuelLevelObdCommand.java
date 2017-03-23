/*
 * TODO put header 
 */
package com.littlechoc.olddriver.obd.commands.fuel;

import com.littlechoc.olddriver.obd.commands.ObdCommand;
import com.littlechoc.olddriver.obd.enums.AvailableCommandNames;

/**
 * Get fuel level in percentage
 */
public class FuelLevelObdCommand extends ObdCommand {

  private float fuelLevel = 0f;

  /**
   * @param command
   */
  public FuelLevelObdCommand() {
    super("01 2F");
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.lighthouselabs.MyCommand.commands.ObdCommand#getFormattedResult()
   */
  @Override
  public String getFormattedResult() {
    if (!"NODATA".equals(getResult())) {
      // ignore first two bytes [hh hh] of the response
      fuelLevel = 100.0f * buffer.get(2) / 255.0f;
    }

    return String.format("%.1f%s", fuelLevel, "%");
  }

  @Override
  public String getName() {
    return AvailableCommandNames.FUEL_LEVEL.getValue();
  }

}