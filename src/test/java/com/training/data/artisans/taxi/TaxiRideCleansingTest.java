package com.training.data.artisans.taxi;


import org.apache.flink.api.common.functions.MapFunction;
import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.operators.StreamSink;
import org.apache.flink.streaming.util.StreamingMultipleProgramsTestBase;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class TaxiRideCleansingTest extends StreamingMultipleProgramsTestBase {
	@Test
	public void dummyTest() throws Exception {
		DateTime now = new DateTime();
		Collection<TaxiRide> taxiRides = new ArrayList<>();
		TaxiRide taxiRideNYC_1 = new TaxiRide(1, true, now, now, (float)GeoUtils.LON_EAST,
				(float)GeoUtils.LAT_NORTH, (float)GeoUtils.LON_WEST, (float)GeoUtils.LAT_SOUTH, (short)3);
		taxiRides.add(taxiRideNYC_1);

		TaxiRide taxiRideNYC_2 = new TaxiRide(2, true, now, now, (float)GeoUtils.LON_EAST,
				(float)GeoUtils.LAT_NORTH, (float)GeoUtils.LON_WEST, (float)GeoUtils.LAT_SOUTH, (short)3);
		taxiRides.add(taxiRideNYC_2);

		TaxiRide taxiRideNotInNYC_1 = new TaxiRide(2, true, now, now, (float)GeoUtils.LON_EAST + 1,
				(float)GeoUtils.LAT_NORTH, (float)GeoUtils.LON_WEST, (float)GeoUtils.LAT_SOUTH, (short)3);
		taxiRides.add(taxiRideNotInNYC_1);

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		DataStream<TaxiRide> rides = env.fromCollection(taxiRides);

		TaxiRideCleansing taxiRideCleansing = new TaxiRideCleansing();

		DataStream<TaxiRide> filteredRides = taxiRideCleansing.execute(rides);

		Collection<TaxiRide> RESULTS = new ArrayList<>();
		// And perform an Identity map, because we want to write all values of this day to the Database:
		filteredRides.addSink(new ResultsSinkFunction(RESULTS));

		env.execute("Running Taxi Ride Cleansing");

//		Assert.assertEquals(2, RESULTS.size());
	}

	public static final class ResultsSinkFunction implements SinkFunction<TaxiRide> {
		private Collection<TaxiRide> taxiRides;

		public ResultsSinkFunction(Collection<TaxiRide> taxiRides) {
			this.taxiRides = taxiRides;
		}

		@Override
		public void invoke(TaxiRide taxiRide) throws Exception {
			taxiRides.add(taxiRide);
		}
	}
}
