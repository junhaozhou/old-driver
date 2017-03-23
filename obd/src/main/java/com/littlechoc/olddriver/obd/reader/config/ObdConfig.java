/*
 * TODO put header
 */
package com.littlechoc.olddriver.obd.reader.config;

import com.littlechoc.olddriver.obd.commands.ObdCommand;
import com.littlechoc.olddriver.obd.commands.SpeedObdCommand;
import com.littlechoc.olddriver.obd.commands.control.CommandEquivRatioObdCommand;
import com.littlechoc.olddriver.obd.commands.control.DtcNumberObdCommand;
import com.littlechoc.olddriver.obd.commands.control.TimingAdvanceObdCommand;
import com.littlechoc.olddriver.obd.commands.control.TroubleCodesObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.EngineLoadObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.EngineRPMObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.EngineRuntimeObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.MassAirFlowObdCommand;
import com.littlechoc.olddriver.obd.commands.engine.ThrottlePositionObdCommand;
import com.littlechoc.olddriver.obd.commands.fuel.FindFuelTypeObdCommand;
import com.littlechoc.olddriver.obd.commands.fuel.FuelLevelObdCommand;
import com.littlechoc.olddriver.obd.commands.fuel.FuelTrimObdCommand;
import com.littlechoc.olddriver.obd.commands.pressure.BarometricPressureObdCommand;
import com.littlechoc.olddriver.obd.commands.pressure.FuelPressureObdCommand;
import com.littlechoc.olddriver.obd.commands.pressure.IntakeManifoldPressureObdCommand;
import com.littlechoc.olddriver.obd.commands.protocol.ObdResetCommand;
import com.littlechoc.olddriver.obd.commands.temperature.AirIntakeTemperatureObdCommand;
import com.littlechoc.olddriver.obd.commands.temperature.AmbientAirTemperatureObdCommand;
import com.littlechoc.olddriver.obd.commands.temperature.EngineCoolantTemperatureObdCommand;
import com.littlechoc.olddriver.obd.enums.FuelTrim;

import java.util.ArrayList;

/**
 * TODO put description
 */
public final class ObdConfig {

  public static ArrayList<ObdCommand> getCommands() {
    ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
    // Protocol
    cmds.add(new ObdResetCommand());

    // Control
    cmds.add(new CommandEquivRatioObdCommand());
    cmds.add(new DtcNumberObdCommand());
    cmds.add(new TimingAdvanceObdCommand());
    cmds.add(new TroubleCodesObdCommand(0));

    // Engine
    cmds.add(new EngineLoadObdCommand());
    cmds.add(new EngineRPMObdCommand());
    cmds.add(new EngineRuntimeObdCommand());
    cmds.add(new MassAirFlowObdCommand());

    // Fuel
    // cmds.add(new AverageFuelEconomyObdCommand());
    // cmds.add(new FuelEconomyObdCommand());
    // cmds.add(new FuelEconomyMAPObdCommand());
    // cmds.add(new FuelEconomyCommandedMAPObdCommand());
    cmds.add(new FindFuelTypeObdCommand());
    cmds.add(new FuelLevelObdCommand());
    cmds.add(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_1));
    cmds.add(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_2));
    cmds.add(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_1));
    cmds.add(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_2));

    // Pressure
    cmds.add(new BarometricPressureObdCommand());
    cmds.add(new FuelPressureObdCommand());
    cmds.add(new IntakeManifoldPressureObdCommand());

    // Temperature
    cmds.add(new AirIntakeTemperatureObdCommand());
    cmds.add(new AmbientAirTemperatureObdCommand());
    cmds.add(new EngineCoolantTemperatureObdCommand());

    // Misc
    cmds.add(new SpeedObdCommand());
    cmds.add(new ThrottlePositionObdCommand());

    return cmds;
  }

}