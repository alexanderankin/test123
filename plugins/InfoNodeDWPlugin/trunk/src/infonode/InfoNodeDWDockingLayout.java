package infonode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.infonode.docking.RootWindow;

import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

public class InfoNodeDWDockingLayout extends DockingLayout {

	private static final String TEMP_LAYOUT_NAME = "temp";
	private String layoutFilename = null;
	private RootWindow rootWindow;
	
	public InfoNodeDWDockingLayout() {
	}
	
	public InfoNodeDWDockingLayout(RootWindow rw) {
		saveLayout(TEMP_LAYOUT_NAME, NO_VIEW_INDEX);
		layoutFilename = getLayoutFilename(TEMP_LAYOUT_NAME, NO_VIEW_INDEX);
		rootWindow = rw;
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
		ObjectOutputStream ous;
		try {
			outputStream = new FileOutputStream(filename);
			ous = new ObjectOutputStream(outputStream);
			rootWindow.write(ous);
			ous.close();
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
