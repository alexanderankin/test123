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
 * GNU General Public License for more details.
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
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
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
import projectviewer.persist.ProjectPersistenceManager;
//}}}

/**
 *  A Project Viewer plugin for jEdit.
 *
 *  @author		<A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 *  @author		<A HREF="mailto:cyu77@yahoo.com">Calvin Yu</A>
 *  @author		<A HREF="mailto:ensonic@sonicpulse.de">Stefan Kost</A>
 *  @author		<A HREF="mailto:webmaster@sutternow.com">Matthew Payne</A>
 *	@author		<A HREF="mailto:vanza@users.sourceforge.net">Marcelo Vanzin</A>
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
		// check plugins that are already loaded
		EditPlugin[] plugins = jEdit.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			if (!(plugins[i] instanceof EditPlugin.Deferred)) {
				// create a "fake" PluginUpdate message
				PluginUpdate msg =
					new PluginUpdate(plugins[i].getPluginJAR(),
										PluginUpdate.LOADED,
										false);
				checkPluginUpdate(msg);
			}
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
		// clean up edit bus
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++) {
			ProjectViewer pv = ProjectViewer.getViewer(views[i]);
			if (pv != null) {
				EditBus.removeFromBus(pv);
			}
		}
	} //}}}

	//{{{ +handleMessage(EBMessage) : void
	/** Handles plugin load/unload messages in the EditBus. */
	public void handleMessage(EBMessage msg) {
		if (msg instanceof PluginUpdate) {
			checkPluginUpdate((PluginUpdate)msg);
		}
	} //}}}

	//{{{ -checkPluginUpdate(PluginUpdate) : void
	private void checkPluginUpdate(PluginUpdate msg) {
		if (msg.getWhat() == PluginUpdate.LOADED) {
			ProjectViewer.addProjectViewerListeners(msg.getPluginJAR(), null);
			ProjectManager.getInstance().addProjectListeners(msg.getPluginJAR());
			ProjectViewer.addToolbarActions(msg.getPluginJAR());
			VPTContextMenu.registerActions(msg.getPluginJAR());
			ProjectPersistenceManager.loadNodeHandlers(msg.getPluginJAR());

			View[] v = jEdit.getViews();
			for (int i = 0; i < v.length; i++) {
				if (ProjectViewer.getViewer(v[i]) != null) {
					ProjectViewer.addProjectViewerListeners(msg.getPluginJAR(), v[i]);
				}
			}
		} else if (msg.getWhat() == PluginUpdate.UNLOADED && !msg.isExiting()) {
			ProjectViewer.removeProjectViewerListeners(msg.getPluginJAR());
			ProjectManager.getInstance().removeProjectListeners(msg.getPluginJAR());
			ProjectViewer.removeToolbarActions(msg.getPluginJAR());
			VPTContextMenu.unregisterActions(msg.getPluginJAR());
		}
	} //}}}

}

