package com.training.data.artisans.taxi;

import org.apache.flink.api.java.utils.ParameterTool;

public class TaxiRideCleansingParameterParser {

	private String dataFilePath;

	private void printHelpMessage() {
		System.out.println("Taxi Ride Cleasing Parameters");
		System.out.println("Required");
		System.out.println("\t--input - Full path to gz data file");
	}

	public boolean parseParams(String[] args) throws Exception {
		boolean wasHelpPrinted = false;
		ParameterTool parameter = ParameterTool.fromArgs(args);

		if(parameter.has("help")){
			printHelpMessage();
			wasHelpPrinted = true;
		}
		else {
			try {
				dataFilePath = parameter.getRequired("input");
			}
			catch(Exception e) {
				printHelpMessage();
				throw e;
			}

		}

		return wasHelpPrinted;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}
}
