package com.training.data.artisans.taxi;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TaxiRideCleansingRunner {

	public static void main(String[] args) throws Exception {

		// get an ExecutionEnvironment
		StreamExecutionEnvironment env =
				StreamExecutionEnvironment.getExecutionEnvironment();
		// configure event-time processing
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		final String dataFilePath = "/home/dinesh/workspace/flink-java-project/nycTaxiRides.gz";

		TaxiRideCleansing taxiRideCleansing = new TaxiRideCleansing(env, dataFilePath, 0, 0);
		taxiRideCleansing.execute();
	}

}
