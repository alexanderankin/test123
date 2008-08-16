package dockingFrames;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.util.xml.XElement;

public class DfDockingLayout extends
		org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout {

	private DfWindowManager wm;
	private String layoutFilename;
	
	public void setWindowManager(DfWindowManager manager) {
		wm = manager;
	}
	public String getPersistenceFilename() {
		return layoutFilename;
	}
	
	@Override
	public boolean loadLayout(String baseName, int viewIndex) {
		layoutFilename = getLayoutFilename(baseName, viewIndex);
		return true;
	}
	
	@Override
	public boolean saveLayout(String baseName, int viewIndex)
	{
		String filename = getLayoutFilename(baseName, viewIndex);
		DockSituation situation = wm.getDockSituation();
		XElement root = new XElement("layout");
		try {
			situation.writeXML(wm.getStationMap(), root);
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(root.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String getName() {
		return "DockingFramesPlugin";
	}

}
