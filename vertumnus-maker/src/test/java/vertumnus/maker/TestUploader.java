package vertumnus.maker;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Assert;
import org.junit.Test;

import vertumnus.base.FTPUpload;
import vertumnus.base.XMLDocument;

public class TestUploader {

	// TODO Check whether the test case makes sense.
	// TODO Method too long
	@Test
	public void uploader() {
		Uploader u = new Uploader();
		XMLDocument verz = new XMLDocument("<directory/>");
		String dnVerz = "temp/directory.xml";
		verz.saveFile(dnVerz);
		u.setDirectory(dnVerz);
		u.setModule("testmodule");
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
		// 1st check: directory file must be updated
		File fVerz = new File(dnVerz);
		XMLDocument dok = new XMLDocument(fVerz);
		Assert.assertEquals("Element is missing in directory\n", 1, dok.getChildren().size());
		Assert.assertEquals("Module name <>", "testmodule", dok.getChildren().get(0).getValue("modul"));
		Assert.assertEquals("Version <>", "1.1.0", dok.getChildren().get(0).getValue("version"));
		Assert.assertEquals("File <>", "1.1.0", dok.getChildren().get(0).getValue("version"));
		String soll = "testmodule-1.1.0.zip";
		Assert.assertEquals("URL <>", soll, dok.getChildren().get(0).getValue("url"));
		Assert.assertEquals("size <>", "" + file.length(), dok.getChildren().get(0).getValue("size"));
		// 2nd check: files must be uploaded
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
		Assert.assertTrue("Packet file not found on FTP server!", gef);
		Assert.assertTrue("Directory file not found on FTP server!", gefVerz);
	}
}
