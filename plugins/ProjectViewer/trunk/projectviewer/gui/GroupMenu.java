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

//{{{ Imports
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import projectviewer.ProjectViewer;

import projectviewer.action.EditGroupAction;
import projectviewer.action.EditProjectAction;

import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.vpt.VPTRoot;
//}}}

/**
 *	A menu that provides a list of groups and, optionally, projects,
 *	letting the user choose them and execute some action when the
 *	item is selected.
 *
 *	<p>Users should call the {@link #populate(Container, VPTGroup, View) populate()}
 *	method when some update to the project/group tree has occurred, since this
 *	class does not take care of that automatically.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class GroupMenu extends JMenu implements ActionListener {

	//{{{ Private members
	private boolean showProjects;
	private boolean showCreate;
	private Object ignore;
	private ActionListener action;
	private ButtonGroup activeNodeGroup;
	//}}}

	//{{{ +GroupMenu(String, boolean, boolean, ActionListener) : <init>
	/**
	 *	A JMenu implementation that populates the items according to the
	 *	object tree under the root node of the projects (VPTRoot).
	 *
	 *	@param	label	The label for the menu (it's safe to pass null if
	 *					you're gonna use this to populate another container).
	 *	@param	showProjects	Whether to show projects in the menu or not.
	 *	@param	showCreate		Whether to show a "Create project here" entry
	 *							in the menu. If this entry is selected by the
	 *							user, a String will be the source of the
	 *							ActionEvent instead of a VPTNode.
	 *	@param	al		The ActionListener to call when a menu item is clicked.
	 */
	public GroupMenu(String label, boolean showProjects, boolean showCreate,
						ActionListener al) {
		super(label);
		this.action = al;
		this.showProjects = showProjects;
		this.showCreate = showCreate;
	} //}}}

	//{{{ +setIgnore(VPTNode) : void
	/**
	 *	Sets a node to be ignored when building the menu. If a group
	 *	under VPTRoot (or it's children) is equal to the given node,
	 *	it will not be added to the menu.
	 */
	public void setIgnore(VPTNode node) {
		this.ignore = node;
	} //}}}

	//{{{ +populate(Container, VPTGroup, View) : void
	public void populate(Container menu, VPTGroup grp, View v) {
		if (showProjects)
			activeNodeGroup = new ButtonGroup();
		populate(menu, grp, ProjectViewer.getActiveNode(v),
					JPopupMenu.Separator.class);
	} //}}}

	//{{{ -populate(Container, VPTGroup, VPTNode, Class) : void
	private void populate(Container menu, VPTGroup grp, VPTNode active, Class separator) {
		menu.removeAll();

		// add the "select this group" entry
		String label;
		JMenuItem mi;
		if (showProjects) {
			label = jEdit.getProperty("projectviewer.groupmenu.activate_group",
							new Object[] { grp.getName() });
			mi = new NodeRadioMenuItem(grp, label);
			if (grp == active)
				mi.setSelected(true);
		} else {
			label = jEdit.getProperty("projectviewer.groupmenu.select_group",
							new Object[] { grp.getName() });
			mi = new GroupMenuItem(grp, label);
		}

		mi.addActionListener(this);
		menu.add(mi);

		// add all the children
		boolean addedGrpSep = false;
		boolean addedPrjSep = false;
		for (int i = 0; i < grp.getChildCount(); i++) {
			VPTNode n = (VPTNode) grp.getChildAt(i);
			if (n != ignore && n.isGroup()) {
				if (!addedGrpSep) {
					try {
						menu.add((JSeparator)separator.newInstance());
					} catch (Exception e) {
						// not gonna happen
					}
					addedGrpSep = true;
				}
				JMenu sub = new JMenu(n.getName());
				populate(sub, (VPTGroup)n, active, separator);
				if (sub.getMenuComponentCount() == 1) {
					// instead of a menu, show a menu item.
					if (showProjects) {
						label = jEdit.getProperty("projectviewer.groupmenu.activate_group",
							new Object[] { n.getName() });
						mi = new NodeRadioMenuItem(n, label);
						activeNodeGroup.add(mi);
						if (n == active)
							mi.setSelected(true);
					} else {
						label = n.getName();
						mi = new GroupMenuItem((VPTGroup)n, label);
					}
					mi.addActionListener(this);
					menu.add(mi);
				} else {
					menu.add(sub);
				}
			} else if (showProjects && n.isProject()) {
				if (!addedPrjSep) {
					try {
						menu.add((JSeparator)separator.newInstance());
					} catch (Exception e) {
						// not gonna happen
					}
					addedPrjSep = true;
				}
				mi = new NodeRadioMenuItem(n, n.getName());
				activeNodeGroup.add(mi);
				if (n == active)
					mi.setSelected(true);
				mi.addActionListener(this);
				menu.add(mi);
			}
		}

		// add the "create group" if requested
		if (showCreate) {
			try {
				menu.add((JSeparator)separator.newInstance());
			} catch (Exception e) {
				// not gonna happen
			}

			label= jEdit.getProperty("projectviewer.groupmenu.create_project");
			mi = new GroupMenuItem(grp, label, true, false, false);
			mi.addActionListener(this);
			menu.add(mi);

			label= jEdit.getProperty("projectviewer.groupmenu.create_group");
			mi = new GroupMenuItem(grp, label, false, true, false);
			mi.addActionListener(this);
			menu.add(mi);

			if (grp != VPTRoot.getInstance()) {
				label= jEdit.getProperty("projectviewer.action.edit_group");
				mi = new GroupMenuItem(grp, label, false, false, true);
				mi.addActionListener(this);
				menu.add(mi);
			}
		}

	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		if (action != null) {
			Object o = ae.getSource();
			if (o instanceof NodeRadioMenuItem) {
				ae.setSource(((NodeRadioMenuItem)o).node);
				action.actionPerformed(ae);
			} else if (o instanceof GroupMenuItem) {
				GroupMenuItem g = (GroupMenuItem) o;
				if (g.isAddProject) {
					EditProjectAction epa = new EditProjectAction(true, g.node);
					epa.actionPerformed(null);
				} else if (g.isAddGroup || g.isEditGroup) {
					EditGroupAction ega =
						new EditGroupAction(g.isAddGroup, g.node,
								jEdit.getActiveView());
					ega.actionPerformed(null);
				} else {
					ae.setSource(g.node);
					action.actionPerformed(ae);
				}
			}
		}
	} //}}}

	//{{{ -class _GroupMenuItem_
	private static class GroupMenuItem extends JMenuItem {

		private VPTGroup node;
		private boolean isAddProject;
		private boolean isAddGroup;
		private boolean isEditGroup;

		//{{{ +GroupMenuItem(VPTGroup, String) : <init>
		public GroupMenuItem(VPTGroup node, String label) {
			this(node, label, false, false, false);
		} //}}}

		//{{{ +GroupMenuItem(VPTGroup, String, boolean, boolean, boolean) : <init>
		public GroupMenuItem(VPTGroup node,
								String label,
								boolean isAddProject,
								boolean isAddGroup,
								boolean isEditGroup) {
			super(label);
			this.node = node;
			this.isAddProject = isAddProject;
			this.isAddGroup = isAddGroup;
			this.isEditGroup = isEditGroup;
		} //}}}

	} //}}}

	//{{{ -class _NodeRadioMenuItem_
	private static class NodeRadioMenuItem extends JRadioButtonMenuItem {

		private VPTNode node;

		//{{{ +NodeRadioMenuItem(VPTNode, String) : <init>
		public NodeRadioMenuItem(VPTNode node, String label) {
			super(label);
			this.node = node;
		} //}}}

	} //}}}

}

