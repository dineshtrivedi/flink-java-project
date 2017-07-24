package com.training.data.artisans.taxi;

import com.dataartisans.flinktraining.exercises.datastream_java.datatypes.TaxiRide;
import com.dataartisans.flinktraining.exercises.datastream_java.sources.TaxiRideSource;
import com.dataartisans.flinktraining.exercises.datastream_java.utils.TaxiRideSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

public class TaxiRideCleansingRunner {

	// events are out of order by max 60 seconds
	private static final int MAX_EVENT_DELAY_DEFAULT = 60;

	// events of 10 minutes are served in 1 second (10 * 60) = 600s
	private static final int SERVING_SPEED_FACTOR_DEFAULT = 600;

	public static void main(String[] args) throws Exception {

		TaxiRideCleansingParameterParser params = new TaxiRideCleansingParameterParser();
		// TODO: refactor this method
		if(!params.parseParams(args)){
			final String dataFilePath = params.getDataFilePath();

			// get an ExecutionEnvironment
			StreamExecutionEnvironment env =
					StreamExecutionEnvironment.getExecutionEnvironment();
			// configure event-time processing
			env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

			// get the taxi ride data stream
			DataStream<TaxiRide> rides = env.addSource(
					new TaxiRideSource(dataFilePath, MAX_EVENT_DELAY_DEFAULT, SERVING_SPEED_FACTOR_DEFAULT));

			TaxiRideCleansing taxiRideCleansing = new TaxiRideCleansing();
			DataStream<TaxiRide> filteredRides = taxiRideCleansing.execute(rides);

			filteredRides.addSink(new FlinkKafkaProducer010<>(
					"localhost:9092",      // Kafka broker host:port
					"cleansedRides",       // Topic to write to
					new TaxiRideSchema())  // Serializer (provided as util)
			);

//			filteredRides.print();
			env.execute("Running Taxi Ride Cleansing");
		}
	}

}
