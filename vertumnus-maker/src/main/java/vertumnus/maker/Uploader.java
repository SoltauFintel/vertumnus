package vertumnus.maker;

import java.io.File;

import vertumnus.base.FTPUpload;
import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;

/**
 * The most important Vertumnus-Maker class.
 * 
 * <p>Upload packet and update directory
 */
public class Uploader {
	private FTPUpload ftp;
	private File fDirectory;
	private String module;
	private String version;
	private File file;
	private String path = "/";
	
	/**
	 * @param fileName directory file name incl. path
	 */
	public void setDirectory(String fileName) {
		fDirectory = new File(fileName);
		if (!fDirectory.exists()) {
			throw new RuntimeException("Directory file doesn't exist: " + fileName);
		}
	}
	
	public void setModule(String module) {
		if (module == null || module.trim().isEmpty()) {
			throw new IllegalArgumentException("Argument module must not be empty!");
		}
		this.module = module;
	}
	
	/**
	 * @param version String contains of 3 numbers which are separeted with a "."
	 * <br>1st part: major version, 2nd part: minor version, 3rd part: bugfix version.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setFile(String file) {
		this.file = new File(file);
		if (!this.file.exists()) {
			throw new RuntimeException("File not found: " + file);
		}
	}
	
	/**
	 * @param u already connected FTPUpload object
	 * @param path Upload path
	 */
	public void initFTP(FTPUpload u, String path) {
		if (!u.isConntected()) {
			throw new IllegalArgumentException("FTPUpload must be connected!");
		}
		ftp = u;
		this.path = path;
	}
	
	public void disconnect() {
		ftp.disconnect();
		ftp = null;
	}
	
	public void upload() {
		if (ftp == null || module == null || version == null || file == null || fDirectory == null) {
			throw new IllegalArgumentException("Not all parameters were set!");
		}
		updateDirectory();
		doUpload();
	}

	private void updateDirectory() {
		XMLDocument dok = new XMLDocument(fDirectory);
		XMLElement e = dok.getElement().add("e");
		e.setValue("module", module);
		e.setValue("version", version);
		e.setValue("url", getNewFilename());
		e.setValue("size", "" + file.length());
		dok.saveFile(fDirectory.getAbsolutePath());
	}

	private void doUpload() {
		ftp.cd(path);
		System.out.println("upload: " + file.getAbsolutePath() + " => " + getNewFilename());
		ftp.upload(file.getAbsolutePath(), getNewFilename(), true);
		System.out.println("  ... ok");
		ftp.upload(fDirectory.getAbsolutePath());
	}
	
	private String getNewFilename() {
		String dn = file.getName();
		if (!dn.contains(version)) {
			int o = dn.lastIndexOf(".");
			if (o >= 0) {
				dn = dn.substring(0, o) + "-" + version + dn.substring(o);
			} else {
				dn += "-" + version;
			}
		}
		return dn;
	}
}
