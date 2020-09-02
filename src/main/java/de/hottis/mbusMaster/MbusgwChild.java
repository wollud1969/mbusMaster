package de.hottis.mbusMaster;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusgwChild {
	static final Logger logger = LogManager.getRootLogger();


  private Process mbusgwProcess;
  private InputStream processInput;
  private OutputStream processOutput;
  private boolean verbose;
  private Thread stderrToLog;


  public MbusgwChild(boolean verbose) {
    this.verbose = verbose;
  }
	
	public void start() throws IOException {
		logger.info("InterfaceChild starting");

    ArrayList<String> arguments = new ArrayList<>();
    arguments.add("/usr/local/bin/mbusgw");
    arguments.add("-l");
    if (this.verbose) {
      arguments.add("-v");
    }
		ProcessBuilder pb = new ProcessBuilder(arguments);
		this.mbusgwProcess = pb.start();
		logger.debug("Process started");

		this.processInput = this.mbusgwProcess.getInputStream();
		InputStream processError = this.mbusgwProcess.getErrorStream();
		this.processOutput = this.mbusgwProcess.getOutputStream();
		logger.debug("Streams collected");

		BufferedReader br = new BufferedReader(new InputStreamReader(processError));

		this.stderrToLog = new Thread(() -> {
			String line;
			try {
				while ((line = br.readLine()) != null) {
					logger.debug(line);
				}
			} catch (IOException ex) {
				logger.debug("exception caught: " + ex.getMessage());
			}
		}, "stderrToLog");
		stderrToLog.start();
    logger.debug("stderrToLog thread started");
  }

  public void stop() throws InterruptedException, IOException {
    logger.info("About to stop mbusgw child process");
    this.sendRequest((byte)0, (byte)0);
    // this.mbusgwProcess.destroy();
    this.mbusgwProcess.waitFor();
    logger.info("Process stopped");

    logger.info("About to stop stderrToLog thread");
    this.stderrToLog.interrupt();
    this.stderrToLog.join();
    logger.info("Thread joined");
  }

  public InputStream getProcessInputStream() {
    return this.processInput;
  }

  public OutputStream getProcessOutputStream() {
    return this.processOutput;
  }

  public void sendRequest(byte cmd, byte addr) throws IOException {
		byte[] b = { cmd, addr };

		this.processOutput.write(b);
		logger.debug("Request written");

		this.processOutput.flush();
		logger.debug("Request flushed");
  }

  public byte[] collectResponse() throws IOException {
		byte[] header = new byte[2];
		int n = this.processInput.read(header, 0, 2);
		int responseCode = Byte.toUnsignedInt(header[0]);
    int responseLen = Byte.toUnsignedInt(header[1]);
    logger.debug("n: " + n + ", h: " + responseCode + ", l: " + responseLen);
    if (responseCode != 0) {
      logger.debug("Received error from child: " + responseCode);
      throw new MbusgwChildException("Error " + responseCode + " from child");
    }
		byte[] frame = new byte[responseLen];
    n = this.processInput.read(frame, 0, responseLen);
    logger.debug("frame completely read");
    return frame;
  }
}
