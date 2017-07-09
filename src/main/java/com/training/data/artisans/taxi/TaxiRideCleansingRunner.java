package com.training.data.artisans.taxi;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TaxiRideCleansingRunner {

	// events are out of order by max 60 seconds
	private static final int MAX_EVENT_DELAY_DEFAULT = 60;

	// events of 10 minutes are served in 1 second (10 * 60) = 600s
	private static final int SERVING_SPEED_FACTOR_DEFAULT = 600;

	public static void main(String[] args) throws Exception {

		TaxiRideCleansingParameterParser params = new TaxiRideCleansingParameterParser();
		if(params.parseParams(args)){
			final String dataFilePath = params.getDataFilePath();
			final int maxPassengerCnt = params.getMaxPassengerCnt();
			final int minPassengerCnt = params.getMinPassengerCnt();

			if(maxPassengerCnt < 0 || minPassengerCnt < 0) {
				throw new Exception("Maximum number and Mininum number of passenger must be positive - Max: "
						+ maxPassengerCnt + " Min: " + minPassengerCnt);
			}
			if(maxPassengerCnt < minPassengerCnt){
				throw new Exception("Maximum number of passenger cannot be smaller than the Mininum number of passenger " +
						"- Max: " + maxPassengerCnt + " Min: " + minPassengerCnt);
			}

			// get an ExecutionEnvironment
			StreamExecutionEnvironment env =
					StreamExecutionEnvironment.getExecutionEnvironment();
			// configure event-time processing
			env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

			// get the taxi ride data stream
			DataStream<TaxiRide> rides = env.addSource(
					new TaxiRideSource(dataFilePath, MAX_EVENT_DELAY_DEFAULT, SERVING_SPEED_FACTOR_DEFAULT));

			TaxiRideCleansing taxiRideCleansing = new TaxiRideCleansing(maxPassengerCnt, minPassengerCnt);
			DataStream<TaxiRide> filteredTaxis = taxiRideCleansing.execute(rides);

			filteredTaxis.print();
			env.execute("Running Taxi Ride Cleansing");
		}
	}

}
