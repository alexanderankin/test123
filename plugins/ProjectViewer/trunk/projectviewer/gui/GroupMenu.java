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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import projectviewer.ProjectViewer;
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
 *	<p>This menu should be created every time it needs to be used, since
 *	it will get information from VPTRoot and will not be updated after
 *	creation of the instance.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.0
 */
public class GroupMenu extends JMenu implements ActionListener {

	//{{{ Private members
	private boolean showProjects;
	private Object ignore;
	private ActionListener action;
	private ButtonGroup activeNodeGrop;
	//}}}

	//{{{ +GroupMenu(String, boolean, ActionListener) : <init>
	/**
	 *	Builds the menu: The options have the following effect:
	 *
	 *	<ul>
	 *		<li>showProjects: if true, will show projecs also, not only
	 *		groups. Projects will be shown as radio buttons, and all of
	 *		them will be part of the same button group. The current
	 *		selected node in the given view, if any, will be shown
	 *		as being selected. When this is true, the group selectors
	 *		will also be shown as radio buttons.</li>
	 *
	 *		<li>ignore: if not null, the group that is equal to the given
	 *		object will not be shown.</li>
	 *
	 *		<li>al: the ActionListener to be added to each menu item. It's
	 *		not actually going to be added to the menu items; this class will
	 *		process the event and modify the source to be the actual VPTNode
	 *		instance instead of the JMenuItem/JRadioButtonMenuItem, and
	 *		then feed it to the given action listener..</li>
	 *	</ul>
	 *
	 *	@param	label	The label for the menu.
	 *	@param	v		The view where the menu will be show, used to define
	 *					the active project.
	 */
	public GroupMenu(String label, boolean showProjects, ActionListener al) {
		super(label);
		this.action = al;
		this.showProjects = showProjects;
		if (showProjects)
			activeNodeGrop = new ButtonGroup();
	} //}}}

	//{{{ +GroupMenu(String, Object, View, ActionListener) : <init>
	public GroupMenu(String label, Object ignore, View v, ActionListener al) {
		this(label, false, al);
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

	//{{{ +populate(JMenu, VPTGroup, View) : void
	/**
	 *	This method needs to be called before the menu is shown. It clears
	 *	the menu and re-populates the entries based on what's under the
	 *	VPTRoot instance.
	 */
	public void populate(JMenu menu, VPTGroup grp, View v) {
		populate(menu, grp, ProjectViewer.getActiveNode(v));
	} //}}}

	//{{{ -populate(JMenu, VPTGroup, VPTNode) : void
	private void populate(JMenu menu, VPTGroup grp, VPTNode active) {
		menu.removeAll();

		// add the "select this group" entry
		String label;
		JMenuItem mi;
		if (showProjects) {
			label = jEdit.getProperty("projectviewer.groupmenu.activate_group",
							new Object[] { grp.getName() });
			mi = new GroupRadioMenuItem(grp, label);
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
					menu.addSeparator();
					addedGrpSep = true;
				}
				JMenu sub = new JMenu(n.getName());
				populate(sub, (VPTGroup)n, active);
				if (sub.getMenuComponentCount() == 1) {
					// instead of a menu, show a menu item.
					if (showProjects) {
						label = jEdit.getProperty("projectviewer.groupmenu.activate_group",
							new Object[] { n.getName() });
						mi = new GroupRadioMenuItem(n, label);
						activeNodeGrop.add(mi);
						if (n == active)
							mi.setSelected(true);
					} else {
						label = n.getName();
						mi = new GroupMenuItem(n, label);
					}
					mi.addActionListener(this);
					menu.add(mi);
				} else {
					menu.add(sub);
				}
			} else if (showProjects && n.isProject()) {
				if (!addedPrjSep) {
					menu.addSeparator();
					addedPrjSep = true;
				}
				mi = new GroupRadioMenuItem(n, n.getName());
				activeNodeGrop.add(mi);
				if (n == active)
					mi.setSelected(true);
				mi.addActionListener(this);
				menu.add(mi);
			}
		}
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		if (action != null) {
			Object o = ae.getSource();
			VPTNode sel = null;
			if (o instanceof GroupMenuItem)
				sel = ((GroupMenuItem)o).node;
			else if (o instanceof GroupRadioMenuItem)
				sel = ((GroupRadioMenuItem)o).node;
			ae.setSource(sel);
			action.actionPerformed(ae);
		}
	} //}}}

	//{{{ -class GroupMenuItem
	private class GroupMenuItem extends JMenuItem {

		private VPTNode node;

		//{{{ +GroupMenuItem(VPTNode, String) : <init>
		public GroupMenuItem(VPTNode node, String label) {
			super(label);
			this.node = node;
		} //}}}

	} //}}}

	//{{{ -class GroupRadioMenuItem
	private class GroupRadioMenuItem extends JRadioButtonMenuItem {

		private VPTNode node;

		//{{{ +GroupRadioMenuItem(VPTNode, String) : <init>
		public GroupRadioMenuItem(VPTNode node, String label) {
			super(label);
			this.node = node;
		} //}}}

	} //}}}

}

