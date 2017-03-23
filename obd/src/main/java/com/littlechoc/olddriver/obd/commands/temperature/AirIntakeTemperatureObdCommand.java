/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.commands.temperature;

import com.littlechoc.olddriver.obd.enums.AvailableCommandNames;

/**
 * TODO
 * <p>
 * put description
 */
public class AirIntakeTemperatureObdCommand extends TemperatureObdCommand {

  public AirIntakeTemperatureObdCommand() {
    super("01 0F");
  }

  public AirIntakeTemperatureObdCommand(AirIntakeTemperatureObdCommand other) {
    super(other);
  }

  @Override
  public String getName() {
    return AvailableCommandNames.AIR_INTAKE_TEMP.getValue();
  }

}