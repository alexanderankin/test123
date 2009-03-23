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

//{{{ Imports
import java.util.List;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.gui.OptionsDialog;

import projectviewer.vpt.VPTGroup;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *  A dialog for configuring the properties of a project. It works like jEdit's
 *	OptionsDialog (from which this class extends) to provide ways for other
 *	plugins to add option panes to it.
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectOptions extends OptionsDialog {

	//{{{ Static Members
	private static String			lookupPath;
	private static VPTProject		p;
	private static boolean			isNew;

	//{{{ +_run(VPTProject)_ : VPTProject
	/**
	 *	Shows the project options dialog for the given project.
	 *
	 *	@param	project	The project to edit or null to create a new one.
	 *	@return	The new or modified project, or null if p was null and
	 *			dialog was cancelled.
	 */
	public static VPTProject run(VPTProject project) {
		return run(project, null, null);
	} //}}}

	//{{{ +_run(VPTProject, VPTGroup, String)_ : VPTProject
	/**
	 *	Shows the project options dialog for the given project, with an
	 *	optional default start folder where to open the file chooser
	 *	dialog.
	 *
	 *	<p>Method is sychronized so that the use of the static variables
	 *	is safe.</p>
	 *
	 *	@param	project	The project to edit or null to create a new one.
	 *  @param	parent	If creating a new project, the parent where the
	 *					project should be added (null is ok).
	 *	@param	startPath	Where to open the "choose root" file dialog.
	 *	@return	The new or modified project, or null if p was null and
	 *			dialog was cancelled.
	 */
	public static VPTProject run(VPTProject project,
								 VPTGroup parent,
								 String startPath)
	{
		return run(project, parent, startPath, null);
	} //}}}

	//{{{ +_run(VPTProject, VPTGroup, String, String)_ : VPTProject
	/**
	 *	Shows the project options dialog for the given project, with an
	 *	optional default start folder where to open the file chooser
	 *	dialog.
	 *
	 *	<p>Method is sychronized so that the use of the static variables
	 *	is safe.</p>
	 *
	 *	@param	project	The project to edit or null to create a new one.
	 *  @param	parent	If creating a new project, the parent where the
	 *					project should be added (null is ok).
	 *	@param	startPath	Where to open the "choose root" file dialog.
	 *	@param	startPane	The name of the option pane to be shown by
	 *						default. If null, will show the main project
	 *						options pane.
	 *	@return	The new or modified project, or null if p was null and
	 *			dialog was cancelled.
	 *	@since	PV 2.1.3.4
	 */
	public static synchronized VPTProject run(VPTProject project,
												VPTGroup parent,
												String startPath,
												String startPane)
	{
		String title;
		if (project == null) {
			p = new VPTProject("");
			p.setParent(parent);
			p.setRootPath("");
			isNew = true;
			title = "projectviewer.create_project";
		} else {
			p = project;
			isNew = false;
			title = "projectviewer.edit_project";
		}

		lookupPath = startPath;
		new ProjectOptions(jEdit.getActiveView(), title, startPane);
		if (isNew && p != null) {
			p.setParent(null);
		}
		project = p;
		p = null;
		return project;
	} //}}}

	//{{{ +_getProject()_ : VPTProject
	/**
	 *	Returns the project currently being edited, or null if the
	 *	dialog is not active.
	 *
	 *	@since	PV 2.1.0.1
	 */
	public static VPTProject getProject() {
		return p;
	} //}}}

	//}}}

	//{{{ Instance Variables

	private PVOptionTreeModel		paneModel;
	private ProjectPropertiesPane	pOptPane;

	//}}}

	//{{{ -ProjectOptions(View, String) : <init>
	private ProjectOptions(View view, String name, String pane) {
		super(JOptionPane.getFrameForComponent(view), name, pane);
		setModal(true);
	} //}}}

	public void setTitle(String title) {
		if (!isNew) {
			super.setTitle(title + " (" + p.getName() + ")");
		} else {
			super.setTitle(title);
		}
	}

	//{{{ +cancel() : void
	/**
	 *	Called when the cancel button is pressed. Sets the project to null
	 *	if "isNew" is true.
	 */
	public void cancel() {
		p = null;
		dispose();
	} //}}}

	//{{{ +ok() : void
	/**
	 *	Called when ok is pressed. Verifies if the project's properties are OK
	 *	before closing the dialog.
	 */
	public void ok() {
		super.ok(false);
		if (pOptPane.isOK()) {
			dispose();
		}
	} //}}}

	//{{{ #getDefaultGroup() : OptionGroup
	protected OptionGroup getDefaultGroup() {
		return (OptionGroup) paneModel.getRoot();
	} //}}}

	//{{{ #createOptionTreeModel() : OptionTreeModel
	protected OptionTreeModel createOptionTreeModel() {
		paneModel = new PVOptionTreeModel();

		pOptPane = new ProjectPropertiesPane(this, p, isNew, lookupPath);
		addOptionPane(pOptPane);

		addOptionPane(new AutoReimportPane(p));
		addOptionPane(new ProjectFilterPane());

		EditPlugin[] eplugins = jEdit.getPlugins();
		for (int i = 0; i < eplugins.length; i++) {
			addRemoveOptions(eplugins[i].getClassName(),
							 "option-pane",
							 "option-group",
							 true);
		}

		return paneModel;
	} //}}}


	void removeOptions(VersionControlService svc)
	{
		PVOptionGroup root = (PVOptionGroup) paneModel.getRoot();
		addRemoveOptions(svc.getPlugin().getName(),
						 "vc-option-pane",
						 "vc-option-group",
						 false);
	}


	void addOptions(VersionControlService svc)
	{
		addRemoveOptions(svc.getPlugin().getName(),
						 "vc-option-pane",
						 "vc-option-group",
						 true);
	}


	private void addRemoveOptions(String plugin,
								  String pane,
								  String group,
								  boolean add)
	{
		PVOptionGroup root = (PVOptionGroup) paneModel.getRoot();

		// Look for a single option pane
		String property = "plugin.projectviewer." + plugin + "." + pane;
		if ((property = jEdit.getProperty(property)) != null) {
			if (add) {
				root.addOptionPane(property);
				paneModel.addRemoveMember(root, property, true);
			} else {
				paneModel.addRemoveMember(root, property, false);
			}
		}

		// Look for an option group
		property = "plugin.projectviewer." + plugin + "." + group;
		if ((property = jEdit.getProperty(property)) != null) {
			if (add) {
				OptionGroup newgroup =
					new OptionGroup("plugin." + plugin,
						jEdit.getProperty("plugin." + plugin + ".name"),
						property);
				root.addOptionGroup(newgroup);
				paneModel.addRemoveMember(root, newgroup.getLabel(), true);
			} else {
				String label = jEdit.getProperty(property + ".label");
				paneModel.addRemoveMember(root, label, false);
			}
		}
	}

	private class PVOptionGroup extends OptionGroup
	{

		PVOptionGroup()
		{
			super(null);
		}


		int getIndexByName(String name)
		{
			for (int i = 0; i < members.size(); i++) {
				Object o = members.get(i);
				if (o instanceof String) {
					if (name.equals((String)o)) {
						return i;
					}
				} else if (o instanceof OptionGroup) {
					if (name.equals(((OptionGroup)o).getLabel())) {
						return i;
					}
				} else if (o instanceof AbstractOptionPane) {
					if (name.equals(((AbstractOptionPane)o).getName())) {
						return i;
					}
				} else {
					throw new InternalError();
				}
			}
			return -1;
		}

		void remove(int idx)
		{
			members.remove(idx);
		}

	}

	private class PVOptionTreeModel extends OptionTreeModel
	{

		PVOptionTreeModel()
		{
			super(new PVOptionGroup());
		}


		void addRemoveMember(PVOptionGroup grp,
							 String member,
							 boolean add)
		{
			int idx = grp.getIndexByName(member);
			if (idx == -1) {
				return;
			}

			Object[] path = new Object[] { grp };
			int[] indices = new int[] { idx };
			Object[] children = new Object[] { grp.getMember(idx) };
			if (add) {
				paneModel.fireNodesInserted(this, path, indices, children);
			} else {
				paneModel.fireNodesRemoved(this, path, indices, children);
				grp.remove(idx);
			}
		}

	}

}

