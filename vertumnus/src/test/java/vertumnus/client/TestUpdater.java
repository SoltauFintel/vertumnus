package vertumnus.client;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test of class Updater
 */
public class TestUpdater {
	public static final String MODULENAME = "testmodule";
	public static final String CURRENT_VERSION = "1.0.1";
	public static final String CURRENT_VERSION_NEW = "2.0.1";
	private static final String HOST = "http://mwvb.de/vertumnus/"; // You must change this URL.
	public static final String DIRECTORY = HOST + "directory.xml";
	public static final String FALSE_DIRECTORY = HOST + "directory-does-not-exist.xml";

	// call vertumnus.maker.TestPrepare.prepare() for first installation 

	// Detects whether an update is available: yes
	@Test
	public void stage1() {
		Updater u = getUpdater();
		stage1portion(u);
	}

	private void stage1portion(Updater u) {
		String nextVersion = u.getNextVersion(MODULENAME, CURRENT_VERSION);
		Assert.assertEquals(getExpectedVersion(), nextVersion);
		System.out.println("Update to version " + nextVersion);
	}
	
	// Detects whether an update is available: no
	@Test
	public void stage1_noUpdate() {
		Updater u = getUpdater();
		String nextVersion = u.getNextVersion("Testmodul", CURRENT_VERSION_NEW);
		Assert.assertNull("Version must be null, but is: " + nextVersion, nextVersion);
	}
	
	// Load packet (to a temp folder)
	@Test
	public void stage2() {
		Updater u = getUpdater();
		stage1portion(u);
		String packet = u.load();
		Assert.assertNotNull(packet);
		File p = new File(packet);
		Assert.assertTrue("File not found: " + packet, p.exists());
		p.deleteOnExit();
	}
	
	// Unzip packet (This is the complete procedure incl. installation.)
	// <br>Required parameters:
	// directory URL, module name, current version, target folder, line (major, minor or bugfix?)
	@Test
	public void stage3() {
		final String filename = "temp/file-" + getExpectedVersion() + ".odt";
		new File(filename).delete();
		Updater u = getUpdater();
		stage1portion(u);
		File p = new File(u.load());
		u.install("temp");
		p.deleteOnExit();
		Assert.assertEquals("File size of " + filename + " is wrong.\n", getStage3Size(), new File(filename).length());
	}
	
	protected long getStage3Size() {
		return 9222;
	}

	protected Updater getUpdater() {
		Updater u = new Updater();
		u.setDirectory(DIRECTORY);
		return u;
	}
	
	@Test
	public void directoryDoesNotExist() {
		Updater u = new Updater();
		u.setDirectory("http://mwvb.de/vertumnus/directory-does-not-exist.xml");
		try {
			u.getNextVersion("Testmodule", "1.0.1");
			Assert.fail("RuntimeException expected");
		} catch (RuntimeException e) {
			Assert.assertTrue("\"doesn't exist\" is not part of the error message\n" + e.getMessage(),
					e.getMessage().contains("doesn't exist"));
		}
	}
	
	protected String getExpectedVersion() {
		return "2.0.1";
	}
}
