/*
* ContextMenuPlugin.java
* Copyright (c) 2006 Jakub Roztocil <j.roztocil@gmail.com>
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
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.MenuElement;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.menu.EnhancedMenu;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.options.GlobalOptions;
import org.gjt.sp.jedit.options.PluginOptions;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}


public class ContextMenuPlugin extends EBPlugin {


	//{{{ Private static properties
	private static HashMap popupCache = new HashMap();
	private static int CORE_MENUBAR_ITEMS_COUNT;

	/**
	* If is true, after DynamicMenuChanged/@menuName="recent-files"
	* message is recieved, we update menus in all views, othervise this message will ignored.
	* True is only during startup and between PropertiesChanged and first DynamicMenuChanged
	* messages, it's because in that moments is menubar [re]created. After PropertiesChanged
	* it works only, if is not open just one untitled buffer.
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
		addCorePopupToCache();
		updateAllViews();
	} //}}}

	//{{{ stop()
	public void stop() {
		Log.log(Log.DEBUG, ContextMenuPlugin.class, "stop()");
		resetAllMenus();
		clearMenuCache();
	} //}}}

	//{{{ handleMessage()
	public void handleMessage(EBMessage message) {

		if (!jEdit.getBooleanProperty("contextmenu.in-menubar")
			&& !jEdit.getBooleanProperty("contextmenu.in-context-menu"))
		{
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
			clearMenuCache();
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
			setMenus(view, mode);

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

	//{{{ setMenus()
	public static void setMenus(View view) {
		setMenus(view, getMode(view));
	} //}}}

	//{{{ setMenus()
	public static void setMenus(View view, String mode) {

		Log.log(Log.DEBUG, ContextMenuPlugin.class, "setMenus(" + mode + ")");

		JMenuBar menuBar = view.getJMenuBar();
		if (menuBar != null && jEdit.getBooleanProperty("contextmenu.in-menubar")) {
			if (hasMenuBarCustomMenu(menuBar)) {
				removeCustomMenuFromMenuBar(menuBar);
			}
			JMenu contextMenu = loadCustomMenuForMode(mode);
			if (contextMenu != null) {
				JMenu help = menuBar.getMenu(menuBar.getMenuCount() - 1);
				menuBar.remove(help);
				menuBar.add(contextMenu);
				menuBar.add(help);
				menuBar.updateUI();
			}
		}

		if (jEdit.getBooleanProperty("contextmenu.in-popup")) {
			view.getTextArea().setRightClickPopup(getPopupForMode(mode));
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

	//{{{ hasMenuBarCustomMenu()
	private static boolean hasMenuBarCustomMenu(JMenuBar menu) {
		return menu.getMenuCount() > CORE_MENUBAR_ITEMS_COUNT;
	} //}}}

	//{{{ removeCustomMenuFromMenuBar()
	private static void removeCustomMenuFromMenuBar(JMenuBar menu) {
		menu.remove(menu.getMenu(menu.getMenuCount() - 2));
	} //}}}

	//{{{ loadCustomMenuForMode()
	private static JMenu loadCustomMenuForMode(String mode) {
		JMenu menu;
		String property = "mode." + mode + ".contextmenu";
		if (jEdit.getProperty(property) != null) {
			menu = GUIUtilities.loadMenu(property);
			menu.addSeparator();
			menu.add(getCustomizeModeItem(jEdit.getProperty("view.context.customize")));
			return menu;
		}
		return null;
	} //}}}

	//{{{ getPopupForMode()
	private static JPopupMenu getPopupForMode(String mode) {

		if (popupCache.containsKey(mode)) {
			Log.log(Log.DEBUG, ContextMenuPlugin.class, "popup from cache (" + mode + ")");
			return (JPopupMenu)popupCache.get(mode);
		}

		if (jEdit.getProperty("mode." + mode + ".contextmenu") == null) {
			// this mode has not own context menu
			Log.log(Log.DEBUG, ContextMenuPlugin.class, "jedit-core-popup");
			return (JPopupMenu)popupCache.get("jedit-core-popup");
		}

		// create new context popup
		JPopupMenu jeditPopup = GUIUtilities.loadPopupMenu("view.context");
		JPopupMenu contextPopup = GUIUtilities.loadPopupMenu("mode." + mode + ".contextmenu");

		MenuElement[] contextElements = contextPopup.getSubElements();

		jeditPopup.addSeparator();

		for (int i = 0; i < contextElements.length; i++) {
			jeditPopup.add((JMenuItem)contextElements[i]);
		}

		jeditPopup.addSeparator();
		jeditPopup.add(getCustomizeMenu());

		popupCache.put(mode, jeditPopup);

		return jeditPopup;
	} //}}}

	//{{{ openPopupMenu()
	// Based on: Open_Context_Menu.bsh Copyright (C) 2003 Nitsan Vardi
	public static void openPopupMenu() {
		View view = jEdit.getActiveView();
		JEditTextArea textArea = view.getTextArea();
		String mode = getMode(view);
		int caretOffset = textArea.getCaretPosition();
		Point caretPos = textArea.offsetToXY(caretOffset);
		GUIUtilities.showPopupMenu(getPopupForMode(mode),
			textArea,
			caretPos.x + 10,
			caretPos.y + 20);
	} //}}}

	//{{{ openPluginOptionsDialog()
	public static void openPluginOptionsDialog() {
		new PluginOptions(jEdit.getActiveView(), "contextmenu-modes");
	} //}}}

	//{{{ resetAllMenus()
	private static void resetAllMenus() {
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++) {
			View view = views[i];
			// set default popup
			JPopupMenu popup = (JPopupMenu)popupCache.get("jedit-core-popup");
			view.getTextArea().setRightClickPopup(popup);
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
			setMenus(views[i]);
		}
	} //}}}

	//{{{ clearMenuCache()
	private static void clearMenuCache() {
		popupCache.clear();
		addCorePopupToCache();
	} //}}}

	//{{{ addCorePopupToCache()
	private static void addCorePopupToCache() {
		JPopupMenu corePopup = GUIUtilities.loadPopupMenu("view.context");
		corePopup.addSeparator();
		corePopup.add(getCustomizeMenu());
		popupCache.put("jedit-core-popup", corePopup);
	} //}}}

	//{{{ getCustomizeGlobalItem()
	private static JMenuItem getCustomizeGlobalItem(String text) {
		// "Customize This Menu" entry in popup menu
		JMenuItem customize = new JMenuItem(text);
		customize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
						new GlobalOptions(jEdit.getActiveView(), "context");
		}});
		return customize;
	} //}}}

	//{{{ getCustomizeModeItem()
	private static JMenuItem getCustomizeModeItem(String text) {
		JMenuItem customize = new JMenuItem(text);
		customize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
						openPluginOptionsDialog();
		}});
		return customize;
	} //}}}

	//{{{ getCustomizeMenu()
	private static JMenu getCustomizeMenu() {
		JMenu menu = new JMenu(jEdit.getProperty("view.context.customize").replace("...", ""));
		menu.add(getCustomizeGlobalItem(jEdit.getProperty("contextmenu.customize-global")));
		menu.add(getCustomizeModeItem(jEdit.getProperty("contextmenu.customize-mode")));
		return menu;
	} //}}}


}



/* :folding=explicit:collapseFolds=1:tabSize=4:indentSize=4:noTabs=false: */
