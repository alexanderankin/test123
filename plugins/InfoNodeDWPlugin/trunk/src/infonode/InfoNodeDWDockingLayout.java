package infonode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

public class InfoNodeDWDockingLayout extends DockingLayout {

	private static final String TEMP_LAYOUT_NAME = "temp";
	private String layoutFilename = null;
	
	public InfoNodeDWDockingLayout() {
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
			//wm.getPersistenceDelegate().save(outputStream);
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
		return "InfoNodeDWPlugin";
	}

}
