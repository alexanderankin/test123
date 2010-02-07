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
package projectviewer.gui;

import java.awt.Component;
import projectviewer.vpt.VPTNode;

/**
 *	Defines the interface for providing custom properties about a node.
 *	When showing the node properties dialog, a new tab will be created
 *	for each available provider.
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public interface NodePropertyProvider
{

	/**
	 *	Returns whether this provider can show information for the
	 *	given node. If the node is not supported, no tab for this
	 *	provides is created in the UI.
	 *
	 *	@param node The node.
	 *
	 *	@return Whether a tab should be shown for this provider.
	 */
	public boolean isNodeSupported(VPTNode node);


	/**
	 *	Returns the title of the tab containing this provider's
	 *	information.
	 */
	public String getTitle();


	/**
	 *	Returns the component containing the UI for the properties
	 *	of the given node. This method is only called when the
	 *	provider's tab is activated by the user, and is only called
	 *	once for each invocation of the dialog.
	 *
	 *	@param node The node.
	 *
	 *	@return A UI component to be shown in the properties dialog.
	 */
	public Component getComponent(VPTNode node);

}

