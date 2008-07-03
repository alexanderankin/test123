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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.gui.OptionPaneBase;

import projectviewer.importer.AutoReimporter;
import projectviewer.importer.GlobFilter;
import projectviewer.importer.ImportUtils;
import projectviewer.importer.ImporterFileFilter;

import projectviewer.vpt.VPTProject;


/**
 *  A dialog for configuring the auto-reimport feature for a project.
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
class AutoReimportPane extends OptionPaneBase
					   implements ActionListener
{

	AutoReimportPane(VPTProject p)
	{
		super("projectviewer.auto_reimport_props",
			  "projectviewer.project.auto_reimport");
		this.project = p;
		this.options = new AutoReimporter.Options();
	}


	/** Load the GUI components of the pane. */
	protected void _init()
	{
		List<ImporterFileFilter> flist = ImportUtils.getFilters();

		options.load(project.getProperties(), flist);

		// Is reimporting enabled?
		enable = addCheckBox("enable", options.getPeriod() > 0);
		enable.addActionListener(this);

		// Reimport period.
		period = new JTextField(String.valueOf(options.getPeriod()));
		period.setEnabled(enable.isSelected());
		addComponent(period, "period");

		// Only current directories?
		currentDirs = addCheckBox("current_dirs", options.getCurrentOnly());
		currentDirs.setEnabled(enable.isSelected());

		// Filters.
		filters = new JComboBox();
		filters.setEnabled(enable.isSelected());
		if (flist != null && flist.size() > 0) {
			for (Object o : flist) {
				filters.addItem(o);
			}
		}
		filters.addItem(jEdit.getProperty("projectviewer.import.filter.custom"));
		filters.addActionListener(this);
		addComponent(filters, "filter");

		// Custom filter file and directory globs.
		fileGlobs = new JTextField();
		fileGlobs.setEnabled(false);

		dirGlobs = new JTextField();
		dirGlobs.setEnabled(false);

		ImporterFileFilter filter = options._getFilter();
		if (filter != null) {
			if (filter instanceof GlobFilter &&
				((GlobFilter)filter).isCustom()) {
				GlobFilter gf = (GlobFilter) filter;
				fileGlobs.setText(gf.getFileGlobs());
				dirGlobs.setText(gf.getDirectoryGlobs());
				fileGlobs.setEnabled(enable.isSelected());
				dirGlobs.setEnabled(enable.isSelected());
				filters.setSelectedIndex(filters.getItemCount() - 1);
			} else {
				filters.setSelectedItem(filter);
			}
		}

		addComponent(fileGlobs, "file_glob");
		addComponent(dirGlobs, "dir_glob");
	}


	/** Saves the configuration to the project. */
	protected void _save() {
		int pval = 0;

		if (enable.isSelected()) {
			try {
				pval = Integer.parseInt(period.getText());
			} catch (NumberFormatException nfe) {
				Log.log(Log.WARNING, this,
						"Invalid auto import period: " + period.getText());
			}
		}

		if (pval > 0) {
			ImporterFileFilter filter;
			if (filters.getSelectedIndex() == filters.getItemCount() - 1) {
				filter = new GlobFilter(fileGlobs.getText(),
										dirGlobs.getText());
			} else {
				filter = (ImporterFileFilter) filters.getSelectedItem();
			}

			options._setFilter(filter);
			options.setPeriod(pval);
			options.setCurrentOnly(currentDirs.isSelected());
			options.save(project.getProperties());
		} else {
			options.clean(project.getProperties());
		}
	}


	/** Keeps the field states in sync. */
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == enable) {
			period.setEnabled(enable.isSelected());
			currentDirs.setEnabled(enable.isSelected());
			filters.setEnabled(enable.isSelected());
			if (enable.isSelected()) {
				boolean custom = (filters.getSelectedItem() instanceof String);
				fileGlobs.setEnabled(custom);
				dirGlobs.setEnabled(custom);
			} else {
				fileGlobs.setEnabled(false);
				dirGlobs.setEnabled(false);
			}
		}

		if (ae.getSource() == filters) {
			boolean custom = (filters.getSelectedItem() instanceof String);
			fileGlobs.setEnabled(custom);
			dirGlobs.setEnabled(custom);

			if (custom) {
				fileGlobs.requestFocus();
			}
		}
	}


	/* Fields. */
	private VPTProject	project;
	private AutoReimporter.Options options;

	private JCheckBox	enable;
	private JTextField	period;
	private JCheckBox 	currentDirs;
	private JComboBox 	filters;
	private JTextField	fileGlobs;
	private JTextField	dirGlobs;

}
