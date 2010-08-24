package marker;

import java.util.Vector;

import javax.swing.text.Position;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("rawtypes")
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
	private String text;
	private Vector<Selection> selection;

	public class Selection {
		public int start, end;
		public Selection(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	public FileMarker(String file, int line) {
		init(file, line);
		attachToBuffer(file);
	}
	public FileMarker(Buffer b, int line) {
		init(b.getPath(), line);
		if (b.isLoaded())
			createPosition(b);
	}
	// Fixed line text - do not extract the text from the file
	public FileMarker(String file, int line, String text)
	{
		this(file, line);
		this.text = text;
	}
	public FileMarker(Element node) {
		importXml(node);
		Buffer b = jEdit.getBuffer(file);
		if ((b != null) && b.isLoaded())
			createPosition(b);
	}
	private void init(String file, int line) {
		this.file = file;
		this.line = line;
		text = null;
		pos = null;
		selection = null;
		setShortcut(null);
	}
	private void attachToBuffer(String file) {
		Buffer b = jEdit.getBuffer(file);
		if ((b == null) || (! b.isLoaded()))
			pos = null;
		else
			createPosition(b);
	}

	public void setShortcut(String shortcut) {
		if ("".equals(shortcut))
			shortcut = null;
		this.shortcut = shortcut;
		shortcutStr = (shortcut == null) ? "" : "[" + shortcut + "] ";
	}
	
	public String getShortcut() {
		return (shortcut == null) ? "" : shortcut;
	}

	public void jump(View view) {
		MarkerSetsPlugin.jump(view, file, getLine(), getSelections());
	}
	
	public String toString() {
		return shortcutStr + file + "(" + (getLine() + 1) + "): " + getLineText();
	}
	
	public int getLine() {
		if (pos == null)
			return line;
		return buffer.getLineOfOffset(pos.getOffset());
	}
	
	public String getLineText() {
		if (text != null)
			return text;
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
		init(node.getAttribute(FILE_ATTR), Integer.valueOf(
			node.getAttribute(LINE_ATTR)));
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
		if (b.getLineCount() <= line)
			return;
		pos = b.createPosition(b.getLineStartOffset(line));
		buffer = b;
	}
	
	public void removePosition()
	{
		/* Return if no position was created; this can happen if the marker's
		 * line is greater than the buffer's line count - i.e. the  buffer was
		 * cut externally or the marker refers to an untitled buffer that was
		 * saved.
		 */
		if (buffer == null)
			return;
		line = buffer.getLineOfOffset(pos.getOffset());
		pos = null;
		buffer = null;
	}
	
	public void addSelection(Selection s)
	{
		if (selection == null)
			selection = new Vector<Selection>();
		int i;
		Selection cur = null;
		for (i = 0; i < selection.size(); i++) {
			cur = selection.get(i);
			if (cur.start > s.start) {
				if (s.end > cur.start) {
					cur.start = s.start;
					if (s.end > cur.end)
						cur.end = s.end;
					return;
				}
				break;
			} else if (cur.end > s.start) {
				if (cur.end > s.end)
					cur.end = s.end;
				return;
			}
		}
		selection.insertElementAt(s, i);
	}
	
	public Vector<Selection> getSelections()
	{
		return selection;
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
