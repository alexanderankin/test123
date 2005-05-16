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
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.EnhancedDialog;

import projectviewer.importer.CVSEntriesFilter;
import projectviewer.importer.GlobFilter;
import projectviewer.importer.ImporterFileFilter;
import projectviewer.importer.NonProjectFileFilter;

import projectviewer.PVActions;

import projectviewer.vpt.VPTDirectory;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *	An import dialog that embeds a JFileChooser and provides several
 *	customization options for the importing process in the same GUI.
 *
 *	<p>This is an attempt to fix the mess that is the import GUI code
 *	in the importer classes, and to provide some extra flexibility.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.1
 */
public class ImportDialog extends EnhancedDialog
						  implements ActionListener, ItemListener {

	//{{{ Private members
	private boolean isApproved;

	private JCheckBox flatten;
	private JCheckBox newNode;
	private JCheckBox traverse;
	private JComboBox filters;
	private JFileChooser chooser;

	private JTextField dGlob;
	private JTextField fGlob;
	private JTextField newNodeName;
	//}}}

	//{{{ +ImportDialog(Dialog, VPTProject, VPTNode) : <init>
	public ImportDialog(Dialog parent, VPTProject proj, VPTNode selected) {
		super(parent, jEdit.getProperty("projectviewer.action.import_dlg.title"),
			  true);
		init(proj, selected);
	} //}}}

	//{{{ +ImportDialog(Frame, VPTProject, VPTNode) : <init>
	public ImportDialog(Frame parent, VPTProject proj, VPTNode selected) {
		super(parent, jEdit.getProperty("projectviewer.action.import_dlg.title"),
			  true);
		init(proj, selected);
	} //}}}

	//{{{ -init(VPTProject, VPTNode) : void
	private void init(VPTProject proj, VPTNode selected) {
		this.isApproved = false;
		getContentPane().setLayout(new BorderLayout());

		List fileFilters = getFileFilters();

		String initPath;
		if (selected != null && selected.isDirectory() &&
			((VPTDirectory)selected).getFile().exists())
		{
			initPath = selected.getNodePath();
		} else {
			initPath = proj.getRootPath();
		}

		chooser = new ModalJFileChooser(initPath);
		chooser.setControlButtonsAreShown(false);

		FileFilter npff = new NonProjectFileFilter(proj);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.addChoosableFileFilter(npff);

		List ffilters = getFileFilters();
		for (Iterator i = ffilters.iterator(); i.hasNext(); )
			chooser.addChoosableFileFilter((FileFilter) i.next());
		chooser.setFileFilter(npff);

		getContentPane().add(BorderLayout.CENTER, chooser);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel options = new JPanel(gbl);
		options.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
							jEdit.getProperty("projectviewer.import-dlg.options")));

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.0;

		traverse = new JCheckBox(jEdit.getProperty("projectviewer.import-dlg.traverse_dirs"));
		traverse.addActionListener(this);
		gbl.setConstraints(traverse, gbc);
		options.add(traverse);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		flatten = new JCheckBox(jEdit.getProperty("projectviewer.import-dlg.flatten_paths"));
		flatten.addActionListener(this);
		gbl.setConstraints(flatten, gbc);
		options.add(flatten);

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		newNode = new JCheckBox(jEdit.getProperty("projectviewer.import-dlg.crate_new"));
		newNode.addActionListener(this);
		gbl.setConstraints(newNode, gbc);
		options.add(newNode);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.0;
		newNodeName = new JTextField();
		gbl.setConstraints(newNodeName, gbc);
		options.add(newNodeName);

		JSeparator sep = new JSeparator();
		gbl.setConstraints(sep, gbc);
		options.add(sep);

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		JLabel label = new JLabel(jEdit.getProperty("projectviewer.import-dlg.file_filter"));
		gbl.setConstraints(label, gbc);
		options.add(label);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.0;

		filters = new JComboBox();
		filters.addItemListener(this);
		filters.addItem(new AllFilesFilter());
		for (Iterator i = ffilters.iterator(); i.hasNext(); )
			filters.addItem(i.next());
		filters.addItem(jEdit.getProperty("projectviewer.import.filter.custom"));

		gbl.setConstraints(filters, gbc);
		options.add(filters);

		label = new JLabel(jEdit.getProperty("projectviewer.import-dlg.file_globs"));
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.0;
		gbl.setConstraints(label, gbc);
		options.add(label);

		fGlob = new JTextField();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.0;
		gbl.setConstraints(fGlob, gbc);
		options.add(fGlob);

		label = new JLabel(jEdit.getProperty("projectviewer.import-dlg.dir_globs"));
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.weightx = 0.0;
		gbl.setConstraints(label, gbc);
		options.add(label);

		dGlob = new JTextField();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.0;
		gbl.setConstraints(dGlob, gbc);
		options.add(dGlob);

		// finish it
		JPanel south = new JPanel(new BorderLayout());
		south.add(BorderLayout.CENTER, options);
		south.add(BorderLayout.SOUTH, new OkCancelButtons(this));
		getContentPane().add(BorderLayout.SOUTH, south);

		actionPerformed(null);
		GUIUtilities.loadGeometry(this, getClass().getName());
	} //}}}

	//{{{ +ok() : void
	public void ok() {
		isApproved = true;
		GUIUtilities.saveGeometry(this, getClass().getName());
		dispose();
	} //}}}

	//{{{ +cancel() : void
	public void cancel() {
		GUIUtilities.saveGeometry(this, getClass().getName());
		dispose();
	} //}}}

	//{{{ +getSelectedFiles() : File[]
	public File[] getSelectedFiles() {
		if (isApproved) {
			// see: http://forum.java.sun.com/thread.jspa?forumID=57&threadID=356088
			if (chooser.getUI() instanceof BasicFileChooserUI) {
				BasicFileChooserUI ui = (BasicFileChooserUI)chooser.getUI();
				ui.getApproveSelectionAction().actionPerformed(null);
			}
			return chooser.getSelectedFiles();
		}
		return null;
	} //}}}

	//{{{ +getTraverseDirectories() : boolean
	public boolean getTraverseDirectories() {
		return traverse.isSelected();
	} //}}}

	//{{{ +getFlattenFilePaths() : boolean
	public boolean getFlattenFilePaths() {
		return (traverse.isSelected() && flatten.isSelected());
	} //}}}

	//{{{ +getNewNodeName() : String
	public String getNewNodeName() {
		return (newNode.isSelected() && newNodeName.getText().length() > 0)
			 ? newNodeName.getText()
			 : null;
	} //}}}

	//{{{ +getImportFilter() : FilenameFilter
	public FilenameFilter getImportFilter() {
		if (filters.getSelectedItem() instanceof ImporterFileFilter) {
			return (FilenameFilter) filters.getSelectedItem();
		} else {
			return new GlobFilter(fGlob.getText(), dGlob.getText());
		}
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	public void actionPerformed(ActionEvent ae) {
		flatten.setEnabled(traverse.isSelected());
		newNodeName.setEnabled(newNode.isSelected());
		if (ae != null && ae.getSource() == newNode && newNodeName.isEnabled())
			newNodeName.requestFocus();

		filters.setEnabled(traverse.isSelected());
		itemStateChanged(null);
	} //}}}

	//{{{ +itemStateChanged(ItemEvent) : void
	public void itemStateChanged(ItemEvent e)  {
		if (dGlob != null) {
			dGlob.setEnabled(filters.isEnabled()
							 && (filters.getSelectedItem() instanceof String));
			fGlob.setEnabled(filters.isEnabled()
							 && (filters.getSelectedItem() instanceof String));
			if (fGlob.isEnabled())
				fGlob.requestFocus();
		}
	} //}}}

	//{{{ +hideFileChooser() : void
	/**
	 *	Hides the file chooser from the UI. Useful when only interested in
	 *	the import options, and not on the choosing of the files to import.
	 */
	public void hideFileChooser() {
		this.chooser.setVisible(false);
	} //}}}

	//{{{ +hideNewNode() : void
	public void hideNewNode() {
		newNode.setVisible(false);
		newNode.setSelected(false);
		newNodeName.setVisible(false);
	} //}}}

	//{{{ +lockTraverse() : void
	public void lockTraverse() {
		traverse.setSelected(true);
		traverse.setEnabled(false);
		actionPerformed(null);
	} //}}}

	//{{{ +show() : void
	public void show() {
		pack();
		super.show();
	} //}}}

	//{{{ +isApproved() : boolean
	public boolean isApproved() {
		return isApproved;
	} //}}}

	//{{{ -getFileFilters() : List
	/**
	 *	Instantiate the default file filters from Project Viewer and checks
	 *	all the other plugins looking for any custom filters they provide.
	 *
	 *	@param	addAllFilter	Whether to add the "AllFilesFilter" to the list.
	 **/
	private List getFileFilters() {
		List filters = new ArrayList();
		filters.add(GlobFilter.getImportSettingsFilter());
		filters.add(new CVSEntriesFilter());

		EditPlugin[] plugins = jEdit.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			String list = jEdit.getProperty("plugin.projectviewer." +
							plugins[i].getClassName() + ".file-filters");
			Collection aList =
				PVActions.listToObjectCollection(list, plugins[i].getPluginJAR(),
					ImporterFileFilter.class);

			if (aList != null && aList.size() > 0) {
				filters.addAll(aList);
			}
		}

		return filters;
	} //}}}

	//{{{ -class _AllFilesFilter_
	/** Dumb file filter that accepts everything. */
	private static class AllFilesFilter extends ImporterFileFilter {

		//{{{ +getDescription() : String
		public String getDescription() {
			return null;
		} //}}}

		//{{{ +accept(File) : boolean
		public boolean accept(File file) {
			return true;
		} //}}}

		//{{{ +accept(File, String) : boolean
		public boolean accept(File file, String fileName) {
			return true;
		} //}}}

		//{{{ +getRecurseDescription() : String
		public String getRecurseDescription() {
			return	jEdit.getProperty("projectviewer.import.filter.all");
		} //}}}

	} //}}}

}

