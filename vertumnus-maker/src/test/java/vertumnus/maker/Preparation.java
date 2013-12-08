package vertumnus.maker;

import vertumnus.base.XMLDocument;

public class Preparation {
	
	@org.junit.Test // only for first unique preparation
	public void preparation() {
		String dnVerz = "preparation/directory.xml";
		XMLDocument leer = new XMLDocument("<directory/>");
		leer.saveFile(dnVerz);
		Uploader u = new Uploader();
		u.setDirectory(dnVerz);
		u.initFTP(FTPData.create(), FTPData.path);
		u.setModule("testmodule");

		u.setVersion("1.1.0");
		u.setFile("preparation/testmodule-1.1.0.zip");
		u.upload();
		
		u.setVersion("2.0.1");
		u.setFile("preparation/testmodule-2.0.1.zip");
		u.upload();
		
		u.disconnect();
	}
}
