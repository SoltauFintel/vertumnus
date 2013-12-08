package vertumnus.client.version;

import org.junit.Assert;
import org.junit.Test;

import vertumnus.client.WhatVersion;

/**
 * Test of the methods compare and makeVersion of the class WhatVersion
 */
public class TestCompareVersion {

	@Test
	public void equal_2() {
		Assert.assertEquals(0, WhatVersion.compare("1.0", "1.0"));
	}

	@Test
	public void equal_3() {
		Assert.assertEquals(0, WhatVersion.compare("18.0.0", "18.0.0"));
	}

	@Test
	public void smaller_2() {
		Assert.assertTrue(WhatVersion.compare("18.4", "18.5") < 0);
	}

	@Test
	public void smaller_3a() {
		Assert.assertTrue(WhatVersion.compare("18.4.17", "18.5.0") < 0);
	}

	@Test
	public void smaller_9_10a() {
		Assert.assertTrue(WhatVersion.compare("18.9.0", "18.10.0") < 0);
	}

	@Test
	public void smaller_9_10b() {
		Assert.assertTrue(WhatVersion.compare("9.9", "10.0") < 0);
		Assert.assertTrue("Test 2", WhatVersion.compare("10.0", "9.9") > 0); // vice versa
	}

	@Test
	public void smaller_3b() {
		Assert.assertTrue(WhatVersion.compare("18.4.17", "19.0.0") < 0);
		Assert.assertTrue("Test 2", WhatVersion.compare("19.0.0", "18.4.17") > 0); // vice versa
	}
	
	@Test
	public void makeVersionTest() {
		Assert.assertEquals("00000001.00000023.~", WhatVersion.makeVersion("1.23"));
		Assert.assertEquals("00000001.00000023.00000456.~", WhatVersion.makeVersion("001.023.456"));
	}

	@Test
	public void makeVersionTest_beta() {
		Assert.assertEquals("00000001.00000023.-beta", WhatVersion.makeVersion("1.23-beta"));
	}

	@Test
	public void beta1() {
		Assert.assertTrue(WhatVersion.compare("18.0.0-beta", "18.0.0") < 0);
	}

	@Test
	public void beta2() {
		Assert.assertTrue(WhatVersion.compare("18.0.0-beta", "18.0.1") < 0);
	}

	@Test
	public void beta3() {
		Assert.assertTrue(WhatVersion.compare("18.0.0", "18.1.0-beta") < 0);
	}

	@Test
	public void invalidCharacters() {
		try {
			WhatVersion.compare("18.0.0beta", "18.0.0");
			Assert.fail("RuntimeException expected");
		} catch (RuntimeException e) {
			Assert.assertTrue(e.getMessage().toLowerCase().contains("invalid"));
		}
	}
}
