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
package projectviewer.config;

import javax.swing.Icon;
import org.gjt.sp.jedit.io.VFSFile;

import projectviewer.importer.ImporterFileFilter;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;


/**
 * Defines the service for providing version control information for
 * a project. Plugins implementing this service should provide it
 * through jEdit's service.xml interface.
 *
 * The interface extends {@link OptionsService}; the option pane or
 * option group returned by this service is only shown if the service
 * is chosen as the "version control service" for a project.
 *
 * @author	Marcelo Vanzin
 * @since	PV 3.0.0
 * @version	$Id$
 */

public interface VersionControlService extends OptionsService
{
	/** Key in the project's properties identifying the version control service. */
	public static final String VC_SERVICE_KEY = "projectviewer.version_control_service";

	/** Special value designating that no special status is available for the node. */
	public static final int VC_STATUS_NORMAL = 0;

	/**
	 * This method should return an integer identifying the current
	 * state of the given node.
	 *
	 * This method will be called every time the tree node needs
	 * to be repainted, so it shouldn't take long to return. It's
	 * extremely encouraged that implementations do some sort of caching
	 * to make this method return quickly.
	 *
	 * @param	node		The node.
	 *
	 * @return A service-specific identifier for the file state, or
	 *         VC_STATUS_NORMAL.
	 */
	public int getNodeState(VPTNode node);


	/**
	 * This should return the status icon to be used to represent the
	 * given state.
	 *
	 * @param	state	The value retrieved from {@link #getNodeState(VPTNode)}.
	 *
	 * @return The icon for the given state, or null for no icon.
	 */
	public Icon getIcon(int state);


	/**
	 * Returns the class identifying the plugin. This is used to check
	 * whether there are version control-specific option panes / groups
	 * to be added to a project's option dialog.
	 *
	 * @return The main plugin class for this service.
	 */
	public Class getPlugin();


	/**
	 * Returns a file filter to be shown as an option when the user
	 * imports files into a project backed by this version control
	 * service.
	 *
	 * @return An ImporterFileFilter, or null if there's no specific
	 *         filter for the service.
	 */
	public ImporterFileFilter getFilter();


	/**
	 * Called when a user removes the version control association with a
	 * project (either by not choosing a version control service or a
	 * different one). This allows the service to clean up any
	 * configuration data associated with the service from the project's
	 * properties.
	 *
	 * @param	proj	The project.
	 */
	public void dissociate(VPTProject proj);

}

