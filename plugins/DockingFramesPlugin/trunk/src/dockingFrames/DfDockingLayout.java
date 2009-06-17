package dockingFrames;

import java.io.File;
import java.io.IOException;

import org.gjt.sp.jedit.jEdit;

public class DfDockingLayout extends
		org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout {

	private static final String TEMP_LAYOUT_NAME = "temp";
	private DfWindowManager wm;
	private String layoutFilename;
	
	public DfDockingLayout() {
	}
	public DfDockingLayout(DfWindowManager manager) {
		wm = manager;
		// Save the layout, so it can be retrieved later, unless we're
		// called during jEdit startup, when the perspective is loaded.
		if (jEdit.isStartupDone())
		{
			saveLayout(TEMP_LAYOUT_NAME, NO_VIEW_INDEX);
			layoutFilename = getLayoutFilename(TEMP_LAYOUT_NAME, NO_VIEW_INDEX);
		}
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
		try {
			wm.getControl().writeXML(new File(filename));
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
