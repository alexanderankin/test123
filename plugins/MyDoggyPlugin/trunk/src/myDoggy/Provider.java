package myDoggy;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.DockingFrameworkProvider;
import org.gjt.sp.jedit.gui.DockableWindowManager.DockingLayout;

public class Provider implements DockingFrameworkProvider {

	public DockableWindowManager create(View view,
			DockableWindowFactory instance, ViewConfig config)
	{
		return new MyDoggyWindowManager(view, instance, config);
	}

	public DockingLayout createDockingLayout() {
		return new MyDoggyDockingLayout();
	}

}
