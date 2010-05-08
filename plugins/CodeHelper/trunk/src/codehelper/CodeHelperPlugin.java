package codehelper;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class CodeHelperPlugin extends EditPlugin
{
	private static final String STATIC_CALL_TREE_DOCKABLE = "codehelper-static-call-tree";
	public void start()
	{
	}
	public void stop()
	{
	}
	public static void showStaticCallTree(View view)
	{
		DockableWindowManager dwm = view.getDockableWindowManager();
		dwm.showDockableWindow(STATIC_CALL_TREE_DOCKABLE);
		StaticCallTree dockable = (StaticCallTree)
			dwm.getDockable(STATIC_CALL_TREE_DOCKABLE);
		dockable.showTreeFor(view.getTextArea().getSelectedText());
	}
}
