package vertumnus.client;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import vertumnus.Vertumnus;

public class TestUpdater_CLI extends TestUpdater {

	@Test
	public void stage1() {
		updater("1", "", null);
		Assert.assertEquals("Next version is wrong!\n", "NV="
				+ getExpectedVersion() + ";", Vertumnus.getResult());
	}

	private void updater(String stages, String installFolder, String currentVersion) {
		Vertumnus.debug = true;
		Vertumnus.main(new String[] { "-d", DIRECTORY, "-s", stages, "-m",
				MODULENAME, "-v", currentVersion == null ? CURRENT_VERSION : currentVersion, "-l", getLine(), "-i",
				installFolder });
	}

	protected String getLine() {
		return "major";
	}

	@Override
	public void stage1_noUpdate() {
		updater("1", "", CURRENT_VERSION_NEW);
		Assert.assertEquals("Next version is wrong!\n", "exit=-2;", Vertumnus.getResult());
	}

	@Test
	public void stage2() {
		updater("12", "", null);
		Assert.assertFalse("Return value is empty!", Vertumnus.getResult().isEmpty());
		int o = Vertumnus.getResult().indexOf("dn=");
		Assert.assertTrue("'dn=' not found:\r\n" + Vertumnus.getResult(), o > 0);
		String dn = Vertumnus.getResult().substring(o + 3);
		dn = dn.substring(0, dn.length() - 1);
		Assert.assertTrue("File was not loaded, etc.", new File(dn).exists());
	}

	@Test
	public void stage3() {
		updater("123", "temp", null);
		Assert.assertTrue(Vertumnus.getResult().contains("installed;"));
	}

	@Test
	public void stage3_emptyMode() {
		Vertumnus.debug = true;
		Vertumnus.main(new String[] { "-d", DIRECTORY, "-m", MODULENAME,
				"-v", CURRENT_VERSION, "-l", getLine(), "-i", "temp"});
		Assert.assertTrue(Vertumnus.getResult().contains("installed;"));
	}
	
	@Override
	public void directoryDoesNotExist() {
		Vertumnus.debug = true;
		Vertumnus.main(new String[] { "-d",
				"http://mwvb.de/vertumnus/directory-does-not-exist.xml", "-m",
				MODULENAME, "-v", CURRENT_VERSION, "-l", getLine(), "-i", "temp",
				"-s", "1" });
	}
}
