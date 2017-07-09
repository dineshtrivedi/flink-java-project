package com.training.data.artisans;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TaxiRideCleansing {
	public static void main(String[] args) throws Exception {
		// get an ExecutionEnvironment
		StreamExecutionEnvironment env =
				StreamExecutionEnvironment.getExecutionEnvironment();
		// configure event-time processing
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		final int maxEventDelay = 60;       // events are out of order by max 60 seconds
		final int servingSpeedFactor = 600; // events of 10 minutes are served in 1 second (10 * 60) = 600s
		final String dataFilePath = "/home/dinesh/workspace/flink-java-project/nycTaxiRides.gz";

		// get the taxi ride data stream
		DataStream<TaxiRide> rides = env.addSource(
				new TaxiRideSource(dataFilePath, maxEventDelay, servingSpeedFactor));

		DataStream<TaxiRide> filteredRidesByNYC = rides
				.filter(new NewYorkTaxiFilter());

		filteredRidesByNYC.print();

		env.execute("Running Taxi Ride Cleansing");
	}

	public static final class NewYorkTaxiFilter implements FilterFunction<TaxiRide> {

		@Override
		public boolean filter(TaxiRide taxiRide) {
			return GeoUtils.isInNYC(taxiRide.endLon, taxiRide.endLat) &&
					GeoUtils.isInNYC(taxiRide.startLon, taxiRide.startLat);
		}
	}
}
