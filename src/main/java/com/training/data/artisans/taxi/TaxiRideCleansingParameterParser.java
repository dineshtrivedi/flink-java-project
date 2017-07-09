package com.training.data.artisans.taxi;

import org.apache.flink.api.java.utils.ParameterTool;

public class TaxiRideCleansingParameterParser {

	public static final int MAX_PASSENGER_COUNT_DEFAULT = 0;
	public static final int MIN_PASSENGER_COUNT_DEFAULT = 0;

	private String dataFilePath;
	private int maxPassengerCnt;
	private int minPassengerCnt;

	private void printHelpMessage() {
		System.out.println("Taxi Ride Cleasing Parameters");
		System.out.println("Required");
		System.out.println("\t--input - Full path to gz data file");
		System.out.println("Optional");
		System.out.println("\t--help  - Show help instructions");
		System.out.println("\t--max-p - Maximum number of passenger");
		System.out.println("\t--min-p - Minimum number of passenger");
	}

	public boolean parseParams(String[] args) throws Exception {
		boolean isHelpParameterNotIncluded = true;
		ParameterTool parameter = ParameterTool.fromArgs(args);

		if(parameter.has("help")){
			printHelpMessage();
			isHelpParameterNotIncluded = false;
		}
		else {
			dataFilePath = parameter.getRequired("input");
			maxPassengerCnt = parameter.getInt("max-p", MAX_PASSENGER_COUNT_DEFAULT);
			minPassengerCnt = parameter.getInt("min-p", MIN_PASSENGER_COUNT_DEFAULT);

			try {
				if(maxPassengerCnt < 0 || minPassengerCnt < 0) {
					throw new Exception("Maximum number and Mininum number of passenger must be positive - Max: "
							+ maxPassengerCnt + " Min: " + minPassengerCnt);
				}
				if(maxPassengerCnt < minPassengerCnt){
					throw new Exception("Maximum number of passenger cannot be smaller than the Mininum number of passenger " +
							"- Max: " + maxPassengerCnt + " Min: " + minPassengerCnt);
				}
			}
			catch (Exception e) {
				printHelpMessage();
				throw e;
			}
		}

		return isHelpParameterNotIncluded;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public int getMaxPassengerCnt() {
		return maxPassengerCnt;
	}

	public int getMinPassengerCnt() {
		return minPassengerCnt;
	}
}
