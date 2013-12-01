package vertumnus.maker;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.Test;

import vertumnus.base.FTPUpload;
import vertumnus.base.XMLDocument;

public class TestUploader {

	// TODO Prüfuen, ob der Testcase Sinn macht.
	// TODO Methode zu lang.
	@Test
	public void uploader() {
		Uploader u = new Uploader();
		XMLDocument verz = new XMLDocument("<directory/>");
		String dnVerz = "temp/directory.xml";
		verz.saveFile(dnVerz);
		u.setVerzeichnis(dnVerz);
		u.setModul("testmodule");
		u.setVersion("1.1.0");
		String dn = "preparation/testmodule.zip";
		File file = new File(dn);
		u.setFile(dn);
		u.initFTP(FTPData.create(), FTPData.path);
		try {
			u.upload();
		} finally {
			u.disconnect();
		}
		// Prüfung 1: Verzeichnisdatei muss fortgeschrieben sein
		File fVerz = new File(dnVerz);
		XMLDocument dok = new XMLDocument(fVerz);
		Assert.assertEquals("Element fehlt im Verzeichnis\n", 1, dok.getChildren().size());
		Assert.assertEquals("Modulname <>", "testmodule", dok.getChildren().get(0).getValue("modul"));
		Assert.assertEquals("Version <>", "1.1.0", dok.getChildren().get(0).getValue("version"));
		Assert.assertEquals("File <>", "1.1.0", dok.getChildren().get(0).getValue("version"));
		String soll = "testmodule-1.1.0.zip";
		Assert.assertEquals("URL <>", soll, dok.getChildren().get(0).getValue("url"));
		Assert.assertEquals("size <>", "" + file.length(), dok.getChildren().get(0).getValue("size"));
		// Prüfung 2: Dateien müssen hochgeladen sein
		FTPUpload ftp = FTPData.create();
		FTPFile[] files;
		try {
			ftp.cd(FTPData.path);
			files = ftp.listFiles();
		} finally {
			ftp.disconnect();
		}
		boolean gef = false;
		boolean gefVerz = false;
		for (FTPFile i : files) {
			if (i.getName().equals(soll)) {
				gef = true;
			}
			if (i.getName().equals(new File(dnVerz).getName())) {
				gefVerz = true;
			}
		}
		Assert.assertTrue("Paket-Datei auf FTP Server nicht gefunden!", gef);
		Assert.assertTrue("Verzeichnisdatei auf FTP Server nicht gefunden!", gefVerz);
	}
}
