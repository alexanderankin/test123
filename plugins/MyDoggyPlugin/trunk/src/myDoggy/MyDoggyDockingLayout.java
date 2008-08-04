package myDoggy;

import java.io.IOException;

import org.gjt.sp.jedit.SettingsXML.Saver;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;
import org.xml.sax.helpers.DefaultHandler;

public class MyDoggyDockingLayout extends DockingLayout {

	@Override
	public DefaultHandler getPerspectiveHandler() {
		return new DefaultHandler();
	}

	@Override
	public void move(int dx, int dy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void savePerspective(Saver out, String lineSep) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPlainView(boolean plain) {
		// TODO Auto-generated method stub

	}

}
