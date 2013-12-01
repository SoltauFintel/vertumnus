package vertumnus.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPUpload {
	private FTPClient ftp;
	private String path = "/";
	private boolean conntected = false;
	
	/**
	 * Mit FTP Server verbinden
	 * @param server
	 * @param login
	 * @param password
	 */
	public void connect(String server, String login, String password) {
		try {
			ftp = new FTPClient();
			ftp.connect(server);
			if (!ftp.login(login, password)) {
				throw new RuntimeException("Passwort falsch");
			}
			conntected = true;
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			ftp = null;
			throw new RuntimeException(e);
		}
	}
	
	public boolean isConntected() {
		return conntected;
	}
	
	/**
	 * FTP Verbindung schlieﬂen
	 */
	public void disconnect() {
		try {
			ftp.logout();
			ftp.disconnect();
			conntected = false;
			ftp = null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Verzeichnis auf FTP Server wechseln
	 * @param dir
	 */
	public void cd(String dir) {
		path = dir.replace("\\", "/");
		if (!path.endsWith("/")) {
			path += "/";
		}
	}

	/**
	 * Datei per FTP hochladen
	 * <p>Dateiname wird beibehalten. Datei wird ggf. ¸berschrieben.
	 * @param source hochzuladende Datei, inkl. Pfad
	 */
	public void upload(final String pSource) {
		String source = pSource.replace("\\", "/");
		int o = source.lastIndexOf("/");
		String target = source;
		if (o >= 0) {
			target = target.substring(o + 1);
		}
		upload(source, target, true);
	}

	/**
	 * Datei per FTP hochladen
	 * @param source hochzuladende Datei, inkl. Pfad
	 * @param target Dateiname der abzulegenden Datei, null wenn source Dateiname beibehalten werden soll
	 * @param overwrite true ¸berschreiben, false Exception werfen falls Datei vorhanden 
	 */
	public void upload(String source, String target, boolean overwrite) {
		if (!overwrite) {
			throw new UnsupportedOperationException("overwrite Option false wird noch nicht unterst¸tzt");
		}
		if (target == null) {
			throw new UnsupportedOperationException("target == null wird noch nicht unterst¸tzt");
		}
		try {
			InputStream stream = new FileInputStream(source);
			try {
				if (!ftp.storeFile(path + target, stream)) {
					throw new RuntimeException("Upload Fehler: " + source + " => " + path + target);
				}
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public FTPFile[] listFiles() {
		try {
			return ftp.listFiles(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
