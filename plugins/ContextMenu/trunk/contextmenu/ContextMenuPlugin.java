/*
* ContextMenuPlugin.java
* Copyright (c) 2006 Jakub Roztocil <j.roztocil@gmail.com>
*
* $Id$
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*
*/


package contextmenu;


//{{{ Imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.options.PluginOptions;
import org.gjt.sp.util.Log;


public class ContextMenuPlugin extends EBPlugin {


	//{{{ Private static properties

	public static final int CACHE_DONT = 0;
	public static final int CACHE_MENUBAR = 1;
	public static final int CACHE_POPUP = 2;

	private static HashMap cacheMenuBar = new HashMap();
	private static HashMap cachePopUp = new HashMap();

	private static int CORE_MENUBAR_ITEMS_COUNT;

	/**
	 * If is true, then after the DynamicMenuChanged message with attr. menuName="recent-files"
	 * is recieved, we update menus in all views, otherwise this message is ignored.
	 * True is only during startup and between PropertiesChanged and first DynamicMenuChanged
	 * messages - it's because in these moments is the menubar [re]created.
	 */
	private static boolean handleRecentFilesUpdate = true;
	//}}}

	//{{{ ContextMenuPlugin constructor
	public ContextMenuPlugin() {
		super();
	} //}}}

	//{{{ start()
	public void start() {
		CORE_MENUBAR_ITEMS_COUNT = (new StringTokenizer(jEdit.getProperty("view.mbar"))).countTokens();
		Log.log(Log.DEBUG, ContextMenuPlugin.class, "start()");
		updateAllViews();
	} //}}}

	//{{{ stop()
	public void stop() {
		Log.log(Log.DEBUG, ContextMenuPlugin.class, "stop()");
		resetAllMenus();
		clearCache();
	} //}}}

	//{{{ handleMessage()
	public void handleMessage(EBMessage message) {

		if (!jEdit.getBooleanProperty("contextmenu.in-menubar")) {
			return;
		}

		View view = null;

		//{{{ EditPaneUpdate
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate)message;
			if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
				view = epu.getEditPane().getView();
			}

		}
		//}}}
		//{{{ BufferUpdate
		else if (message instanceof BufferUpdate) {

			BufferUpdate bu = (BufferUpdate)message;

			if (bu.getWhat() == BufferUpdate.PROPERTIES_CHANGED) {
				if (bu.getBuffer().isLoaded()) {
					view = jEdit.getActiveView();
				}
			} else if (bu.getWhat() == BufferUpdate.LOADED) {
				view = bu.getView();
				if (view == null) {
					view = jEdit.getActiveView();
				}
			}

		} //}}}
		//{{{ ViewUpdate
		else if (message instanceof ViewUpdate) {

			ViewUpdate msg = (ViewUpdate)message;
			if (msg.getWhat() == ViewUpdate.CREATED
				|| msg.getWhat() == ViewUpdate.EDIT_PANE_CHANGED)
			{
				view = msg.getView();
			}
		} //}}}
		//{{{ PropertiesChanged
		else if (message instanceof PropertiesChanged) {
			handleRecentFilesUpdate = true;
			clearCache();
			updateAllViews();

		} //}}}
		//{{{ DynamicMenuChanged
		else if (message instanceof DynamicMenuChanged) {
			if (handleRecentFilesUpdate &&
				((DynamicMenuChanged)message).getMenuName().equals("recent-files")) {
				updateAllViews();
				handleRecentFilesUpdate = false;
			}
		} //}}}

		if (view != null) {
			String mode = getMode(view);
			if (mode == null) {
				return;
			}
			addToMenubar(view, mode);

		}
	} //}}}

	//{{{ getMode()
	public static String getMode(View view) {
		Buffer buffer = (Buffer)view.getBuffer();
		if (buffer != null) {
			Mode mode = buffer.getMode();
			if (mode != null) {
				return mode.getName();
			}
		}
		return null;
	} //}}}

	//{{{ addToMenubar()
	public static void addToMenubar(View view) {
		addToMenubar(view, getMode(view));
	} //}}}

	//{{{ addToMenubar()
	public static void addToMenubar(View view, String mode) {

		JMenuBar menuBar = view.getJMenuBar();
		if (menuBar != null && jEdit.getBooleanProperty("contextmenu.in-menubar")) {
			if (hasMenuBarCustomMenu(menuBar)) {
				removeCustomMenuFromMenuBar(menuBar);
			}
			JMenu contextMenu = getMenuForMode(mode, CACHE_MENUBAR);
			if (contextMenu != null) {
				JMenu help = menuBar.getMenu(menuBar.getMenuCount() - 1);
				menuBar.remove(help);
				menuBar.add(contextMenu);
				menuBar.add(help);
				menuBar.updateUI();
			}
		}


	} //}}}

	//{{{ getActionsForMode()
	public static StringTokenizer getActionsForMode(String modeName) {
		String actions = jEdit.getProperty("mode." + modeName + ".contextmenu");
		if (actions == null) {
			return null;
		}
		return new StringTokenizer(actions);
	} //}}}

	//{{{ getMenuForMode()
	/**
	 * @param String mode
	 * @param int cacheInfo We need to have two caches, because
	 *                      the mode-specific menu can be displayed at two places
	 *                      in the same moment (menubar and popup)
	 * @return JMenu
	 */
	public static JMenu getMenuForMode(String mode, int cacheInfo) {
		JMenu menu = null;
		HashMap cache = null;

		if (cacheInfo == CACHE_MENUBAR) {
			cache = cacheMenuBar;
		} else if (cacheInfo == CACHE_POPUP) {
			cache = cachePopUp;
		}

		if (cacheInfo != CACHE_DONT && cache.containsKey(mode)) {
			menu = (JMenu)cache.get(mode);
		} else {
			String propName = "mode." + mode + ".contextmenu";
			if (jEdit.getProperty(propName) != null) {
				menu = GUIUtilities.loadMenu(propName);
				menu.addSeparator();
				menu.add(getCustomizeModeItem(jEdit.getProperty("view.context.customize")));
				if (cacheInfo != CACHE_DONT) {
					cache.put(mode, menu);
				}
			}
		}
		return menu;
	} //}}}

	//{{{ openPluginOptionsDialog()
	public static void openPluginOptionsDialog() {
		new PluginOptions(jEdit.getActiveView(), "contextmenu-modes");
	} //}}}

	//{{{ getCustomizeModeItem()
	public static JMenuItem getCustomizeModeItem(String text) {
		JMenuItem customize = new JMenuItem(text);
		customize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
						openPluginOptionsDialog();
		}});
		return customize;
	} //}}}

	//{{{ Private methods

	//{{{ removeCustomMenuFromMenuBar()
	private static void removeCustomMenuFromMenuBar(JMenuBar menu) {
		menu.remove(menu.getMenu(menu.getMenuCount() - 2));
	} //}}}

	//{{{ resetAllMenus()
	private static void resetAllMenus() {
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++) {
			View view = views[i];
			// remove context menu from menubar
			JMenuBar menuBar = view.getJMenuBar();
			if (menuBar != null && hasMenuBarCustomMenu(menuBar)) {
				removeCustomMenuFromMenuBar(menuBar);
			}
		}
	} //}}}

	//{{{ updateAllViews()
	private static void updateAllViews() {
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++) {
			addToMenubar(views[i]);
		}
	} //}}}

	//{{{ clearCache()
	private static void clearCache() {
		cacheMenuBar.clear();
		cachePopUp.clear();
	} //}}}

	//{{{ hasMenuBarCustomMenu()
	private static boolean hasMenuBarCustomMenu(JMenuBar menu) {
		return menu.getMenuCount() > CORE_MENUBAR_ITEMS_COUNT;
	} //}}}

	//}}}

}



/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */
