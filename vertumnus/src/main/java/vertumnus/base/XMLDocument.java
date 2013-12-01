package vertumnus.base;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * XML Dokument
 * <br>XML Kapselung für vereinfachten DOM-basierten XML-Zugriff
 * 
 * @author  Marcus Warm
 * @version 1.8 20.12.2008
 */
public class XMLDocument implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Document dok;
	
	/**
	 * Standard-Konstruktor
	 * <br>Dokument wird nicht initialisiert
	 */
	public XMLDocument() {
	}

	/**
	 * Konstruktor XML String
	 * @param xml XML Dokument in String-Form
	 */
	public XMLDocument(String xml) {
		if (xml == null) {
			throw new IllegalArgumentException("XMLDocument-Argument xml darf nicht null sein!");
		}
		try {
			dok = DocumentHelper.parseText(xml);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Konstruktor XML Datei laden
	 * @param pIsResource true wenn Ressourcendatei im Package, false wenn Datei im Dateisystem
	 * @param pFilename   Dateiname inkl. Pfad.<br>
	 *                    Dateiname kann auch mit file: oder http: beginnen. In diesem Fall wird pIsResource ignoriert.
	 */
	public XMLDocument(boolean pIsResource, String pFilename) {
		if (pFilename.startsWith("file:") || pFilename.startsWith("http:")) {
			try {
				URL url = new URL(pFilename);
				loadStream(url.openConnection().getInputStream());
			} catch (Throwable e) {
				throw new RuntimeException("XML-Datei '" + pFilename + "' kann nicht geladen werden!", e);
			}
		} else if(pIsResource) {
			loadResource(pFilename);
		} else {
			loadFile(pFilename);
		}
	}

	/**
	 * Konstruktor XML Datei laden
	 * @param file Datei aus Dateisystem
	 */
	public XMLDocument(File file) {
		this(false, file.getPath());
	}
	
	/**
	 * Konstruktor XML Datei laden
	 * @param pStream InputStream
	 */
	public XMLDocument(InputStream pStream) {
		loadStream(pStream);
	}

	/**
	 * Konstruktor XML Datei laden
	 * @param pClass        Class um Packagepfad zu ermitteln 
	 * @param pResourcename Dateiname der Ressource ohne Pfad
	 */
	public XMLDocument(Class<?> pClass, String pResourcename) {
		final char slash = '/';
		String pfad = pClass.getPackage().getName().replace('.', slash);
		loadResource(slash + pfad + slash + pResourcename);
	}

	/**
	 * load file
	 * @param pDateiname
	 * @return XMLDocument
	 */
	public static XMLDocument load(String pDateiname) {
		XMLDocument ret = new XMLDocument();
		ret.loadFile(pDateiname);
		return ret;
	}

	/**
	 * load file
	 * @param pDateiname
	 * @return XML String
	 */
	public static String loadXML(String pDateiname) {
		XMLDocument ret = new XMLDocument();
		ret.loadFile(pDateiname);
		return ret.getXML();
	}

	/**
	 * XML Datei laden
	 * @param pDateiname
	 */
	public void loadFile(String pDateiname) {
		SAXReader r = new SAXReader();
		try {
			dok = r.read(pDateiname);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * XML Datei laden
	 * @param pRessourcenname
	 */
	public void loadResource(String pRessourcenname) {
		InputStream stream = getClass().getResourceAsStream(pRessourcenname);
		if (stream == null) {
			throw new RuntimeException("Ressourcendatei '" + pRessourcenname + "' kann nicht geladen werden!");
		}
		loadStream(stream);
	}
	
	/**
	 * XML Datei laden
	 * @param pStream InputStream
	 */
	public void loadStream(InputStream pStream) {
		SAXReader r = new SAXReader();
		try {
			dok = r.read(pStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * XML Dokument im PrettyPrint Format in Datei speichern
	 * @param pDateiname
	 */
	public void saveFile(String pDateiname) {
		saveFile(pDateiname, OutputFormat.createPrettyPrint());
	}

	/**
	 * XML Dokument im Compact Format in Datei speichern
	 * @param pDateiname
	 */
	public void saveFileCompact(String pDateiname) {
		saveFile(pDateiname, OutputFormat.createCompactFormat());
	}
	
	protected void saveFile(String pDateiname, OutputFormat pFormat) {
		pFormat.setEncoding("windows-1252");
        try {
			XMLWriter writer = new XMLWriter(
					new FileWriter(pDateiname), 
					pFormat);
			writer.write(dok);
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return true wenn Dokument gültig und das root-Element Kind-Elemente besitzt
	 */
	public boolean isOkay() {
		return dok!=null
			&& dok.getRootElement()!=null
			&& dok.getRootElement().elements()!=null
			&& dok.getRootElement().elements().size() > 0;
	}
	
	/**
	 * @return root Element
	 */
	public XMLElement getElement() {
		return dok==null ? null : XMLElementImpl.create(dok.getRootElement());
	}
	
	/**
	 * Schnellzugriff auf Kind-Elemente vom Dokument aus
	 * @return Kind-Elemente vom root-Element
	 */
	public List<XMLElement> getChildren() {
		return XMLElementImpl.getChildElements(dok.getRootElement().elements());
	}
	
	/**
	 * selectNodes Zugriff auf Dokumentebene <br>
	 * Es wird eine Exception ausgelöst wenn die XPath-Anweisung fehlerhaft ist.
	 * @param pXPath XPath String
	 * @return XMLElement Liste
	 */
	public List<XMLElement> selectNodes(String pXPath) {
		return XMLElementImpl.getChildElements(dok.selectNodes(pXPath));
	}
	
	/**
	 * selectSingleNode Zugriff auf Dokumentebene <br>
	 * Wenn die XPath-Anweisung fehlerhaft ist wird eine Exception ausgelöst.
	 * @param pXPath XPath String. Der XPath String muß so aufgebaut sein,
	 * 			                   daß nur ein Element zurückgegeben wird.
	 * @return XMLElement oder null wenn Element nicht gefunden
	 */
	public XMLElement selectSingleNode(String pXPath) {
		Node node = dok.selectSingleNode(pXPath);
		if(node==null) {
			return null;
		} else {
			return XMLElementImpl.create((Element) node);
		}
	}
	
	/**
	 * Annahme ist, dass es im Dokument genau ein Element mit der id gibt.
	 * @param id
	 * @return XMLElement anhand Attribut id liefern
	 */
	public XMLElement byId(String id) {
		return selectSingleNode("//*[@id='" + id + "']");
	}
	
	/**
	 * Element mit Attribut "id" = id löschen.
	 * Element kann nicht das root Element sein. Es muss aber auch nicht
	 * das direkte Kindelement des root Elements sein.
	 * @param id Inhalt des Attributs "id"
	 * @return true wenn child gelöscht worden ist,
	 * false wenn child nicht gefunden worden ist
	 */
	public boolean removeChildById(final String id) {
		final String x = "*[@id='" + id + "']";
		XMLElement p = selectSingleNode("//" + x + "/..");
		if (p != null) {
			p.removeChildren(x);
		}
		return p != null;
	}

	/**
	 * @return XML String
	 */
	public String getXML() {
		return dok.asXML();
	}
}
