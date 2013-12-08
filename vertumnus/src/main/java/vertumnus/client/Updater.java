package vertumnus.client;

import java.io.File;
import java.io.IOException;

import vertumnus.Vertumnus;
import vertumnus.base.FileService;
import vertumnus.base.FileService.DownloadInfo;
import vertumnus.base.Zip;

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
	private ProgressGUI gui = null;
	private String info = "";
	private String title = "Vertumnus " + Vertumnus.VERSION + " - Live Update";
	
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
	 * @param withGUI false (default) no GUI is showed, true to show progress in extra window
	 */
	public void setGUI(boolean withGUI) {
		if (withGUI) {
			gui = new ProgressGUI();
		} else {
			gui = null;
		}
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
			download(urlDirectory, directory);
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
		info = "Download Version " + version;
		return version;
	}
	
	/**
	 * Sets title of download window.
	 * @param title The default is "Vertumnus {version} - Live Update".
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets label of download window. If you want to modify the info call this method before calling load().
	 * @param text
	 */
	public void setInfo(String text) {
		info = text;
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
		download(getHost() + url, file);
		return file.getAbsolutePath();
	}
	
	private void download(String fromURL, File toFile) {
		if (size100 > 0 && gui != null) {
			gui.setTitle(title);
			gui.setInfo(info);
			gui.show();
		}
		try {
			boolean ok;
			try {
				ok = FileService.download(fromURL, toFile, this);
			} catch (Exception e) {
				ok = false;
			}
			if (!ok) {
				throw new RuntimeException("File could not be downloaded: " + fromURL);
			}
		} finally {
			if (size100 > 0 && gui != null) {
				gui.hide();
				gui = null;
			}
		}
	}

	@Override
	public boolean progress(long bytes) {
		if (size100 > 0) {
			final long z = System.currentTimeMillis();
			int percent = (int) (bytes * 100L / size100);
			if (percent == 100) {
				size100 = -1;
				if (lastTime > 0) {
					if (gui == null) {
						System.out.println("    Download 100%");
						System.out.println();
					} else {
						gui.setPercent(100);
					}
				}
				lastTime = 0;
			} else if (gui != null) {
				gui.setPercent(percent);
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
