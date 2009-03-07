package marker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileMarker {
	private static final String SHORTCUT_ATTR = "shortcut";
	private static final String LINE_ATTR = "line";
	private static final String FILE_ATTR = "file";
	String file;
	int line;
	String shortcut;
	
	public FileMarker(String file, int line, String shortcut) {
		this.file = file;
		this.line = line;
		this.shortcut = shortcut;
	}
	public FileMarker(Element node) {
		importXml(node);
	}
	
	public String toString() {
		return file + ":" + line + ":" + shortcut;
	}
	
	public void importXml(Element node)
	{
		file = node.getAttribute(FILE_ATTR);
		line = Integer.valueOf(node.getAttribute(LINE_ATTR));
		shortcut = node.getAttribute(SHORTCUT_ATTR);
	}
	
	public void exportXml(Element parent)
	{
		Document doc = parent.getOwnerDocument();
		Element marker = doc.createElement("Marker");
		parent.appendChild(marker);
		marker.setAttribute(FILE_ATTR, file);
		marker.setAttribute(LINE_ATTR, String.valueOf(line));
		marker.setAttribute(SHORTCUT_ATTR, String.valueOf(shortcut));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof FileMarker))
			return false;
		FileMarker other = (FileMarker) obj;
		return (file.equals(other.file) && line == other.line);
	}

	@Override
	public int hashCode() {
		return file.hashCode() + line;
	}

	
}
