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

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.gui.OptionsDialog;

import org.gjt.sp.util.Log;

import projectviewer.vpt.VPTContextMenu;

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
 *  @version	2.0.3
 */
public final class ProjectPlugin extends EBPlugin {

	//{{{ Static Members
	public final static String NAME = "projectviewer";

	private final static ProjectViewerConfig config = ProjectViewerConfig.getInstance();

	//{{{ +_getResourceAsStream(String)_ : InputStream
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

	//{{{ +_getResourceAsOutputStream(String)_ : OutputStream
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

	//{{{ +_getResourcePath(String)_ : String
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

	//{{{ +start() : void
	/** Start the plugin. */
	public void start() {
		File f = new File(getResourcePath("projects/null"));
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
 	} //}}}

	//{{{ +stop() : void
	/** Stop the plugin and save the project resources. */
	public void stop() {
		config.save();
		try {
			ProjectManager.getInstance().save();
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
	} //}}}

	//{{{ +createMenuItems(Vector) : void
	/**
	 *	Create the appropriate menu items for this plugin.
	 *
	 *	@param  menuItems  The list of menuItems from jEdit.
	 */
	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("projectviewer.menu"));
	} //}}}

	//{{{ +createOptionPanes(OptionsDialog) : void
	/** Add the configuration option panes to jEdit's option dialog. */
	public void createOptionPanes(OptionsDialog optionsDialog) {
		OptionGroup optionGroup = new OptionGroup(NAME);
		optionGroup.addOptionPane(new ProjectViewerOptionsPane("projectviewer.optiongroup.main_options"));
		optionGroup.addOptionPane(new ContextOptionPane("projectviewer.optiongroup.context_menu"));
		optionGroup.addOptionPane(new ProjectAppConfigPane("projectviewer.optiongroup.external_apps"));
		optionsDialog.addOptionGroup(optionGroup);
	} //}}}

	//{{{ +handleMessage(EBMessage) : void
	/** Handles plugin load/unload messages in the EditBus. */
	public void handleMessage(EBMessage msg) {
		if (config.isJEdit42()) {
			Helper.checkPluginUpdate(msg);
		}
	} //}}}

	//{{{ -class _Helper_
	/**
	 *	Class to hold methods that require classes that may not be available,
	 *	so that PV behaves well when called from a BeanShell script.
	 */
	private static class Helper {

		//{{{ +_checkPluginUpdate(EBMessage)_ : void
		public static void checkPluginUpdate(EBMessage msg) {
			if (msg instanceof PluginUpdate) {
				PluginUpdate pu = (PluginUpdate) msg;
				if (pu.getWhat() == PluginUpdate.LOADED) {
					ProjectViewer.SHelper.addProjectViewerListeners(pu.getPluginJAR(), null);
					ProjectManager.getInstance().addProjectListeners(pu.getPluginJAR());
					ProjectViewer.SHelper.addToolbarActions(pu.getPluginJAR());
					VPTContextMenu.Helper.registerActions(pu.getPluginJAR());

					View[] v = jEdit.getViews();
					for (int i = 0; i < v.length; i++) {
						if (ProjectViewer.getViewer(v[i]) != null) {
							ProjectViewer.SHelper.addProjectViewerListeners(pu.getPluginJAR(), v[i]);
						}
					}
				} else if (pu.getWhat() == PluginUpdate.UNLOADED && !pu.isExiting()) {
					ProjectViewer.SHelper.removeProjectViewerListeners(pu.getPluginJAR());
					ProjectManager.getInstance().removeProjectListeners(pu.getPluginJAR());
					ProjectViewer.SHelper.removeToolbarActions(pu.getPluginJAR());
					VPTContextMenu.Helper.unregisterActions(pu.getPluginJAR());
				}
			}
		} //}}}

	} //}}}

}


