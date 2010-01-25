package launcher.integration;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import launcher.LauncherPlugin;
import launcher.LauncherUtils;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class PluginContextMenuProvider implements DynamicMenuProvider {
	
	public static final PluginContextMenuProvider INSTANCE = 
		new PluginContextMenuProvider();

	public void update(JMenu menu) {
		LauncherPlugin plugin = (LauncherPlugin)jEdit.getPlugin(LauncherPlugin.class.getName());
		JEditTextArea textArea = GUIUtilities.getView(menu).getTextArea();
		// Using TextArea because this makes it possible for a Launcher
		// to support JEditTextArea as the resource to be launched
		// (or most likely the selected text, see
		// launcher.textarea.TextAreaLauncher).
		// In any case a JEditTextArea can be resolved to a File or URI
		// in LauncherUtils.
		JMenuItem[] items = plugin.getMenuItemsFor(textArea);
		LauncherUtils.addItemsToMenu(menu, items);
	}

	public boolean updateEveryTime() {
		return true; // The LauncherPlugin cache makes this less heavy 
	}

}
