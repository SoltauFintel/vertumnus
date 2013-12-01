package vertumnus.maker;

import java.io.File;

import vertumnus.base.FTPUpload;
import vertumnus.base.XMLDocument;
import vertumnus.base.XMLElement;

public class FTPDaten {
	public static String path;

	private FTPDaten() {
	}

	public static FTPUpload create() {
		XMLDocument dok = new XMLDocument(new File("/ftp.xml"));
		XMLElement e = dok.getElement();
		FTPUpload u = new FTPUpload();
		path = e.getValue("path");
		u.connect(e.getValue("s"), e.getValue("u"), e.getValue("p"));
		return u;
	}
}
