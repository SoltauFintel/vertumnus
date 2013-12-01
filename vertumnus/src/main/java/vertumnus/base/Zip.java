package vertumnus.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {

	private Zip() {
	}

	// http://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	public static void unzip(String archiv, String toDir) {
		try {
			byte[] buffer = new byte[8192];
			File folder = new File(toDir);
			ZipInputStream zis = new ZipInputStream(new FileInputStream(archiv));
			try {
				ZipEntry entry = zis.getNextEntry();
				while (entry != null) {
					String fileName = entry.getName();
					File newFile = new File(folder, fileName);
					if (entry.isDirectory()) {
						newFile.mkdirs();
					} else {
						FileOutputStream fos = new FileOutputStream(newFile);
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
					}
					zis.closeEntry();
					
					entry = zis.getNextEntry();
				}
			} finally {
				zis.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
