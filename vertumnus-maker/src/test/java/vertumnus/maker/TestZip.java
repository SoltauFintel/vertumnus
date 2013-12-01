package vertumnus.maker;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import vertumnus.base.Zip;

public class TestZip {

	@Test
	public void unzip() {
		File toDir = new File("temp/unzip-c-root");
		toDir.mkdirs();
		Zip.unzip("preparation/testmodule.zip", toDir.toString());
		Assert.assertEquals(9033, new File(toDir, "datei-1.1.0.odt").length());
		Assert.assertTrue("ein leeres Verzeichnis fehlt", new File(toDir,
				"empty").isDirectory());
	}
}
