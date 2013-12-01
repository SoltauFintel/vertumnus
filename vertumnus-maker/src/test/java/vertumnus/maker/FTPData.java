package vertumnus.maker;

import java.io.File;

import vertumnus.base.FTPUpload;
import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;

/**
 * I don't want to save the FTP connection data in this Java project.
 * So I put these infos in /ftp.xml.
 */
public class FTPData {
	public static String path;

	private FTPData() {
	}

	public static FTPUpload create() {
		File ftpxml = new File("/ftp.xml"); // Maybe you need to modify this path.
		if (!ftpxml.exists()) {
			throw new RuntimeException("File '" + ftpxml.getAbsolutePath()
					+ "' not found! Please create this file. Shoud look something like this:"
					+ " <ftp p=\"...\" u=\"...\" s=\"...\" path=\"...\"/> See README.md for more details.");
		}
		XMLDocument doc = new XMLDocument(ftpxml);
		XMLElement e = doc.getElement();
		FTPUpload u = new FTPUpload();
		path = e.getValue("path");
		u.connect(
				e.getValue("s"), // FTP server name
				e.getValue("u"), // FTP user name
				e.getValue("p")); // FTP user password
		return u;
	}
}
