package vertumnus.maker;

import org.junit.Test;

import vertumnus.base.FTPUpload;

/**
 * Test for FTPUpload
 */
public class TestFTPUpload {

	@Test
	public void upload() {
		FTPUpload u = FTPData.create();
		try {
			u.cd(FTPData.path);
			u.upload("preparation/testmodule-1.1.0.zip");
		} finally {
			u.disconnect();
		}
	}
}
