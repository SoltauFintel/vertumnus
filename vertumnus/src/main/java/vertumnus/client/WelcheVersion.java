package vertumnus.client;

import java.io.File;

import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;

/**
 * What Version?
 */
public class WelcheVersion {
	private String modul;
	private XMLDocument dok;
	
	/**
	 * Setzt Verzeichnisdatei.
	 * @param dn Dateiname inkl. Pfad
	 */
	public void setVerzeichnis(String dn) {
		dok = new XMLDocument(new File(dn));
	}

	public void setModul(String modul) {
		this.modul = modul;
	}
	
	/**
	 * Liefert die neueste Version.
	 * @param istVersion
	 * @return Version (oder null wenn keine neuere Version gefunden wird)
	 */
	public String getNextMajorVersion(String istVersion) {
		String ret = null;
		for (XMLElement e : dok.getChildren()) {
			if (e.getValue("modul").equals(modul)) {
				String v = e.getValue("version");
				if (compare(v, istVersion) > 0 && (ret == null || compare(v, ret) > 0)) {
					ret = v;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Liefert die neueste Version innerhalb der gleichen Major-Version.
	 * @param istVersion
	 * @return Version (oder null wenn keine neuere Version gefunden wird)
	 */
	public String getNextMinorVersion(String istVersion) {
		String ret = null;
		String major = getMajorVersion(istVersion);
		for (XMLElement e : dok.getChildren()) {
			if (e.getValue("modul").equals(modul)) {
				String v = e.getValue("version");
				if (major.equals(getMajorVersion(v))) {
					if (compare(v, istVersion) > 0 && (ret == null || compare(v, ret) > 0)) {
						ret = v;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param v1 Versionsnummer, z.B. "18.4.2"
	 * @param v2 Versionsnummer, z.B. "18.0.0-beta"
	 * @return < 0 wenn v1 älter als v2, = 0 wenn gleich
	 */
	public static int compare(String v1, String v2) {
		return makeVersion(v1).compareTo(makeVersion(v2));
	}
	
	public static String makeVersion(String v) {
		StringBuilder ret = new StringBuilder();
		String rest = "~"; // hoher ASCII-Wert als Terminator damit "-beta" funktioniert
		int o = v.indexOf("-");
		if (o >= 0) {
			rest = v.substring(o);
			v = v.substring(0, o);
		}
		for (int i = 0; i < v.length(); i++) {
			char c = v.charAt(i);
			if ((c < '0' || c > '9') && c != '.') {
				throw new RuntimeException("Ungültige Zeichen in Version: " + v);
			}
		}
		String w[] = v.split("\\.");
		final String pad = "00000000";
		for (String i : w) {
			String j = pad + Integer.parseInt(i);
			ret.append(j.substring(j.length() - pad.length()) + ".");
		}
		ret.append(rest);
		return ret.toString();
	}
	
	public static String getMajorVersion(String v) {
		int o = v.indexOf(".");
		if (o >= 0) {
			return v.substring(0, o);
		} else {
			return v;
		}
	}

	public String getPaket(String version) {
		for (XMLElement e : dok.getChildren()) {
			if (e.getValue("modul").equals(modul)
					&& e.getValue("version").equals(version)) {
				return e.getValue("size") + " " + e.getValue("url");
			}
		}
		return null;
	}
}
