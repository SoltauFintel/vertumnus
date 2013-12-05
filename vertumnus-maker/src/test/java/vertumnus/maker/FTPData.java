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
					+ "' not found! Please create this file. Should look something like this:"
					+ " <ftp server=\"...\" username=\"...\" password=\"...\" path=\"/...\"/>");
		}
		XMLDocument doc = new XMLDocument(ftpxml);
		XMLElement e = doc.getElement();
		path = e.getValue("path");       // e.g. "/webpages/myserver.com/myapp/liveupdate"
		FTPUpload u = new FTPUpload();
		u.connect(
				e.getValue("server"),    // e.g. "myserver.com"
				e.getValue("username"),  // e.g. "joe"
				e.getValue("password")); // e.g. "secret"
		return u;
	}
}
