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

//{{{ Imports
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Enumeration;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.search.SearchDialog;
import org.gjt.sp.jedit.search.SearchFileSet;
import org.gjt.sp.jedit.search.SearchAndReplace;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import projectviewer.persist.ProjectZipper;
//}}}

/**
 *	Opens the search dialog for the selected directory/project.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ArchiveAction extends Action {
	
	//{{{ getText() method
	/** Returns the text to be shown on the button and/or menu item. */
	public String getText() {
		return jEdit.getProperty("projectviewer.launcher.archive");
	} //}}}
	
	//{{{ getIcon() method
	/** Returns null. Shouldn't be on the toolbar. */
	public Icon getIcon() {
		return null;
	} //}}}
	
	//{{{ actionPerformed(ActionEvent) method
	/** Creates a new project. */
	public void actionPerformed(ActionEvent e) {
		VPTNode node = viewer.getSelectedNode();
		if (node == null || node.isRoot()) return;

		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(
			new FileFilter() {
				public String getDescription() {
					return "Archive File Types";
				}

				public boolean accept(File f) {
					String name = f.getName();
					if (name.endsWith(".zip")) return true;
					else if (name.endsWith(".jar")) return true;
					else if (name.endsWith(".war")) return true;
					else if (name.endsWith(".ear")) return true;
					else if (f.isDirectory()) return true;
					return false;
				}
			});
		
		while (!node.isProject()) {
			node = (VPTNode) node.getParent();
		}
		VPTProject project = (VPTProject) node;

		chooser.setCurrentDirectory(new File (project.getRootPath())); 
		
 	   if (chooser.showSaveDialog(viewer) != javax.swing.JFileChooser.APPROVE_OPTION) {
			return;
		}

		
	    ProjectZipper pz = new ProjectZipper();
		pz.createProjectAchive(new File (chooser.getSelectedFile().getAbsolutePath()), 	
								project.getFiles().iterator(),
								project.getRootPath()); 
	} //}}}

	//{{{ prepareForNode(VPTNode) method
	/** Enable action only for the root node. */
	public void prepareForNode(VPTNode node) {
		cmItem.setVisible(node.isProject());
	} //}}}
	
}

