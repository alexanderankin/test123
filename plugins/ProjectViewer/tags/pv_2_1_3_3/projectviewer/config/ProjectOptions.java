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
import javax.swing.JOptionPane;

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
	 *	@param	startPath	Where to open the "choose root" file dialog.
	 *	@return	The new or modified project, or null if p was null and
	 *			dialog was cancelled.
	 */
	public static synchronized VPTProject run(VPTProject project,
												VPTGroup parent,
												String startPath) {

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
		new ProjectOptions(jEdit.getActiveView(), title);
		if (isNew && p != null) {
			p.setParent(null);
		}
		project = p;
		p = null;
		return project;
	}

	//}}}

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

	private OptionTreeModel			paneModel;
	private ProjectPropertiesPane	pOptPane;

	//}}}

	//{{{ -ProjectOptions(View, String) : <init>
	private ProjectOptions(View view, String name) {
		super(JOptionPane.getFrameForComponent(view), name, null);
		setModal(true);
	} //}}}

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
		super.ok(false); //save(paneModel.getRoot());
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
		paneModel = new OptionTreeModel();

		pOptPane = new ProjectPropertiesPane(p, isNew, lookupPath);
		addOptionPane(pOptPane);

		addOptionPane(new ProjectFilterPane());

		EditPlugin[] eplugins = jEdit.getPlugins();
		for (int i = 0; i < eplugins.length; i++) {
			createOptions(eplugins[i]);
		}

		return paneModel;
	} //}}}

	//{{{ #createOptions(EditPlugin) : boolean
	/**
	 *	For jEdit 4.2: creates options panes based on properties set by the
	 *	plugin, so manual registration of the plugin is not necessary. More
	 *	details in the package description documentation.
	 *
	 *	@return	true if an option pane or an option group was added, false
	 *			otherwise.
	 */
	protected boolean createOptions(EditPlugin plugin) {
		// Look for a single option pane
		String property = "plugin.projectviewer." + plugin.getClassName() + ".option-pane";
		if ((property = jEdit.getProperty(property)) != null) {
			((OptionGroup)paneModel.getRoot()).addOptionPane(property);
			return true;
		}

		// Look for an option group
		property = "plugin.projectviewer." +
					plugin.getClassName() + ".option-group";
		if ((property = jEdit.getProperty(property)) != null) {
			((OptionGroup)paneModel.getRoot()).addOptionGroup(
				new OptionGroup("plugin." + plugin.getClassName(),
					jEdit.getProperty("plugin."
										+ plugin.getClassName() + ".name"),
					property)
				);
			return true;
		}

		// nothing found
		return false;
	} //}}}

}

