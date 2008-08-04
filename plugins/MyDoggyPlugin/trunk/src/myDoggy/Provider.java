package myDoggy;

import javax.swing.JPanel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.IDockingFrameworkProvider;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

public class Provider implements IDockingFrameworkProvider {

	public DockableWindowManager create(View view,
			DockableWindowFactory instance, ViewConfig config)
	{
		return new MyDoggyWindowManager(view, instance, config);
	}

	public DockingLayout createDockingLayout() {
		return new MyDoggyDockingLayout();
	}

}
