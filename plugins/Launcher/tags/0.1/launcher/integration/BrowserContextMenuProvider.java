package launcher.integration;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import launcher.LauncherPlugin;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;

public class BrowserContextMenuProvider implements DynamicMenuProvider {
	
	public static final BrowserContextMenuProvider INSTANCE = 
		new BrowserContextMenuProvider();

	public void update(JMenu menu) {
		VFSBrowser browser = (VFSBrowser)GUIUtilities.
				getComponentParent(menu, VFSBrowser.class);
		VFSFile[] vfsFiles = browser.getSelectedFiles(menu);
		if (vfsFiles == null || vfsFiles.length == 0)
			return;

		LauncherPlugin plugin = (LauncherPlugin)jEdit.getPlugin(LauncherPlugin.class.getName());
		JMenuItem[] items = plugin.getMenuItemsFor(vfsFiles);
		LauncherUtils.addItemsToMenu(menu, items);
	}

	public boolean updateEveryTime() {
		return true; // The LauncherPlugin cache makes this less heavy 
	}

}
