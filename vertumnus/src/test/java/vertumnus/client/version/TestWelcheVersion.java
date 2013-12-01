package vertumnus.client.version;

import org.junit.Assert;
import org.junit.Test;

import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;
import vertumnus.client.WelcheVersion;

public class TestWelcheVersion {

	@Test
	public void minor1() {
		minor("18.0.0", "18.2.0");
	}

	@Test
	public void minor_null() {
		minor("18.2.0", null);
	}

	@Test
	public void major1() {
		major("18.0.0", "20.0.1");
	}

	@Test
	public void major2() {
		major("18.2.0", "20.0.1");
	}

	@Test
	public void major3() {
		major("19.0.7", "20.0.1");
	}

	@Test
	public void major4() {
		major("20.0.0", "20.0.1");
	}

	@Test
	public void minor4() {
		minor("20.0.0", "20.0.1");
	}

	@Test
	public void major_null() {
		major("20.0.1", null);
	}
	
	@Test
	public void twoModules_minor() {
		WelcheVersion v = getWelcheVersionR();
		String akt = "19.0.0";
		v.setModul("Testmodul");
		String m1 = v.getNextMinorVersion(akt);
		v.setModul("Libmodul");
		String m2 = v.getNextMinorVersion(akt);
		Assert.assertEquals("19.0.7", m1);
		Assert.assertNull("Test 2 <>", m2);
	}
	
	@Test
	public void twoModules_major() {
		WelcheVersion v = getWelcheVersionR();
		String akt = "19.0.0";
		v.setModul("Testmodul");
		String m1 = v.getNextMajorVersion(akt);
		v.setModul("Libmodul");
		String m2 = v.getNextMajorVersion(akt);
		Assert.assertEquals("20.0.1", m1);
		Assert.assertEquals("Test 2 <>", "20.0.0", m2);
	}

	private void major(String version, String soll) {
		WelcheVersion v = getWelcheVersion();
		String ist = v.getNextMajorVersion(version);
		Assert.assertEquals(
				"Version falsch (Major-Linie)\nVersion: " + version, soll, ist);
	}

	private void minor(String version, String soll) {
		WelcheVersion v = getWelcheVersion();
		String ist = v.getNextMinorVersion(version);
		Assert.assertEquals(
				"Version falsch (Minor-Linie)\nVersion: " + version, soll, ist);
	}

	private WelcheVersion getWelcheVersion() {
		WelcheVersion v = getWelcheVersionR();
		v.setModul("Testmodul");
		return v;
	}
	
	private WelcheVersion getWelcheVersionR() {
		XMLDocument dok = new XMLDocument("<directory/>");
		String dn = "temp/directory.xml";
		XMLElement e;

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "18.0.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Libmodul");
		e.setValue("version", "18.0.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "17.6.4");

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "18.2.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Libmodul");
		e.setValue("version", "18.2.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "19.0.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "19.0.7");

		e = dok.getElement().add("e");
		e.setValue("modul", "Libmodul");
		e.setValue("version", "20.0.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "20.0.0");

		e = dok.getElement().add("e");
		e.setValue("modul", "Testmodul");
		e.setValue("version", "20.0.1");

		dok.saveFile(dn);
		WelcheVersion v = new WelcheVersion();
		v.setVerzeichnis(dn);
		return v;
	}
}
