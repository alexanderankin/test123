package marker;

import java.awt.Color;
import java.util.Vector;

import org.gjt.sp.util.SyntaxUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MarkerSet {

	private static final String COLOR_ATTR = "color";
	private static final String NAME_ATTR = "name";
	private String name;
	private Vector<FileMarker> markers;
	private Color color;
	
	public MarkerSet(String name) {
		this.name = name;
		color = Color.black;
		markers = new Vector<FileMarker>();
	}
	public MarkerSet(Element node) {
		markers = new Vector<FileMarker>();
		importXml(node);
	}
	public void setColor(Color c) { color = c; }
	public Color getColor() { return color; }
	
	public String getName() { return name; }
	
	public void toggle(FileMarker marker) {
		if (markers.contains(marker))
			remove(marker);
		else
			add(marker);
	}
	
	public void add(FileMarker marker) {
		if (markers.contains(marker))
			return;
		markers.add(marker);
	}
	
	public void remove(FileMarker marker) {
		markers.remove(marker);
	}
	
	public Vector<FileMarker> getMarkers() {
		return new Vector<FileMarker>(markers);
	}
	
	public FileMarker getMarkerFor(String path, int line)
	{
		for (int i = 0; i < markers.size(); i++)
		{
			FileMarker marker = markers.get(i);
			if (marker.file.equals(path) && marker.line == line)
				return marker;
		}
		return null;
	}
	
	public void importXml(Element node)
	{
		name = node.getAttribute(NAME_ATTR);
		color = SyntaxUtilities.parseColor(node.getAttribute(COLOR_ATTR),
			Color.black);
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child instanceof Element)
				markers.add(new FileMarker((Element) child));
		}
	}
	public void exportXml(Element parent)
	{
		Document doc = parent.getOwnerDocument();
		Element setNode = doc.createElement("MarkerSet");
		parent.appendChild(setNode);
		setNode.setAttribute(NAME_ATTR, getName());
		setNode.setAttribute(COLOR_ATTR,
			SyntaxUtilities.getColorHexString(color));
		for (int i = 0; i < markers.size(); i++)
			markers.get(i).exportXml(setNode);
	}
}
