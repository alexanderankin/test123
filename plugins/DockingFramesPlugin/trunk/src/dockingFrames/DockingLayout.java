package dockingFrames;

import java.io.File;
import java.io.IOException;

import org.gjt.sp.jedit.SettingsXML.Saver;
import org.xml.sax.helpers.DefaultHandler;

public class DockingLayout extends
		org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout {

	@Override
	public DefaultHandler getPerspectiveHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void savePerspective(File file, Saver out, String lineSep)
			throws IOException {
		// TODO Auto-generated method stub

	}

}
