package vertumnus.client;

import java.io.File;

import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;

/**
 * What Version?
 */
public class WhatVersion {
	private String module;
	private XMLDocument doc;
	
	/**
	 * Sets directory file.
	 * @param fileName file name incl. path
	 */
	public void setVerzeichnis(String fileName) {
		doc = new XMLDocument(new File(fileName));
	}

	public void setModule(String module) {
		this.module = module;
	}
	
	/**
	 * Returns newest version.
	 * @param currentVersion
	 * @return version (or null if there is no newer version)
	 */
	public String getNextMajorVersion(String currentVersion) {
		String ret = null;
		for (XMLElement e : doc.getChildren()) {
			if (e.getValue("modul").equals(module)) {
				String v = e.getValue("version");
				if (compare(v, currentVersion) > 0 && (ret == null || compare(v, ret) > 0)) {
					ret = v;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Returns the newest version within the same major version line.
	 * @param currentVersion
	 * @return version (or null if there is no newer version)
	 */
	public String getNextMinorVersion(String currentVersion) {
		String ret = null;
		String major = getMajorVersion(currentVersion);
		for (XMLElement e : doc.getChildren()) {
			if (e.getValue("modul").equals(module)) {
				String v = e.getValue("version");
				if (major.equals(getMajorVersion(v))) {
					if (compare(v, currentVersion) > 0 && (ret == null || compare(v, ret) > 0)) {
						ret = v;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param v1 Version number, e.g. "18.4.2"
	 * @param v2 Version number, e.g. "18.0.0-beta"
	 * @return < 0 if v1 is older than v2, = 0 if equal
	 */
	public static int compare(String v1, String v2) {
		return makeVersion(v1).compareTo(makeVersion(v2));
	}
	
	public static String makeVersion(String v) {
		StringBuilder ret = new StringBuilder();
		String rest = "~"; // big ASCII value als terminator so that "-beta" works
		int o = v.indexOf("-");
		if (o >= 0) {
			rest = v.substring(o);
			v = v.substring(0, o);
		}
		for (int i = 0; i < v.length(); i++) {
			char c = v.charAt(i);
			if ((c < '0' || c > '9') && c != '.') {
				throw new RuntimeException("Invalid character in version: " + v);
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
		return o >= 0 ? v.substring(0, o) : v;
	}

	public String getPacket(String version) {
		for (XMLElement e : doc.getChildren()) {
			if (e.getValue("modul").equals(module)
					&& e.getValue("version").equals(version)) {
				return e.getValue("size") + " " + e.getValue("url");
			}
		}
		return null;
	}
}
