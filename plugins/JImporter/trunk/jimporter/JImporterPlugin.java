/*
 *  JImporterPlugin.java - Plugin for add java imports to the top of a java file.
 *  Copyright (C) 2002 Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter;

import java.util.Vector;
import jimporter.options.ClasspathOptionPane;
import jimporter.options.JImporterOptionPane;
import jimporter.options.MiscellaneousOptionPane;
import jimporter.options.SortingOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.jEdit;

/**
 * This class is designed to follow the JEdit API specification for adding a
 * plugin, or extension to the JEdit Extension.  This class is responsible for
 * defining which menus will appear and which option panes will appear.  There
 * are also facilities for initialization and finalization code.
 *
 * @author Matthew Flower
 * @since 08/29/2002
 */
public class JImporterPlugin extends EditPlugin {
	/**
	 * The name method can be used as a prefix for any properties stored in a
	 * properties file or by jEdit.setProperty().
	 */
	public String NAME = "jimporter";
	/**
	 * All options are stored with the prefix "options.jimporter".  This variable makes
	 * that knowledge available throughout the class.
	 */
	public String OPTION_PREFIX = "options." + NAME + "." ;

	/**
	 * This method initializes the JImporter plugin.  Currently, it doesn't do any
	 * initialization in advance.
	 */
	public void start() {
	}

	/**
	 * This method could perform extra handling when the JImporter plugin was stopped.
	 * Right now it performs no special functions.
	 */
	public void stop() {
	}

	/**
	 * Add the JImporter menu items to JEdit.
	 *
	 * @param menuItems A vector of already existing menuItems that JImporter will add it's menu to.
	 */
/* 	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("plugin.jimporter.JImporterPlugin"));
	}
 */
	/**
	 * Create the JImporter options page that will be used to capture configuration
	 * information.
	 *
	 * @param dialog The dialog box to which JImporter will add it's
	 * configuration-capturing option pane.
	 */
/* 	public void createOptionPanes(OptionsDialog dialog) {
		OptionGroup og = new OptionGroup("jimporter");
		og.addOptionPane(new ClasspathOptionPane());
		og.addOptionPane(new SortingOptionPane());
		og.addOptionPane(new MiscellaneousOptionPane());

		dialog.addOptionGroup(og);
	}
 */}
