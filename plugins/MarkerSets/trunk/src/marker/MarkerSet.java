package marker;

import java.awt.Color;
import java.util.Vector;

public class MarkerSet {

	private String name;
	private Vector<FileMarker> markers;
	private Color color;
	
	public MarkerSet(String name) {
		this.name = name;
		color = Color.black;
		markers = new Vector<FileMarker>();
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
}
