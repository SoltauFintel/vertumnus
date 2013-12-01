package vertumnus.maker;

import org.junit.Test;

import vertumnus.base.FTPUpload;

public class TestFTPUpload {

	@Test
	public void upload() {
		FTPUpload u = FTPDaten.create();
		try {
			u.cd(FTPDaten.path);
			u.upload("preparation/testmodule-1.1.0.zip");
		} finally {
			u.disconnect();
		}
	}
}
