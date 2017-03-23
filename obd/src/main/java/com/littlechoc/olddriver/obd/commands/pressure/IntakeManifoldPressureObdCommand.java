/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.commands.pressure;

import com.littlechoc.olddriver.obd.enums.AvailableCommandNames;


/**
 * Intake Manifold Pressure
 */
public class IntakeManifoldPressureObdCommand extends PressureObdCommand {

  /**
   * Default ctor.
   */
  public IntakeManifoldPressureObdCommand() {
    super("01 0B");
  }

  /**
   * Copy ctor.
   *
   * @param other
   */
  public IntakeManifoldPressureObdCommand(
          IntakeManifoldPressureObdCommand other) {
    super(other);
  }

  @Override
  public String getName() {
    return AvailableCommandNames.INTAKE_MANIFOLD_PRESSURE.getValue();
  }

}