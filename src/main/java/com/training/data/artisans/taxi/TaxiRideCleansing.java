package com.training.data.artisans.taxi;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TaxiRideCleansing {

	// events are out of order by max 60 seconds
	private static final int MAX_EVENT_DELAY_DEFAULT = 60;

	// events of 10 minutes are served in 1 second (10 * 60) = 600s
	private static final int SERVING_SPEED_FACTOR_DEFAULT = 600;

	private final StreamExecutionEnvironment streamEnv;
	private final String dataFilePath;
	private final int maxPassagengerCnt;
	private final int minPassagengerCnt;
	private final int maxEventDelay;
	private final int servingSpeedFactor;

	public TaxiRideCleansing(StreamExecutionEnvironment streamEnv, String dataFilePath, int maxPassagengerCnt,
							int minPassagengerCnt){
		this(streamEnv, dataFilePath, maxPassagengerCnt, minPassagengerCnt, MAX_EVENT_DELAY_DEFAULT,
				SERVING_SPEED_FACTOR_DEFAULT);
	}

	public TaxiRideCleansing(StreamExecutionEnvironment streamEnv, String dataFilePath, int maxPassagengerCnt,
							int minPassagengerCnt, int maxEventDelay, int servingSpeedFactor){
		this.streamEnv = streamEnv;
		this.dataFilePath = dataFilePath;
		this.maxPassagengerCnt = maxPassagengerCnt;
		this.minPassagengerCnt = minPassagengerCnt;
		this.maxEventDelay = maxEventDelay;
		this.servingSpeedFactor = servingSpeedFactor;
	}

	public void execute() throws Exception {
		// get the taxi ride data stream
		DataStream<TaxiRide> rides = streamEnv.addSource(
				new TaxiRideSource(dataFilePath, maxEventDelay, servingSpeedFactor));

		DataStream<TaxiRide> filteredRidesByNYC = rides
				.filter(new NewYorkTaxiFilter());

		filteredRidesByNYC.print();

		streamEnv.execute("Running Taxi Ride Cleansing");
	}

	public static final class NewYorkTaxiFilter implements FilterFunction<TaxiRide> {

		@Override
		public boolean filter(TaxiRide taxiRide) {
			return GeoUtils.isInNYC(taxiRide.endLon, taxiRide.endLat) &&
					GeoUtils.isInNYC(taxiRide.startLon, taxiRide.startLat);
		}
	}
}
