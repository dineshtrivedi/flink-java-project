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

		boolean wasHelpPrinted = parser.parseParams(args);

		Assert.assertTrue(wasHelpPrinted);
		Assert.assertNull(parser.getDataFilePath());
	}

	@Test(expected = Exception.class)
	public void test_input_argument_required() throws Exception {
		String[] args = {"--path", "/this/path/should/not/be/read"};

		parser.parseParams(args);
		Assert.fail("--input argument is required. An exception was expected");
	}

	@Test
	public void test_passing_all_parameters_happy_path() throws Exception {
		final String expectedInput = "/this/path/should/not/be/read";

		String[] args = {"--input", expectedInput};

		boolean wasHelpPrinted = parser.parseParams(args);
		Assert.assertFalse(wasHelpPrinted);

		Assert.assertEquals(expectedInput, parser.getDataFilePath());
	}
}
