package com.training.data.artisans.taxi;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

public class TaxiRideCleansing {

	public DataStream<TaxiRide> execute(DataStream<TaxiRide> rides) throws Exception {
		DataStream<TaxiRide> filteredRidesByNYC = rides
				.filter(new NewYorkTaxiFilter());

		return filteredRidesByNYC;
	}

	public static final class NewYorkTaxiFilter implements FilterFunction<TaxiRide> {

		@Override
		public boolean filter(TaxiRide taxiRide) {
			return GeoUtils.isInNYC(taxiRide.endLon, taxiRide.endLat) &&
					GeoUtils.isInNYC(taxiRide.startLon, taxiRide.startLat);
		}
	}
}
