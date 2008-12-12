package myDoggy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;
import org.noos.xing.mydoggy.ToolWindowManager;

public class MyDoggyDockingLayout extends DockingLayout {

	private static final String TEMP_LAYOUT_NAME = "temp";
	private ToolWindowManager wm = null;
	private String layoutFilename = null;
	
	public MyDoggyDockingLayout() {
	}
	
	public MyDoggyDockingLayout(ToolWindowManager manager) {
		wm = manager;
		// Save the layout, so it can be retrieved later, unless we're
		// called during jEdit startup, when the perspective is loaded.
		if (jEdit.isStartupDone()) {
			saveLayout(TEMP_LAYOUT_NAME, NO_VIEW_INDEX);
			layoutFilename = getLayoutFilename(TEMP_LAYOUT_NAME, NO_VIEW_INDEX);
		}
	}
	
	public String getLayoutFilename() {
		return layoutFilename;
	}
	
	@Override
	public boolean loadLayout(String baseName, int viewIndex) {
		layoutFilename = getLayoutFilename(baseName, viewIndex);
		return true;
	}

	@Override
	public boolean saveLayout(String baseName, int viewIndex) {
		String filename = getLayoutFilename(baseName, viewIndex);
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(filename);
			wm.getPersistenceDelegate().save(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "MyDoggyPlugin";
	}

}
