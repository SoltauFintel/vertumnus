package vertumnus.client;

import java.io.File;
import java.io.IOException;

import vertumnus.base.FileService;
import vertumnus.base.Zip;
import vertumnus.base.FileService.DownloadInfo;

/**
 * The most important Vertumnus class.
 */
public class Updater implements DownloadInfo {
	private boolean minorLine = false;
	private boolean bugfixLine = false;
	private String urlDirectory;
	private File directory;
	private WhatVersion wv;
	/** size + " " + url, or null */
	private String packet;
	private long size100 = -1;
	private long lastTime = 0;
	private File file;
	
	public void setDirectory(String url) {
		urlDirectory = url;
		directory = null;
	}

	/**
	 * @param true if updates are only permitted in the minor line,
	 * false (default) if the newest version should be loaded
	 */
	public void setKeepMajorVersion(boolean e) {
		minorLine = e;
		bugfixLine = false;
	}
	
	/**
	 * @param true if updates are only permitted in the bugfix line,
	 * false (default) if the newest version should be loaded
	 */
	public void setKeepMinorVersion(boolean e) {
		bugfixLine = e;
		minorLine = false;
	}

	/**
	 * This method loads a file from the internet.
	 * @param modul
	 * @param currentVersion
	 * @return next version or null if there is none
	 */
	public String getNextVersion(String modul, String currentVersion) {
		if (directory == null) {
			try {
				directory = File.createTempFile("vertumnus-verz-", ".tmp");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			directory.deleteOnExit();
			boolean ok;
			try {
				ok = FileService.download(urlDirectory, directory, this);
			} catch (Exception e) {
				ok = false;
			}
			if (!ok) {
				throw new RuntimeException("File doesn't exist or is not downloadable: " + urlDirectory);
			}
			wv = new WhatVersion();
			wv.setDirectory(directory.getAbsolutePath());
		}
		wv.setModule(modul);
		String version;
		if (bugfixLine) {
			version = wv.getNextBugfixVersion(currentVersion);
		} else if (minorLine) {
			version = wv.getNextMinorVersion(currentVersion);
		} else {
			version = wv.getNextMajorVersion(currentVersion);
		}
		packet = wv.getPacket(version);
		return version;
	}
	
	/**
	 * Load packet.
	 * <br>This method loads a file from the internet.
	 * @return file name incl. path of the downloaded packet
	 */
	public String load() {
		int o = packet.indexOf(" ");
		if (o < 0) {
			throw new RuntimeException("Format of packet wrong: '" + packet + "'!");
		}
		size100 = Long.parseLong(packet.substring(0, o));
		String url = packet.substring(o + 1);
		try {
			file = File.createTempFile("vertumnus-paket-", ".tmp");
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		boolean ok;
		try {
			ok = FileService.download(getHost() + url, file, this);
		} catch (Exception e) {
			ok = false;
		}
		if (!ok) {
			throw new RuntimeException(
					"File could not be downloaded: " + url);
		}
		return file.getAbsolutePath();
	}

	@Override
	public boolean progress(long bytes) {
		if (size100 > 0) {
			final long z = System.currentTimeMillis();
			long percent = bytes * 100L / size100;
			if (percent == 100) {
				size100 = -1;
				if (lastTime > 0) {
					System.out.println("    Download 100%");
					System.out.println();
				}
				lastTime = 0;
			} else if (percent > 4 && z - lastTime > 2400) {
				lastTime = z;
				System.out.println("    Download " + percent + "% ...");
			}
		}
		return false;
	}
	
	private String getHost() {
		int o = urlDirectory.lastIndexOf("/");
		if (o >= 0) {
			return urlDirectory.substring(0, o + 1);
		}
		String ret = urlDirectory;
		if (!ret.endsWith("/")) {
			ret += "/";
		}
		return ret;
	}
	
	/**
	 * Install packet
	 * @param toDir
	 */
	public void install(String toDir) {
		Zip.unzip(file.getAbsolutePath(), toDir);
	}
}
