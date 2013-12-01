package vertumnus.maker;

import java.io.File;

import vertumnus.base.FTPUpload;
import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;

/**
 * The most important Vertumnus-Maker class.
 * 
 * <p>Paket hochladen und Verzeichnis fortschreiben
 */
public class Uploader {
	private FTPUpload ftp;
	private File fVerz;
	private String modul;
	private String version;
	private File file;
	private String path = "/";
	
	/**
	 * @param dn Verzeichnisdatei Dateiname, inkl. Pfad 
	 */
	public void setVerzeichnis(String dn) {
		fVerz = new File(dn);
		if (!fVerz.exists()) {
			throw new RuntimeException("Verzeichnisdatei nicht vorhanden: " + dn);
		}
	}
	
	public void setModul(String modul) {
		if (modul == null || modul.trim().isEmpty()) {
			throw new IllegalArgumentException("Argument modul darf nicht leer sein");
		}
		this.modul = modul;
	}
	
	/**
	 * @param version String bestehend aus 3 Zahlen, die mit "." getrennt sind.
	 * <br>erste Stelle: Major Version, zweite Stelle: Minor Version, dritte Stelle: Bugfix Version.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setFile(String file) {
		this.file = new File(file);
		if (!this.file.exists()) {
			throw new RuntimeException("Datei nicht vorhanden: " + file);
		}
	}
	
	/**
	 * @param u already connected FTPUpload object
	 * @param path Upload path
	 */
	public void initFTP(FTPUpload u, String path) {
		if (!u.isConntected()) {
			throw new IllegalArgumentException("FTPUpload muss connected sein!");
		}
		ftp = u;
		this.path = path;
	}
	
	public void disconnect() {
		ftp.disconnect();
		ftp = null;
	}
	
	public void upload() {
		if (ftp == null || modul == null || version == null || file == null || fVerz == null) {
			throw new IllegalArgumentException("Es wurden nicht alle Parameter gesetzt!");
		}
		fortschreiben();
		doUpload();
	}

	private void fortschreiben() {
		XMLDocument dok = new XMLDocument(fVerz);
		XMLElement e = dok.getElement().add("e");
		e.setValue("modul", modul);
		e.setValue("version", version);
		e.setValue("url", getNewFilename());
		e.setValue("size", "" + file.length());
		dok.saveFile(fVerz.getAbsolutePath());
	}

	private void doUpload() {
		ftp.cd(path);
		System.out.println("upload: " + file.getAbsolutePath() + " => " + getNewFilename());
		ftp.upload(file.getAbsolutePath(), getNewFilename(), true);
		System.out.println("  ... ok");
		ftp.upload(fVerz.getAbsolutePath());
	}
	
	private String getNewFilename() {
		String dn = file.getName();
		if (!dn.contains(version)) {
			int o = dn.lastIndexOf(".");
			if (o >= 0) {
				dn = dn.substring(0, o) + "-" + version + dn.substring(o);
			} else {
				dn += "-" + version;
			}
		}
		return dn;
	}
}
