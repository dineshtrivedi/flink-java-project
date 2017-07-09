package com.training.data.artisans.taxi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaxiRideCleansingParameterParserTest {

	private TaxiRideCleansingParameterParser parser;

	@Before
	public void setUp(){
		parser = new TaxiRideCleansingParameterParser();
	}

	@Test
	public void test_when_has_help_parameter_others_params_are_ignored() throws Exception {
		String[] args = {"--help", "--input", "/this/path/should/not/be/read"};

		boolean isHelpParameterIncluded = !parser.parseParams(args);

		Assert.assertTrue(isHelpParameterIncluded);

		Assert.assertNull(parser.getDataFilePath());
		Assert.assertEquals(TaxiRideCleansingParameterParser.MAX_PASSENGER_COUNT_DEFAULT,
				parser.getMaxPassengerCnt());
		Assert.assertEquals(TaxiRideCleansingParameterParser.MIN_PASSENGER_COUNT_DEFAULT,
				parser.getMinPassengerCnt());
	}

	@Test(expected = Exception.class)
	public void test_input_argument_required() throws Exception {
		String[] args = {"--path", "/this/path/should/not/be/read"};

		parser.parseParams(args);
		Assert.fail("--input argument is required. An exception was expected");
	}

	@Test(expected = Exception.class)
	public void test_min_passenger_number_bigger_than_max_passenger_number() throws Exception {
		String[] args = {"--input", "/this/path/should/not/be/read", "--max-p", "1", "--min-p", "5"};

		parser.parseParams(args);
		Assert.fail("Maximum passenger number cannot be smaller than minimum passenger number");
	}

	@Test(expected = Exception.class)
	public void test_passengers_numbers_must_be_positive() throws Exception {
		String[] args = {"--input", "/this/path/should/not/be/read", "--max-p", "-1", "--min-p", "-5"};

		parser.parseParams(args);
		Assert.fail("Maximum and minimum passenger number must be positive");
	}

	@Test
	public void test_passing_all_parameters_happy_path() throws Exception {
		final String expectedInput = "/this/path/should/not/be/read";
		final int expectedMaxPassengerCount = 5;
		final int expectedMinPassengerCount = 3;

		String[] args = {"--input", expectedInput, "--max-p", Integer.toString(expectedMaxPassengerCount),
				"--min-p", Integer.toString(expectedMinPassengerCount)};

		boolean isHelpParameterIncluded = !parser.parseParams(args);

		Assert.assertFalse(isHelpParameterIncluded);

		Assert.assertEquals(expectedInput, parser.getDataFilePath());
		Assert.assertEquals(expectedMaxPassengerCount, parser.getMaxPassengerCnt());
		Assert.assertEquals(expectedMinPassengerCount,	parser.getMinPassengerCnt());
	}

	@Test
	public void test_default_parameters() throws Exception {
		final String expectedInput = "/this/path/should/not/be/read";

		String[] args = {"--input", expectedInput};

		boolean isHelpParameterIncluded = !parser.parseParams(args);

		Assert.assertFalse(isHelpParameterIncluded);

		Assert.assertEquals(expectedInput, parser.getDataFilePath());
		Assert.assertEquals(TaxiRideCleansingParameterParser.MAX_PASSENGER_COUNT_DEFAULT, parser.getMaxPassengerCnt());
		Assert.assertEquals(TaxiRideCleansingParameterParser.MIN_PASSENGER_COUNT_DEFAULT, parser.getMinPassengerCnt());
	}
}
