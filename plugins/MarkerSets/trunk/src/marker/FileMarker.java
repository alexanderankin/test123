package marker;

import javax.swing.text.Position;

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
	private int line;
	public String shortcut;
	private String shortcutStr;
	private Position pos;
	private Buffer buffer;
	
	public FileMarker(String file, int line, String shortcut) {
		init(file, line, shortcut);
		Buffer b = jEdit.getBuffer(file);
		if (b == null)
			pos = null;
		else
			createPosition(b);
	}
	public FileMarker(Buffer b, int line, String shortcut) {
		init(b.getPath(), line, shortcut);
		createPosition(b);
	}
	public FileMarker(Element node) {
		importXml(node);
		Buffer b = jEdit.getBuffer(file);
		if (b != null)
			createPosition(b);
	}
	private void init(String file, int line, String shortcut) {
		this.file = file;
		this.line = line;
		setShortcut(shortcut);
	}

	private void setShortcut(String shortcut) {
		if ("".equals(shortcut))
			shortcut = null;
		this.shortcut = shortcut;
		shortcutStr = (shortcut == null) ? "" : "[" + shortcut + "] ";
	}
	
	public void jump(View view) {
		MarkerSetsPlugin.jump(view, file, getLine());
	}
	
	public String toString() {
		return shortcutStr + file + "(" + getLine() + "): " + getLineText();
	}
	
	public int getLine() {
		if (pos == null)
			return line;
		return buffer.getLineOfOffset(pos.getOffset());
	}
	
	public String getLineText() {
		Buffer buffer = jEdit.openTemporary(null,null,file,false);
		if (buffer == null)
			return "";
		try {
			return buffer.getLineText(getLine());
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
		marker.setAttribute(LINE_ATTR, String.valueOf(getLine()));
		if (shortcut != null)
			marker.setAttribute(SHORTCUT_ATTR, shortcut);
	}
	
	public void createPosition(Buffer b)
	{
		pos = b.createPosition(b.getLineStartOffset(line));
		buffer = b;
	}
	
	public void removePosition()
	{
		line = buffer.getLineOfOffset(pos.getOffset());
		pos = null;
		buffer = null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof FileMarker))
			return false;
		FileMarker other = (FileMarker) obj;
		return (file.equals(other.file) && getLine() == other.getLine());
	}

	@Override
	public int hashCode() {
		return file.hashCode() + getLine();
	}
	
	public int compareTo(Object o) {
		if (! (o instanceof FileMarker))
			return 0;
		FileMarker other = (FileMarker) o;
		int res = file.compareTo(other.file);
		if (res != 0)
			return res;
		int l1 = getLine();
		int l2 = other.getLine();
		if (l1 < l2)
			return -1;
		if (l1 == l2)
			return 0;
		return 1;
	}
	
}
