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
package projectviewer.action;

import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.jEdit;

import projectviewer.gui.NodePropertiesDialog;
import projectviewer.vpt.VPTNode;

/**
 *	Shows a dialog with properties for the selected node.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public class NodePropertiesAction extends Action
{

	public String getText() {
		return jEdit.getProperty("projectviewer.action.nodeproperties");
	}

	/** Disable action for multiple selection. */
	public void prepareForNode(VPTNode node)
	{
		cmItem.setVisible(node != null);
	}

	/** Shows the property dialog. */
	public void actionPerformed(ActionEvent e)
	{
		NodePropertiesDialog dlg;

		dlg = new NodePropertiesDialog(viewer.getSelectedNode(),
									   viewer);
		dlg.setVisible(true);
	}

}

