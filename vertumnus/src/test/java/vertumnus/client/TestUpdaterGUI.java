package vertumnus.client;

import java.io.File;

import org.junit.Test;

import vertumnus.Vertumnus;

/**
 * Test of the GUI
 */
public class TestUpdaterGUI {
	private static final String CURRENT_VERSION = "0.6.0";

	@Test
	public void gui() {
		Updater u = new Updater();
		u.setGUI(true);
		u.setTitle("TestUpdaterGUI");
		u.setDirectory(TestUpdater.DIRECTORY);
		u.setKeepMinorVersion(true);
		u.getNextVersion(TestUpdater.MODULENAME, CURRENT_VERSION);
		String packet = u.load();
		new File(packet).deleteOnExit();
	}
	
	@Test
	public void gui_CLI() {
		Vertumnus.main(new String[] {
				"-d", TestUpdater.DIRECTORY,
				"-s", "12",
				"-m", TestUpdater.MODULENAME,
				"-v", CURRENT_VERSION,
				"-l", "bugfix",
				"-g" });

	}
}
