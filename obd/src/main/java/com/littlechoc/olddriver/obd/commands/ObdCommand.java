/*
 * TODO put header
 */

package com.littlechoc.olddriver.obd.commands;

import com.littlechoc.commonutils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * TODO put description
 */
public abstract class ObdCommand {

  private static final String TAG = "ObdCommand";

  protected ArrayList<Integer> buffer = null;
  protected String cmd = null;
  protected boolean useImperialUnits = false;
  protected String rawData = null;

  /**
   * Default ctor to use
   *
   * @param command the command to send
   */
  public ObdCommand(String command) {
    this.cmd = command;
    this.buffer = new ArrayList<>();
  }

  /**
   * Prevent empty instantiation
   */
  private ObdCommand() {
  }

  /**
   * Copy ctor.
   *
   * @param other the ObdCommand to copy.
   */
  public ObdCommand(ObdCommand other) {
    this(other.cmd);
  }

  /**
   * Sends the OBD-II request and deals with the response.
   * <p>
   * This method CAN be overriden in fake commands.
   */
  public void run(InputStream in, OutputStream out) throws IOException,
          InterruptedException {
    sendCommand(out);
    readResult(in);
  }

  /**
   * Sends the OBD-II request.
   * <p>
   * This method may be overriden in subclasses, such as ObMultiCommand or
   * TroubleCodesObdCommand.
   *
   * @param out The command to send.
   */
  protected void sendCommand(OutputStream out) throws IOException,
          InterruptedException {

    long time = System.currentTimeMillis();
    // add the carriage return char
    String command = cmd + "\r";

    // write to OutputStream, or in this case a BluetoothSocket
    out.write(command.getBytes());
    out.flush();

    Logger.e(TAG, "send data cost %d", System.currentTimeMillis() - time);
		/*
     * HACK GOLDEN HAMMER ahead!!
		 * 
		 * TODO clean
		 * 
		 * Due to the time that some systems may take to respond, let's give it
		 * 500ms.
		 */
//    Thread.sleep(200);
  }

  /**
   * Resends this command.
   */
  protected void resendCommand(OutputStream out) throws IOException,
          InterruptedException {
    out.write("\r".getBytes());
    out.flush();
    /*
     * HACK GOLDEN HAMMER ahead!!
		 * 
		 * TODO clean this
		 * 
		 * Due to the time that some systems may take to respond, let's give it
		 * 500ms.
		 */
    // Thread.sleep(250);
  }

  /**
   * Reads the OBD-II response.
   * <p>
   * This method may be overriden in subclasses, such as ObdMultiCommand.
   */
  protected void readResult(InputStream in) throws IOException {
    long time = System.currentTimeMillis();
    byte b = 0;
    StringBuilder res = new StringBuilder();

    // read until '>' arrives
    while ((char) (b = (byte) in.read()) != '>') {
      if ((char) b != ' ') {
        res.append((char) b);
      }
    }
    Logger.e(TAG, "read data cost %d", System.currentTimeMillis() - time);

		/*
     * Imagine the following response 41 0c 00 0d.
		 *
		 * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
		 * attention to the fact that I've put the word byte in quotes, because
		 * 41 is actually TWO bytes (two chars) in the socket. So, we must do
		 * some more processing..
		 */
    //
    rawData = res.toString().trim();
    Logger.d(TAG, "rawData = %s", rawData);

    // clear buffer
    buffer.clear();

    // read string each two chars
    int begin = 0;
    int end = 2;
    try {
      while (end <= rawData.length()) {
        String temp = "0x" + rawData.substring(begin, end);
        buffer.add(Integer.decode(temp));
        begin = end;
        end += 2;
      }
    } catch (NumberFormatException e) {
      Logger.e(TAG, "rawData is String");
      buffer.clear();
    }
  }

  /**
   * @return the raw command response in string representation.
   */
  public String getResult() {
    if (rawData.contains("SEARCHING") || rawData.contains("DATA")) {
      rawData = "NODATA";
    }

    return rawData;
  }

  /**
   * @return a formatted command response in string representation.
   */
  public abstract String getFormattedResult();

  /******************************************************************
   * Getters & Setters
   */

  /**
   * @return a list of integers
   */
  public ArrayList<Integer> getBuffer() {
    return buffer;
  }

  /**
   * Returns this command in string representation.
   *
   * @return the command
   */
  public String getCommand() {
    return cmd;
  }

  /**
   * @return true if imperial units are used, or false otherwise
   */
  public boolean useImperialUnits() {
    return useImperialUnits;
  }

  /**
   * Set to 'true' if you want to use imperial units, false otherwise. By
   * default this value is set to 'false'.
   *
   * @param isImperial
   */
  public void useImperialUnits(boolean isImperial) {
    this.useImperialUnits = isImperial;
  }

  /**
   * @return the OBD command name.
   */
  public abstract String getName();

  public String getString() {
    return getCommand() + "|" + getResult() + "|" + getName();
  }

}