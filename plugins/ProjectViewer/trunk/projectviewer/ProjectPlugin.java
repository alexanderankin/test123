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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.msg.PluginUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import static org.gjt.sp.jedit.EditBus.EBHandler;

import org.gjt.sp.util.Log;

import projectviewer.vpt.VPTContextMenu;
import projectviewer.vpt.VPTNode;

import projectviewer.config.ExtensionManager;
import projectviewer.config.ProjectViewerConfig;
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
public final class ProjectPlugin extends EditPlugin {

	//{{{ Static Members
	private static File CONFIG_DIR;
	private static ProjectViewerConfig config;

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
	public static String getResourcePath(String path)
		throws FileNotFoundException
	{
		if (CONFIG_DIR != null) {
			File f = new File(CONFIG_DIR, path);
			File d = f.getParentFile();
			if (! d.exists())
				d.mkdirs();
			return f.getAbsolutePath();
		} else {
			throw new FileNotFoundException("No config directory.");
		}
	} //}}}

	//}}}

	//{{{ +start() : void
	/** Start the plugin. */
	public void start() {
		/*
		 * First, try to see if the new config directory exists.
		 * If it doesn't, try to move the old config directory to
		 * the new location.
		 */
		File configDir = getPluginHome();
		if (configDir == null) {
			Log.log(Log.WARNING, this,
					"ProjectViewer won't work without a settings directory. " +
					"Use this setup at your own risk.");
			return;
		}

		if (!configDir.isDirectory()) {
			File oldConfig = new File(jEdit.getSettingsDirectory(),
									  "projectviewer");
			if (oldConfig.isDirectory()) {
				File configParentDir = configDir.getParentFile();
				if ((!configParentDir.isDirectory()) && (!configParentDir.mkdirs())) {
					Log.log(Log.WARNING, this, "Cannot create plugin home dir.");
					configDir = oldConfig;
				} else if (!oldConfig.renameTo(configDir)) {
					Log.log(Log.WARNING, this, "Cannot move config directory.");
					configDir = oldConfig;
				}
			} else if (!configDir.mkdirs()) {
				Log.log(Log.ERROR, this, "Cannot create config directory; ProjectViewer will not function properly.");
			}

			/*
			 * When moving the settings settings, it most probably means that
			 * PV is being upgraded from 2.x to 3.0. Take the opportunity to
			 * clean up the file dialog geometry info in jEdit's config file
			 * so that the new dialog starts fresh and doesn't have any hidden
			 * controls because of being shown too small.
			 */
			jEdit.unsetProperty(projectviewer.gui.ImportDialog.class.getName() + ".height");
			jEdit.unsetProperty(projectviewer.gui.ImportDialog.class.getName() + ".width");
			jEdit.unsetProperty(projectviewer.gui.ImportDialog.class.getName() + ".x");
			jEdit.unsetProperty(projectviewer.gui.ImportDialog.class.getName() + ".y");
		}

		CONFIG_DIR = configDir;
		config = ProjectViewerConfig.getInstance();
		EditBus.addToBus(this);
 	} //}}}

	//{{{ +stop() : void
	/** Stop the plugin and save the project resources. */
	public void stop() {
		config.save();
		try {
			ProjectManager.getInstance().save();
			ProjectManager.getInstance().unload();
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
		// clean up edit bus
		View[] views = jEdit.getViews();
		for (int i = 0; i < views.length; i++) {
			ProjectViewer pv = ProjectViewer.getViewer(views[i]);
			if (pv != null) {
				pv.unload();
			}
		}
		EditBus.removeFromBus(this);
	} //}}}


	@EBHandler
	public void handleViewUpdate(ViewUpdate vu)
	{
		if (!viewActivated) {
			viewActivated = (vu.getWhat() == ViewUpdate.ACTIVATED);
			if (viewActivated) {
				ExtensionManager.getInstance().reloadExtensions();
			}
		}
		if (viewActivated) {
			if (vu.getWhat() == ViewUpdate.CREATED &&
				ProjectViewer.getViewer(vu.getView()) == null) {
				ProjectViewer.setActiveNode(vu.getView(), config.getLastNode());
			}
		}
		if (vu.getWhat() == ViewUpdate.CLOSED) {
			ProjectViewer.cleanViewEntry(vu.getView());
		}
	}


	@EBHandler
	public void handlePluginUpdate(PluginUpdate pu)
	{
		if (viewActivated) {
			ExtensionManager.getInstance().reloadExtensions();
		}
	}

	private boolean viewActivated = false;

}

