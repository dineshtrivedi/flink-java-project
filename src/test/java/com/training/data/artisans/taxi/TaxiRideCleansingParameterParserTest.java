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
	public void testWhenHasHelpParameterOthersParamsAreIgnored() throws Exception {
		String[] args = {"--help", "--input", "/this/path/should/not/be/read"};

		boolean wasHelpPrinted = parser.parseParams(args);

		Assert.assertTrue(wasHelpPrinted);
		Assert.assertNull(parser.getDataFilePath());
	}

	@Test(expected = Exception.class)
	public void testInputArgumentRequired() throws Exception {
		String[] args = {"--path", "/this/path/should/not/be/read"};

		parser.parseParams(args);
		Assert.fail("--input argument is required. An exception was expected");
	}

	@Test
	public void testPassingAllParametersHappyPath() throws Exception {
		final String expectedInput = "/this/path/should/not/be/read";

		String[] args = {"--input", expectedInput};

		boolean wasHelpPrinted = parser.parseParams(args);
		Assert.assertFalse(wasHelpPrinted);

		Assert.assertEquals(expectedInput, parser.getDataFilePath());
	}
}
