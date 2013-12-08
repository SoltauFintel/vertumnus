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
 * XML document
 * <br>XML encapsulation for easy DOM-based XML access
 * 
 * @author  Marcus Warm
 * @version 20.12.2008
 */
public class XMLDocument implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Document doc;
	
	/**
	 * Default constructor
	 * <br>Document will not be initialized.
	 */
	public XMLDocument() {
	}

	/**
	 * XML String constructor
	 * @param xml XML document in String form
	 */
	public XMLDocument(String xml) {
		if (xml == null) {
			throw new IllegalArgumentException("XMLDocument argument xml must not be null!");
		}
		try {
			doc = DocumentHelper.parseText(xml);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Load XML file constructor
	 * @param pIsResource true if resource file is in package, false if file is in file system
	 * @param pFilename   file name incl. path.<br>
	 *                    File name can start with file: or http:. In this case pIsResource will be ignored.
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
	 * Load XML file constructor
	 * @param file File from file system
	 */
	public XMLDocument(File file) {
		this(false, file.getPath());
	}
	
	/**
	 * Load XML file constructor 
	 * @param pStream InputStream
	 */
	public XMLDocument(InputStream pStream) {
		loadStream(pStream);
	}

	/**
	 * Load XML file constructor
	 * @param pClass        Class for package path detection 
	 * @param pResourceName File name or resource without path
	 */
	public XMLDocument(Class<?> pClass, String pResourceName) {
		final char slash = '/';
		String pfad = pClass.getPackage().getName().replace('.', slash);
		loadResource(slash + pfad + slash + pResourceName);
	}

	/**
	 * Load XML file
	 * @param pFileName
	 * @return XMLDocument
	 */
	public static XMLDocument load(String pFileName) {
		XMLDocument ret = new XMLDocument();
		ret.loadFile(pFileName);
		return ret;
	}

	/**
	 * Load XML file
	 * @param pFileName
	 * @return XML String
	 */
	public static String loadXML(String pFileName) {
		XMLDocument ret = new XMLDocument();
		ret.loadFile(pFileName);
		return ret.getXML();
	}

	/**
	 * Load XML file
	 * @param pFileName
	 */
	public void loadFile(String pFileName) {
		SAXReader r = new SAXReader();
		try {
			doc = r.read(pFileName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Load XML file
	 * @param pResourceName
	 */
	public void loadResource(String pResourceName) {
		InputStream stream = getClass().getResourceAsStream(pResourceName);
		if (stream == null) {
			throw new RuntimeException("Resource file '" + pResourceName + "' cannot be loaded!");
		}
		loadStream(stream);
	}
	
	/**
	 * Load XML file
	 * @param pStream InputStream
	 */
	public void loadStream(InputStream pStream) {
		SAXReader r = new SAXReader();
		try {
			doc = r.read(pStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Save XML document in file with pretty print format
	 * @param pFileName
	 */
	public void saveFile(String pFileName) {
		saveFile(pFileName, OutputFormat.createPrettyPrint());
	}

	/**
	 * Save XML document in file with compact format
	 * @param pFileName
	 */
	public void saveFileCompact(String pFileName) {
		saveFile(pFileName, OutputFormat.createCompactFormat());
	}
	
	protected void saveFile(String pFileName, OutputFormat pFormat) {
		pFormat.setEncoding("windows-1252");
        try {
			XMLWriter writer = new XMLWriter(
					new FileWriter(pFileName), 
					pFormat);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return true if document is valid and the root element contains children
	 */
	public boolean isOkay() {
		return doc != null
			&& doc.getRootElement() != null
			&& doc.getRootElement().elements() != null
			&& !doc.getRootElement().elements().isEmpty();
	}
	
	/**
	 * @return root element
	 */
	public XMLElement getElement() {
		return doc == null ? null : XMLElementImpl.create(doc.getRootElement());
	}
	
	/**
	 * Quick access to children of root element
	 * @return children of root element
	 */
	public List<XMLElement> getChildren() {
		return XMLElementImpl.getChildElements(doc.getRootElement().elements());
	}
	
	/**
	 * Document level selectNodes access<br>
	 * An exception will be thrown if the XPath command is false.
	 * @param pXPath XPath String
	 * @return XMLElement list
	 */
	public List<XMLElement> selectNodes(String pXPath) {
		return XMLElementImpl.getChildElements(doc.selectNodes(pXPath));
	}
	
	/**
	 * Document level selectSingleNode access<br>
	 * An exception will be thrown if the XPath command is false.
	 * @param pXPath XPath String. The XPath string must be built that way that only one element is returned.
	 * @return XMLElement or null if no element was found
	 */
	public XMLElement selectSingleNode(String pXPath) {
		Node node = doc.selectSingleNode(pXPath);
		return node == null ? null : XMLElementImpl.create((Element) node);
	}
	
	/**
	 * Assumption is that there is exactly one element with the id attribute in the document.
	 * @param id
	 * @return XMLElement with that id, or null if no element match that id
	 */
	public XMLElement byId(String id) {
		return selectSingleNode("//*[@id='" + id + "']");
	}
	
	/**
	 * Delete element with attribute 'id' = id.
	 * That element can't be the root element. It is not necessary that the element is the direct child of the root element.
	 * @param id Content of attribute 'id'
	 * @return true if child was deleted,
	 * false if child wasn't found.
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
		return doc.asXML();
	}
}
