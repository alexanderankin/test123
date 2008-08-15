package dockingFrames;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.SettingsXML.Saver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.util.xml.XElement;

public class DfDockingLayout extends
		org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout {

	private static final String FILE_ATTR = "FILE";
	private static final String DOCKINGFRAMES_ELEMENT = "DOCKINGFRAMES";
	private DfWindowManager wm;
	private int index;
	private String filename;
	
	public void setIndex(int i) {
		index = i;
	}
	public void setManager(DfWindowManager manager) {
		wm = manager;
	}
	public String getPersistenceFilename() {
		return filename;
	}
	@Override
	public DefaultHandler getPerspectiveHandler() {
		return new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName, String name,
					Attributes attributes) throws SAXException {
				if (name.equals(DOCKINGFRAMES_ELEMENT)) {
					filename = attributes.getValue(FILE_ATTR);
					if (filename == null)
						return;
				}
			}
		};
	}
	
	private String getFilename(File f) {
		String dir = jEdit.getSettingsDirectory() + File.separator + "DockingFramesPlugin";
		File d = new File(dir);
		if (! d.exists())
			d.mkdir();
		String name = "df-" + index + "-" + f.getName();
		if (! name.toLowerCase().endsWith(".xml"))
			name = name + ".xml";
		return dir + File.separator + name;
	}

	@Override
	public void savePerspective(File file, Saver out, String lineSep)
			throws IOException
	{
		String filename = getFilename(file);
		out.write("<" + DOCKINGFRAMES_ELEMENT + " FILE=\"");
		out.write(filename);
		out.write("\" />");
		out.write(lineSep);
		DockSituation situation = new DockSituation();
		situation.add(wm.getDockFactory());
		XElement root = new XElement("layout");
		situation.writeXML(wm.getStationMap(), root);
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(root.toString());
		writer.close();
	}

}
