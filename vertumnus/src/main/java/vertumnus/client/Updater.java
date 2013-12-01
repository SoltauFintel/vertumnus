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
	private boolean minorLinie = false;
	private String urlVerz;
	private File verz;
	private WelcheVersion wv;
	/** size + " " + url, oder null */
	private String paket;
	private long size100 = -1;
	private long lastTime = 0;
	private File datei;
	
	public void setVerzeichnis(String url) {
		urlVerz = url;
		verz = null;
	}

	/**
	 * @param true wenn Updates nur in der Minor Linie erlaubt sind,
	 * false (default) wenn die neueste Version geladen werden soll
	 */
	public void setKeepMajorVersion(boolean e) {
		minorLinie = e;
	}

	/**
	 * Diese Methode lädt eine Datei aus dem Internet.
	 * @param modul
	 * @param istVersion
	 * @return nächste Version oder null wenn es keine gibt
	 */
	public String getNextVersion(String modul, String istVersion) {
		if (verz == null) {
			try {
				verz = File.createTempFile("vertumnus-verz-", ".tmp");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			verz.deleteOnExit();
			boolean ok;
			try {
				ok = FileService.download(urlVerz, verz, this);
			} catch (Exception e) {
				ok = false;
			}
			if (!ok) {
				throw new RuntimeException("Datei nicht vorhanden oder herunterladbar: " + urlVerz);
			}
			wv = new WelcheVersion();
			wv.setVerzeichnis(verz.getAbsolutePath());
		}
		wv.setModul(modul);
		String version;
		if (minorLinie) {
			version = wv.getNextMinorVersion(istVersion);
		} else {
			version = wv.getNextMajorVersion(istVersion);
		}
		paket = wv.getPaket(version);
		return version;
	}
	
	/**
	 * Paket laden. 
	 * Diese Methode lädt eine Datei aus dem Internet.
	 * @return Dateiname inkl. Pfad des heruntergeladenen Pakets
	 */
	public String load() {
		int o = paket.indexOf(" ");
		if (o < 0) {
			throw new RuntimeException("Format von paket falsch: '" + paket + "'!");
		}
		size100 = Long.parseLong(paket.substring(0, o));
		String url = paket.substring(o + 1);
		try {
			datei = File.createTempFile("vertumnus-paket-", ".tmp");
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		boolean ok;
		try {
			ok = FileService.download(getHost() + url, datei, this);
		} catch (Exception e) {
			ok = false;
		}
		if (!ok) {
			throw new RuntimeException(
					"Datei konnte nicht herunter geladen werden: " + url);
		}
		return datei.getAbsolutePath();
	}

	@Override
	public boolean progress(long bytes) {
		if (size100 > 0) {
			final long z = System.currentTimeMillis();
			long prozent = bytes * 100L / size100;
			if (prozent == 100) {
				size100 = -1;
				if (lastTime > 0) {
					System.out.println("    Download 100%");
					System.out.println();
				}
				lastTime = 0;
			} else if (prozent > 4 && z - lastTime > 2400) {
				lastTime = z;
				System.out.println("    Download " + prozent + "% ...");
			}
		}
		return false;
	}
	
	private String getHost() {
		int o = urlVerz.lastIndexOf("/");
		if (o >= 0) {
			return urlVerz.substring(0, o + 1);
		}
		String ret = urlVerz;
		if (!ret.endsWith("/")) {
			ret += "/";
		}
		return ret;
	}
	
	/**
	 * Paket installieren
	 * @param toDir
	 */
	public void install(String toDir) {
		Zip.unzip(datei.getAbsolutePath(), toDir);
	}
}
