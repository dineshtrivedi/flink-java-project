package com.training.data.artisans.taxi;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.GeoUtils;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.TaxiRideSchema;
import com.google.common.collect.Iterables;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class PoupularPlacesMain {

	// events are out of order by max 60 seconds
	private static final int MAX_EVENT_DELAY_DEFAULT = 60;

	// events of 10 minutes are served in 1 second (10 * 60) = 600s
	private static final int SERVING_SPEED_FACTOR_DEFAULT = 600;

	private static final int POPULAR_PLACES_COUNTER_THRESHOLD = 20;

	public static void main(String[] args) throws Exception {

		// get an ExecutionEnvironment
		StreamExecutionEnvironment env =
				StreamExecutionEnvironment.getExecutionEnvironment();
		// configure event-time processing
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		// generate a Watermark every second
		env.getConfig().setAutoWatermarkInterval(1000);

		// configure Kafka consumer
		Properties props = new Properties();
		props.setProperty("zookeeper.connect", "localhost:2181"); // Zookeeper default host:port
		props.setProperty("bootstrap.servers", "localhost:9092"); // Broker default host:port
		props.setProperty("group.id", "myGroup");                 // Consumer group ID
		props.setProperty("auto.offset.reset", "earliest");       // Always read topic from start

		// create a Kafka consumer
		FlinkKafkaConsumer010<TaxiRide> consumer =
				new FlinkKafkaConsumer010<>(
						"cleansedRides",
						new TaxiRideSchema(),
						props);

		// assign a timestamp extractor to the consumer
		consumer.assignTimestampsAndWatermarks(new PopulatPlacesWatermarkOutOfOrdeness(MAX_EVENT_DELAY_DEFAULT));

		DataStream<TaxiRide> rides = env.addSource(consumer);

//		DataStream<TaxiRide> rides = env.addSource(
//				new TaxiRideSource("/Users/dineshat/solo/flink-java-project/nycTaxiRides.gz", MAX_EVENT_DELAY_DEFAULT, SERVING_SPEED_FACTOR_DEFAULT));

		DataStream<Tuple5<Float, Float, Long, Boolean, Integer>> popoularPlaces = rides
				.filter(new TaxiRideCleansing.NewYorkTaxiFilter())
				.map(new MapToGridCell())
				.<KeyedStream<Tuple2<Integer, Boolean>, Tuple2<Integer, Boolean>>>keyBy(0, 1)
				.timeWindow(Time.minutes(15), Time.minutes(5))
				.apply(new RideCounterWindowFunction())
				.filter(new PopularPlaceThresholdFilter(POPULAR_PLACES_COUNTER_THRESHOLD))
				.map(new MapFromGridCellToLatLon());

		popoularPlaces.print();
		env.execute("Popular place task");
	}

	public static class MapToGridCell implements MapFunction<TaxiRide, Tuple2<Integer, Boolean>> {

		@Override
		public Tuple2<Integer, Boolean> map(TaxiRide taxiRide) throws Exception {
			float lon;
			float lat;
			final boolean isStart = taxiRide.isStart;
			if(isStart) {
				lon = taxiRide.startLon;
				lat = taxiRide.startLat;
			}
			else {
				lon = taxiRide.endLon;
				lat = taxiRide.endLat;
			}

			int gridId = GeoUtils.mapToGridCell(lon, lat);
			return Tuple2.of(gridId, isStart);
		}
	}

	public static class RideCounterWindowFunction implements WindowFunction<
			// input type
			Tuple2<Integer, Boolean>,
			// output type
			Tuple4<Integer, Long, Boolean, Integer>,
			// key type
			Tuple,
			// window type
			TimeWindow>
	{

		@Override
		public void apply(Tuple key, TimeWindow timeWindow, Iterable<Tuple2<Integer, Boolean>> events,
				Collector<Tuple4<Integer, Long, Boolean, Integer>> collector) throws Exception {

			Tuple2<Integer, Boolean> castedKey = (Tuple2<Integer, Boolean>)key;
			int gridId = castedKey.f0;
			boolean isStart = castedKey.f1;
			long windowTime = timeWindow.getEnd();
			int rideCounter = Iterables.size(events);

			collector.collect(Tuple4.of(gridId, windowTime, isStart, rideCounter));
		}
	}

	public static final class PopularPlaceThresholdFilter implements FilterFunction<Tuple4<Integer, Long, Boolean, Integer>> {

		private final int popularPlacesCounterThreshold;

		public PopularPlaceThresholdFilter(int popularPlacesCounterThreshold) {
			this.popularPlacesCounterThreshold = popularPlacesCounterThreshold;
		}

		@Override
		public boolean filter(Tuple4<Integer, Long, Boolean, Integer> place) {
			return place.f3 >= popularPlacesCounterThreshold;
		}
	}

	public static class MapFromGridCellToLatLon implements MapFunction<Tuple4<Integer, Long, Boolean, Integer>,
			Tuple5<Float, Float, Long, Boolean, Integer>> {


		@Override
		public Tuple5<Float, Float, Long, Boolean, Integer> map(Tuple4<Integer, Long, Boolean, Integer> cell) throws Exception {
			float lon = GeoUtils.getGridCellCenterLon(cell.f0);
			float lat = GeoUtils.getGridCellCenterLat(cell.f0);
			long timeWindow = cell.f1;
			boolean isStart = cell.f2;
			int count = cell.f3;

			return Tuple5.of(lon, lat, timeWindow, isStart, count);
		}
	}

	public static class PopulatPlacesWatermarkOutOfOrdeness extends BoundedOutOfOrdernessTimestampExtractor<TaxiRide> {
		public PopulatPlacesWatermarkOutOfOrdeness(int maxOutOfOrderness) {
			super(Time.seconds(maxOutOfOrderness));
		}

		@Override
		public long extractTimestamp(TaxiRide ride) {
			if (ride.isStart) {
				return ride.startTime.getMillis();
			}
			else {
				return ride.endTime.getMillis();
			}
		}
	}
}
