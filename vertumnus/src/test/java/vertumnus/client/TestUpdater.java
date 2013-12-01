package vertumnus.client;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class TestUpdater {
	public static final String MODULNAME = "testmodule";
	public static final String IST_VERSION = "1.0.1";
	public static final String IST_VERSION_NEU = "2.0.1";
	public static final String VERZEICHNIS = "http://mwvb.de/vertumnus/directory.xml";

	// für erstmalige Einrichtung: vertumnus.maker.TestPrepare.prepare()

	// Feststellen, ob es ein Update gibt: ja
	@Test
	public void stage1() {
		Updater u = getUpdater();
		stage1teil(u);
	}

	private void stage1teil(Updater u) {
		String nextVersion = u.getNextVersion(MODULNAME, IST_VERSION);
		Assert.assertEquals(getExpectedVersion(), nextVersion);
		System.out.println("Update auf Version " + nextVersion);
	}

	// Feststellen, ob es ein Update gibt: nein
	@Test
	public void stage1_esGibtKeinUpdate() {
		Updater u = getUpdater();
		String nextVersion = u.getNextVersion("Testmodul", IST_VERSION_NEU);
		Assert.assertNull("Version müsste null sein, ist aber: " + nextVersion, nextVersion);
	}
	
	// Paket laden (in einen temp Folder)
	@Test
	public void stage2() {
		Updater u = getUpdater();
		stage1teil(u);
		String paket = u.load();
		Assert.assertNotNull(paket);
		File p = new File(paket);
		Assert.assertTrue("Datei nicht vorhanden: " + paket, p.exists());
		p.deleteOnExit();
	}
	
	// Paket entpacken (also vollständiger Vorgang inkl. Installation)
	// erforderliche Parameter: URL Verzeichnis, Modul, aktuelle Version, Zielordner, major/minor?
	@Test
	public void stage3() {
		final String dn = "temp/datei-" + getExpectedVersion() + ".odt";
		new File(dn).delete();
		Updater u = getUpdater();
		stage1teil(u);
		File p = new File(u.load());
		u.install("temp");
		p.deleteOnExit();
		Assert.assertEquals("Dateigröße von " + dn + " falsch\n", getStage3Size(), new File(dn).length());
	}
	
	protected long getStage3Size() {
		return 9222;
	}

	protected Updater getUpdater() {
		Updater u = new Updater();
		u.setVerzeichnis(VERZEICHNIS);
		return u;
	}
	
	@Test
	public void verzeichnisNichtVorhanden() {
		Updater u = new Updater();
		u.setVerzeichnis("http://mwvb.de/vertumnus/verzeichnis-nicht-da.xml");
		try {
			u.getNextVersion("Testmodul", "1.0.1");
			Assert.fail("RuntimeException erwartet");
		} catch (RuntimeException e) {
			Assert.assertTrue("'nicht vorhanden' nicht in der Fehlermeldung enthalten\n" + e.getMessage(),
					e.getMessage().contains("nicht vorhanden"));
		}
	}
	
	protected String getExpectedVersion() {
		return "2.0.1";
	}
}
