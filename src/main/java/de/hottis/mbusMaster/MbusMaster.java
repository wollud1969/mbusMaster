package de.hottis.mbusMaster;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MbusMaster {
	static final String PROPS_FILENAME = "mbusMaster.props";
	static final Logger logger = LogManager.getRootLogger();
	
	
	public static void main(String[] args) throws Exception {
		logger.info("MbusMaster starting");

		/*
		final Properties config = new Properties();
		try (FileInputStream propsFileInputStream = new FileInputStream(PROPS_FILENAME)) {
			config.load(propsFileInputStream);
		}
		logger.debug("Configuration loaded");
		*/

		ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/mbusgw", "-l", "-v");
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process p = pb.start();
		System.out.println("Process started");



		InputStream i = p.getInputStream();
		OutputStream o = p.getOutputStream();
		System.out.println("Streams collected");

		byte[] b = { 0x5b, 80 };

		o.write(b);
		System.out.println("Data written");

		o.flush();
		System.out.println("Data flushed");


		byte[] header = new byte[2];
		int n = i.read(header, 0, 2);
		int responseCode = Byte.toUnsignedInt(header[0]);
		int responseLen = Byte.toUnsignedInt(header[1]);
		System.out.println("n: " + n + ", h: " + responseCode + ", l: " + responseLen);

		byte[] frame = new byte[responseLen];
		n = i.read(frame, 0, responseLen);
		for (byte x : frame) {
			System.out.print(Integer.toHexString(Byte.toUnsignedInt(x)) + " ");
		}
		System.out.println();



	}		
}
