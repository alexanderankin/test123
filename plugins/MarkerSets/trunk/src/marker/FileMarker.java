package marker;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("unchecked")
public class FileMarker implements Comparable {
	private static final String SHORTCUT_ATTR = "shortcut";
	private static final String LINE_ATTR = "line";
	private static final String FILE_ATTR = "file";
	public String file;
	public int line;
	public String shortcut;
	private String shortcutStr;
	
	public FileMarker(String file, int line, String shortcut) {
		this.file = file;
		this.line = line;
		setShortcut(shortcut);
	}
	public FileMarker(Element node) {
		importXml(node);
	}

	private void setShortcut(String shortcut) {
		if ("".equals(shortcut))
			shortcut = null;
		this.shortcut = shortcut;
		shortcutStr = (shortcut == null) ? "" : "[" + shortcut + "] ";
	}
	
	public void jump(View view) {
		MarkerSetsPlugin.jump(view, file, line);
	}
	
	public String toString() {
		return shortcutStr + file + "(" + line + "): " + getLineText();
	}
	
	public String getLineText() {
		Buffer buffer = jEdit.openTemporary(null,null,file,false);
		if (buffer == null)
			return "";
		try {
			return buffer.getLineText(line);
		} catch (Exception e) {
		}
		return "";
	}
	
	public void importXml(Element node)
	{
		file = node.getAttribute(FILE_ATTR);
		line = Integer.valueOf(node.getAttribute(LINE_ATTR));
		setShortcut(node.getAttribute(SHORTCUT_ATTR));
	}
	
	public void exportXml(Element parent)
	{
		Document doc = parent.getOwnerDocument();
		Element marker = doc.createElement("Marker");
		parent.appendChild(marker);
		marker.setAttribute(FILE_ATTR, file);
		marker.setAttribute(LINE_ATTR, String.valueOf(line));
		if (shortcut != null)
			marker.setAttribute(SHORTCUT_ATTR, shortcut);
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
	
	public int compareTo(Object o) {
		if (! (o instanceof FileMarker))
			return 0;
		FileMarker other = (FileMarker) o;
		int res = file.compareTo(other.file);
		if (res != 0)
			return res;
		if (line < other.line)
			return -1;
		if (line == other.line)
			return 0;
		return 1;
	}
	
}
