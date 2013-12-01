package vertumnus.base;

import java.io.*;
import java.net.URL;

public class FileService {

	private FileService() {
	}

	/**
	 * Datei from nach to kopieren
	 * @param from Dateiname inkl. Pfad, Datei muss vorhanden und zugreifbar sein
	 * @param to Dateiname inkl. Pfad
	 * @throws IOException
	 */
	public static void copyFile(String from, String to) throws IOException {
		FileInputStream in = new FileInputStream(from);
		FileOutputStream out = new FileOutputStream(to);
		byte[] buffer = new byte[4096];
		int bytes_read;
		while ((bytes_read = in.read(buffer)) != -1)
	        out.write(buffer, 0, bytes_read);
		out.close();
		in.close();
	}

	/**
	 * Ordner l�schen, der noch Dateien enth�lt
	 * @param dn zu l�schender Ordner
	 * @return true wenn Ordner existierte und gel�scht werden konnte
	 */
	public static boolean deleteFolder(String dn) {
		File dir = new File(dn);
		if (dir.exists() && dir.isDirectory()) {
			return deleteFolderR(dir);
		}
		return false;
	}
	
	private static boolean deleteFolderR(File folder) {
		for (File f : folder.listFiles()) {
			boolean ok;
			if (f.isDirectory()) {
				ok = deleteFolderR(f);
			} else {
				ok = f.delete();
			}
			if (!ok) {
				return false;
			}
		}
		return folder.delete();
	}
	
	/**
	 * @param fromurl source
	 * @param tofile target
	 * @param listener can be null
	 * @return true if download was complete and successfully
	 */
	public static boolean download(String fromurl, File tofile, DownloadInfo listener) {
		boolean delete = false;
		try {
			URL u = new URL(fromurl);
			InputStream input = new DataInputStream(u.openStream());
			try {
				OutputStream output = new FileOutputStream(tofile);
				try {
					long downloaded = 0;
					byte[] buffer = new byte[8192];
					int n = -1;
					while ((n = input.read(buffer)) != -1) {
						downloaded += n;
						output.write(buffer, 0, n);
						if (listener != null && listener.progress(downloaded)) {
							delete = true;
							return false; // abort download
						}
					}
				} finally {
					output.close();
					if (delete) {
						tofile.delete();
					}
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Fehler beim Download von " + fromurl, e);
		}
		return true;
	}
	
	public interface DownloadInfo {

		/**
		 * @param bytes already downloaded bytes
		 * @return default false,
		 * <br>true if download should be aborted
		 */
		boolean progress(long bytes);
	}
	
	/*  // PROXY           http://www.java2s.com/Tutorial/Java/0320__Network/ConnectthroughaProxy.htm
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import sun.misc.BASE64Encoder;

public class Main {
  public static void main(String[] argv) throws Exception {
    byte[] b = new byte[1];
    Properties systemSettings = System.getProperties();
    systemSettings.put("http.proxyHost", "proxy.mydomain.local");
    systemSettings.put("http.proxyPort", "80");

    URL u = new URL("http://www.google.com");
    HttpURLConnection con = (HttpURLConnection) u.openConnection();
    BASE64Encoder encoder = new BASE64Encoder();
    String encodedUserPwd = encoder.encode("mydomain\\MYUSER:MYPASSWORD".getBytes());
    con.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
    DataInputStream di = new DataInputStream(con.getInputStream());
    while (-1 != di.read(b, 0, 1)) {
      System.out.print(new String(b));
    }
  }
}
	 */
}
