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
package pvdebug;

//{{{ Imports
import java.util.List;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;

import org.gjt.sp.util.Log;

import projectviewer.event.ProjectUpdate;
import projectviewer.event.StructureUpdate;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.VPTFile;
//}}}

/**
 *  A debug plugin for ProjectViewer.
 *
 *	@author		Marcelo Vanzin
 *  @version	$Id$
 */
public final class PVDebugPlugin extends EBPlugin {

	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof ProjectUpdate) {
			handleProjectUpdate((ProjectUpdate) msg);
		} else if (msg instanceof StructureUpdate) {
			handleStructureUpdate((StructureUpdate) msg);
		} else if (msg instanceof ViewerUpdate) {
			handleViewerUpdate((ViewerUpdate) msg);
		}
	}

	private void handleProjectUpdate(ProjectUpdate msg)
	{
		System.err.println("========================================");
		System.err.println("Event:   " + msg.getClass().getName());
		System.err.println("Type:    " + msg.getType());

		if (msg.getType() == ProjectUpdate.Type.FILES_CHANGED) {
			List<VPTFile> added = msg.getAddedFiles();
			List<VPTFile> removed = msg.getRemovedFiles();

			if (added != null)
			for (VPTFile f : added) {
				System.err.println("Added:   " + f.getNodePath());
			}

			if (removed != null)
			for (VPTFile f : removed) {
				System.err.println("Removed: " + f.getNodePath());
			}
		}

	}

	private void handleStructureUpdate(StructureUpdate msg)
	{
		System.err.println("========================================");
		System.err.println("Event:   " + msg.getClass().getName());
		System.err.println("Type:    " + msg.getType());
		System.err.println("Node:    " + msg.getNode());
		System.err.println("Parent:  " + msg.getOldParent());
	}

	private void handleViewerUpdate(ViewerUpdate msg)
	{
		System.err.println("========================================");
		System.err.println("Event:   " + msg.getClass().getName());
		System.err.println("Type:    " + msg.getType());
		System.err.println("Node:    " + msg.getNode());
	}

}

