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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.util.Log;

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

	private static final ArrayList plugins = new ArrayList();

	private static VPTProject		p;
	private static boolean			isNew;

	/**
	 *	Adds a new plugin callback to the internal list. When the project
	 *	options dialog is shown, it calls the
	 *	{@link ProjectOptionsPlugin#createOptionPanes(OptionsDialog, VPTProject)
	 *	createOptionPanes()} method on the given object so that its options
	 *	panes are added to the dialog.
	 *
	 *	<p>Plugins that use a java.util.Properties style configuration can
	 *	reuse most of their code from the standard jEdit EBPlugin and OptionPane
	 *	implementations. They can simply extend those existing classes and instead
	 *	of querying the old property source, query the given project for the
	 *	properties, and later save them to the project instead of saving
	 *	them to the old store. This way, the same GUI can be used, and making
	 *	project-specific options require only two extra classes with little
	 *	extra code.</p>
	 *
	 *	<p><strong>IMPORTANT</strong>: do not call this method if your
	 *	ProjectOptionsPlugin class is the same class that implements your
	 *	EditPlugin. These cases are handled automatically.</p>
	 */
	public static void registerPlugin(ProjectOptionsPlugin plugin) {
		if (plugin != null) plugins.add(plugin);
	}

	/**
	 *	Shows the project options dialog for the given project.
	 *
	 *	@param	p	The project to edit or null to create a new one.
	 *	@return	The new or modified project, or null if p was null and
	 *			dialog was cancelled.
	 */
	public static VPTProject run(VPTProject project) {
		String title;
		if (project == null) {
			title = "projectviewer.create_project";
		} else {
			title = "projectviewer.edit_project";
		}

		if (project == null) {
			p = new VPTProject("");
			p.setRootPath("");
			isNew = true;
		} else {
			p = project;
			isNew = false;
		}

		new ProjectOptions(jEdit.getActiveView(), title);
		return p;
	}

	//}}}

	//{{{ Instance Variables

	private OptionGroup				rootGroup;
	private ProjectPropertiesPane	pOptPane;

	//}}}

	//{{{ Constructor

	private ProjectOptions(View view, String name) {
		super(JOptionPane.getFrameForComponent(view), name, null);
		setModal(true);
	}

	//}}}

	//{{{ cancel() method
	/**
	 *	Called when the cancel button is pressed. Sets the project to null
	 *	if "isNew" is true.
	 */
	public void cancel() {
		if (isNew) p = null;
		dispose();
	} //}}}

	//{{{ ok() method
	/**
	 *	Called when ok is pressed. Verifies if the project's properties are OK
	 *	before closing the dialog.
	 */
	public void ok() {
		save(rootGroup);
		if (pOptPane.isOK()) {
			dispose();
		}
	} //}}}

	//{{{ getDefaultGroup() method
	protected OptionGroup getDefaultGroup() {
		return rootGroup;
	} //}}}

	//{{{ createOptionTreeModel() method
	protected OptionTreeModel createOptionTreeModel() {
		OptionTreeModel paneTreeModel = new OptionTreeModel();
		rootGroup = (OptionGroup) paneTreeModel.getRoot();

		pOptPane = new ProjectPropertiesPane(p, isNew);
		addOptionPane(pOptPane);

		EditPlugin[] eplugins = jEdit.getPlugins();
		for (int i = 0; i < eplugins.length; i++) {
			if (eplugins[i] instanceof ProjectOptionsPlugin)
				((ProjectOptionsPlugin)eplugins[i]).createOptionPanes(this, p);
		}

		for (Iterator it = plugins.iterator(); it.hasNext(); ) {
			((ProjectOptionsPlugin)it.next()).createOptionPanes(this, p);
		}

		return paneTreeModel;
	} //}}}

	//{{{ save(Object)
	/** Saves the information from the option panes. */
	private void save(Object o) {
		if (o instanceof OptionGroup) {
			Enumeration en = ((OptionGroup)o).getMembers();
			while (en.hasMoreElements()) {
				Object m = en.nextElement();
				if (m instanceof OptionGroup) {
					save(m);
				} else if (m instanceof OptionPane) {
					try {
						((OptionPane)m).save();
					} catch (Exception e) {
						Log.log(Log.ERROR, m, e);
					}
				}
			}
		} else if (o instanceof OptionPane) {
			try {
				((OptionPane)o).save();
			} catch (Exception e) {
				Log.log(Log.ERROR, o, e);
			}
		}
	} //}}}

}

