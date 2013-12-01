package vertumnus.client;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import vertumnus.Vertumnus;

public class TestUpdater_CLI extends TestUpdater {

	@Test
	public void stage1() {
		updater("1", "", null);
		Assert.assertEquals("Nächste Version falsch!\n", "NV="
				+ getExpectedVersion() + ";", Vertumnus.getResult());
	}

	private void updater(String stages, String installFolder, String istVersion) {
		Vertumnus.debug = true;
		Vertumnus.main(new String[] { "-d", VERZEICHNIS, "-s", stages, "-m",
				MODULNAME, "-v", istVersion == null ? IST_VERSION : istVersion, "-l", getLinie(), "-i",
				installFolder });
	}

	protected String getLinie() {
		return "major";
	}

	@Override
	public void stage1_esGibtKeinUpdate() {
		updater("1", "", IST_VERSION_NEU);
		Assert.assertEquals("Nächste Version falsch!\n", "exit=-2;", Vertumnus.getResult());
	}

	@Test
	public void stage2() {
		updater("12", "", null);
		Assert.assertFalse("Rückgabe ist leer!", Vertumnus.getResult().isEmpty());
		int o = Vertumnus.getResult().indexOf("dn=");
		Assert.assertTrue("'dn=' nicht gefunden:\r\n" + Vertumnus.getResult(), o > 0);
		String dn = Vertumnus.getResult().substring(o + 3);
		dn = dn.substring(0, dn.length() - 1);
		Assert.assertTrue("Datei wurde nicht heruntergeladen o.ä.", new File(dn).exists());
	}

	@Test
	public void stage3() {
		updater("123", "temp", null);
		Assert.assertTrue(Vertumnus.getResult().contains("installed;"));
	}

	@Test
	public void stage3_emptyMode() {
		Vertumnus.debug = true;
		Vertumnus.main(new String[] { "-d", VERZEICHNIS, "-m", MODULNAME,
				"-v", IST_VERSION, "-l", getLinie(), "-i", "temp"});
		Assert.assertTrue(Vertumnus.getResult().contains("installed;"));
	}
	
	@Override
	public void verzeichnisNichtVorhanden() {
		Vertumnus.debug = true;
		Vertumnus.main(new String[] { "-d",
				"http://mwvb.de/vertumnus/verzeichnis-nicht-da.xml", "-m",
				MODULNAME, "-v", IST_VERSION, "-l", getLinie(), "-i", "temp",
				"-s", "1" });
	}
}
