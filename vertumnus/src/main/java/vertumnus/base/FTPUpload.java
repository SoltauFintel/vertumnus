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
	 * Connect to FTP server
	 * @param server
	 * @param login
	 * @param password
	 */
	public void connect(String server, String login, String password) {
		try {
			ftp = new FTPClient();
			ftp.connect(server);
			if (!ftp.login(login, password)) {
				throw new RuntimeException("Wrong password");
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
	 * Close FTP connection
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
	 * Change directory on FTP server
	 * @param dir
	 */
	public void cd(String dir) {
		path = dir.replace("\\", "/");
		if (!path.endsWith("/")) {
			path += "/";
		}
	}

	/**
	 * Upload file using FTP
	 * <p>File name is maintained. File will be overwritten if the file existed.
	 * @param source to be uploaded file, incl. path
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
	 * Upload file using FTP
	 * @param source to be uploaded file, incl. path
	 * @param target file name of the to be saved file, null if source file name shall be maintained
	 * @param overwrite true overwrite, false throw exception if file exist 
	 */
	public void upload(String source, String target, boolean overwrite) {
		if (!overwrite) {
			throw new UnsupportedOperationException("overwrite option false is not supported");
		}
		if (target == null) {
			throw new UnsupportedOperationException("target == null is not supported");
		}
		try {
			InputStream stream = new FileInputStream(source);
			try {
				if (!ftp.storeFile(path + target, stream)) {
					throw new RuntimeException("Upload error: " + source + " => " + path + target);
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
