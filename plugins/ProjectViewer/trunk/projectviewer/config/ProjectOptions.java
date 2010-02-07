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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.ServiceManager;
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
public class ProjectOptions
{

	private VPTProject				p;
	private boolean					isNew;

	private ProjectOptionsDialog.PVOptionTreeModel	paneModel;
	private ProjectPropertiesPane	pOptPane;

	private Map<OptionsService, OptionGroup>	groups;
	private Map<OptionsService, OptionPane>		panes;


	/**
	 * Shows the project options dialog for the given project.
	 *
	 * @param	proj		The project to edit (null to create a new one).
	 * @param	isNew		If a non-null project is provided, whether to
	 *						treat is as a new project.
	 * @param	parent		The parent node where to insert new projects;
	 *						may be null.
	 *
	 * @return The project with updated information, or null if the user
	 *         cancelled the dialog.
	 *
	 * @since	PV 3.0.0
	 */
	public static VPTProject run(VPTProject proj,
								 boolean isNew,
								 VPTGroup parent)
	{
		ProjectOptions dlg = new ProjectOptions(proj, isNew, parent);
		return dlg.run();
	}


	private ProjectOptions(VPTProject proj,
						   boolean isNew,
						   VPTGroup parent)
	{
		this.p = proj;
		this.isNew = isNew;
		if (p == null) {
			this.isNew = true;
			p = new VPTProject("");
			p.setRootPath("");
		}
		if (p.getParent() == null) {
			p.setParent(parent);
		}
	}


	private VPTProject run()
	{
		String title;
		title = (isNew) ? "projectviewer.create_project"
		                : "projectviewer.edit_project";

		new ProjectOptionsDialog(jEdit.getActiveView(), title);
		if (isNew && p != null) {
			p.setParent(null);
		}
		return p;
	}


	void removeOptions(VersionControlService svc)
	{
		addRemoveOptions(svc, false);
	}


	void addOptions(VersionControlService svc)
	{
		addRemoveOptions(svc, true);
	}


	private void addRemoveOptions(OptionsService svc,
								  boolean add)
	{
		ProjectOptionsDialog.PVOptionGroup root =
			(ProjectOptionsDialog.PVOptionGroup) paneModel.getRoot();
		OptionPane pane;
		OptionGroup group;

		// Single option pane.
		if (add) {
			if ((pane = panes.get(svc)) == null) {
				pane = svc.getOptionPane(p);
			}
			if (pane != null) {
				root.addOptionPane(pane);
				panes.put(svc, pane);
				paneModel.addRemoveMember(root, pane, true);
			}
		} else {
			pane = panes.get(svc);
			if (pane != null) {
				paneModel.addRemoveMember(root, pane, false);
			}
		}

		// Option group;
		if (add) {
			if ((group = groups.get(svc)) == null) {
				group = svc.getOptionGroup(p);
			}
			if (group != null) {
				root.addOptionGroup(group);
				paneModel.addRemoveMember(root, group, true);
				groups.put(svc, group);
			}
		} else {
			group = groups.get(svc);
			if (group != null) {
				paneModel.addRemoveMember(root, group, false);
			}
		}
	}


	private class ProjectOptionsDialog extends OptionsDialog
	{

		private ProjectOptionsDialog(View view,
									 String name)
		{
			super(JOptionPane.getFrameForComponent(view), name, null);
		}


		public void setTitle(String title)
		{
			if (p.getName() != null && p.getName().length() > 0) {
				super.setTitle(title + " (" + p.getName() + ")");
			}
		}


		/**
		 *	Called when the cancel button is pressed. Sets the project to null
		 *	if "isNew" is true.
		 */
		public void cancel() {
			p = null;
			dispose();
		}


		/**
		 *	Called when ok is pressed. Verifies if the project's properties are OK
		 *	before closing the dialog.
		 */
		public void ok()
		{
			super.ok(false);
			if (pOptPane.isOK()) {
				dispose();
			}
		}


		protected OptionGroup getDefaultGroup()
		{
			return (OptionGroup) paneModel.getRoot();
		}


		protected OptionTreeModel createOptionTreeModel()
		{
			ProjectViewerConfig cfg = ProjectViewerConfig.getInstance();
			PVOptionGroup root;

			paneModel = new PVOptionTreeModel();
			root = (PVOptionGroup) paneModel.getRoot();

			pOptPane = new ProjectPropertiesPane(ProjectOptions.this,
												 p, isNew);
			addOptionPane(pOptPane);

			addOptionPane(new AutoReimportPane(p));
			addOptionPane(new ProjectFilterPane(p));

			groups = new HashMap<OptionsService, OptionGroup>();
			panes = new HashMap<OptionsService, OptionPane>();

			String[] popts;
			String type = OptionsService.class.getName();
			popts = ServiceManager.getServiceNames(type);
			for (String svcname : popts) {
				OptionsService svc = (OptionsService)
					ServiceManager.getService(type, svcname);
				if (cfg.isExtensionEnabled(type, svc.getClass().getName())) {
					addRemoveOptions(svc, true);
				}
			}

			return paneModel;
		}


		private class PVOptionGroup extends OptionGroup
		{

			PVOptionGroup()
			{
				super(null);
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
								 Object member,
								 boolean add)
			{
				int idx = grp.getMemberIndex(member);
				if (idx == -1) {
					return;
				}

				Object[] path = new Object[] { grp };
				int[] indices = new int[] { idx };
				Object[] children = new Object[] { grp.getMember(idx) };
				if (add) {
					fireNodesInserted(this, path, indices, children);
				} else {
					fireNodesRemoved(this, path, indices, children);
					grp.remove(idx);
				}
			}

		}

	}
}

