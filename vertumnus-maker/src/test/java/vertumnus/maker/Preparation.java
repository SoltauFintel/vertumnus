package vertumnus.maker;

import vertumnus.base.XMLDocument;

public class Preparation {
	
	@org.junit.Test // only for first unique preparation
	public void preparation() {
		String filenameDirectory = "preparation/directory.xml";
		XMLDocument empty = new XMLDocument("<directory/>");
		empty.saveFile(filenameDirectory);
		Uploader u = new Uploader();
		u.setDirectory(filenameDirectory);
		u.initFTP(FTPData.create(), FTPData.path);
		u.setModule("testmodule");

		u.setVersion("1.0.4");
		u.setFile("preparation/testmodule-1.0.4.zip");
		u.upload();
		
		u.setVersion("1.1.0");
		u.setFile("preparation/testmodule-1.1.0.zip");
		u.upload();
		
		u.setVersion("2.0.1");
		u.setFile("preparation/testmodule-2.0.1.zip");
		u.upload();
		
		u.disconnect();
	}
}
