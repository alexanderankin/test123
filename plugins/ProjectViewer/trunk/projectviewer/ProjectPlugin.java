/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

//{{{ Imports
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.OptionsDialog;

import org.gjt.sp.util.Log;

import projectviewer.config.ContextOptionPane;
import projectviewer.config.ProjectViewerConfig;
import projectviewer.config.ProjectAppConfigPane;
import projectviewer.config.ProjectViewerOptionsPane;
//}}}

/**
 *  A Project Viewer plugin for jEdit.
 *
 *  @author		<A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 *  @author		<A HREF="mailto:cyu77@yahoo.com">Calvin Yu</A>
 *  @author		<A HREF="mailto:ensonic@sonicpulse.de">Stefan Kost</A>
 *  @author		<A HREF="mailto:webmaster@sutternow.com">Matthew Payne</A>
 *	@author		<A HREF="mailto:vanzin@ece.utexas.edu">Marcelo Vanzin</A>
 *  @version	2.0.0
 */
public final class ProjectPlugin extends EditPlugin {

	//{{{ Static Members
	public final static String NAME = "projectviewer";

	private final static ProjectViewerConfig config = ProjectViewerConfig.getInstance();

	//{{{ getResourceAsStream(String) method
	/**
	 *	Returns an input stream to the specified resource, or <code>null</code>
	 *	if none is found.
	 *
	 *	@param		path	The path to the resource to be returned, relative to
	 *						the plugin's resource path.
	 *	@return		An input stream for the resource.
	 */
	public static InputStream getResourceAsStream(String path) {
		try {
			return new FileInputStream(getResourcePath(path));
		} catch (IOException e) {
			return null;
		}
	} //}}}

	//{{{ getResourceAsOutputStream(String) method
	/**
	 *	Returns an output stream to the specified resource, or <code>null</node> if access
	 *	to that resource is denied.
	 *
	 *	@param		path	The path to the resource to be returned, relative to
	 *						the plugin's resource path.
	 *	@return		An output stream for the resource.
	*/
	public static OutputStream getResourceAsOutputStream(String path) {
		try {
			return new FileOutputStream(getResourcePath(path));
		} catch (IOException e) {
			return null;
		}
	} //}}}

	//{{{ getResourcePath(String) method
	/**
     *	Returns the full path of the specified plugin resource.
	 *
	 *	@param  	path	The relative path to the resource from the plugin's
	 *						resource path.
	 *	@return		The absolute path to the resource.
	 */
	public static String getResourcePath(String path) {
		return jEdit.getSettingsDirectory()
					+ File.separator + NAME
					+ File.separator + path;

	} //}}}

	//}}}

	//{{{ start() method
	/** Start the plugin. */
	public void start() {
        System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory","org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		File f = new File(getResourcePath("projects/null"));
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
 	} //}}}

	//{{{ stop() method
	/** Stop the plugin and save the project resources. */
	public void stop() {
		config.save();
		try {
			ProjectManager.getInstance().save();
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
	} //}}}

	//{{{ createMenuItems(Vector) method
	/**
	 *	Create the appropriate menu items for this plugin.
	 *
	 *	@param  menuItems  The list of menuItems from jEdit.
	 */
	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("projectviewer.menu"));
	} //}}}

	//{{{ createOptionPanes(OptionsDialog) method
	/** Add the configuration option panes to jEdit's option dialog. */
	public void createOptionPanes(OptionsDialog optionsDialog) {
		OptionGroup optionGroup = new OptionGroup(NAME);
		optionGroup.addOptionPane(new ProjectViewerOptionsPane("projectviewer.mainconfig"));
		optionGroup.addOptionPane(new ContextOptionPane("projectviewer.context"));
		optionGroup.addOptionPane(new ProjectAppConfigPane("projectviewer.appconfig"));
		optionsDialog.addOptionGroup(optionGroup);
	} //}}}

}

