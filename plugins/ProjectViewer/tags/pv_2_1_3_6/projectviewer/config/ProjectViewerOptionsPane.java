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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OperatingSystem;
import org.gjt.sp.jedit.AbstractOptionPane;

import projectviewer.ProjectViewer;
//}}}

/**
 *  <p>Option pane to configure the ProjectViewer plugin.</p>
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 */
public class ProjectViewerOptionsPane extends AbstractOptionPane
										implements ActionListener {

	//{{{ Instance variables
	private ProjectViewerConfig config;
	private JScrollPane			component;

	private JCheckBox useExternalApps;
	private JCheckBox closeFiles;
	private JCheckBox rememberOpen;
	private JCheckBox deleteNotFoundFiles;

	private JRadioButton autoImport;
	private JRadioButton askAlways;
	private JRadioButton askOnce;
	private JRadioButton askNever;

	private JCheckBox showToolBar;
	private JCheckBox showFoldersTree;
	private JCheckBox showFilesTree;
	private JCheckBox showWorkingFilesTree;
	private JCheckBox showCompactTree;
	private JCheckBox showFilteredTree;
	private JCheckBox useSystemIcons;
	private JCheckBox showProjectInTitle;
	private JCheckBox caseInsensitiveSort;
	private JCheckBox followCurrentBuffer;

	private JTextArea importGlobs;
	private JTextField excludeDirs;

	private JCheckBox	useInfoViewer;
	private JTextField	browserExecPath;
	//}}}

	//{{{ +ProjectViewerOptionsPane(String) : <init>

	public ProjectViewerOptionsPane(String name) {
		super(name);
		config = ProjectViewerConfig.getInstance();
	}

	//}}}

	//{{{ #_init() : void
	/** Initializes the option pane. */
	protected void _init() {
		//{{{ general options
		addSeparator("options.projectviewer.general-opt.label");

		// Checkbox: "use external apps by default"
		useExternalApps = new JCheckBox(jEdit.getProperty("projectviewer.options.use_external_apps"));
		useExternalApps.setToolTipText(jEdit.getProperty("projectviewer.options.use_external_apps.tooltip"));
		useExternalApps.setSelected(config.getUseExternalApps());
		addComponent(useExternalApps);

		// Checkbox: "close project files on switch"
		closeFiles = new JCheckBox(jEdit.getProperty("projectviewer.options.close_on_change"));
		closeFiles.setToolTipText(jEdit.getProperty("projectviewer.options.close_on_change.tooltip"));
		closeFiles.setSelected(config.getCloseFiles());
		addComponent(closeFiles);

		// Checkbox: "remember open project files"
		rememberOpen = new JCheckBox(jEdit.getProperty("projectviewer.options.remember_open"));
		rememberOpen.setToolTipText(jEdit.getProperty("projectviewer.options.remember_open.tooltip"));
		rememberOpen.setSelected(config.getRememberOpen());
		addComponent(rememberOpen);

		// Checkbox: "delete non-existant files"
		deleteNotFoundFiles = new JCheckBox(jEdit.getProperty("projectviewer.options.delete_stale"));
		deleteNotFoundFiles.setToolTipText(jEdit.getProperty("projectviewer.options.delete_stale.tooltip"));
		deleteNotFoundFiles.setSelected(config.getDeleteNotFoundFiles());
		addComponent(deleteNotFoundFiles);

		//{{{ Button group: "ask import"
		JLabel label = new JLabel(jEdit.getProperty("projectviewer.options.ask_import"));
		label.setToolTipText(jEdit.getProperty("projectviewer.options.ask_import.tooltip"));
		addComponent(label);

		JPanel pane = new JPanel(new FlowLayout());
		ButtonGroup bg = new ButtonGroup();

		autoImport = new JRadioButton(jEdit.getProperty("projectviewer.options.ask_import.auto_import"));
		bg.add(autoImport);
		pane.add(autoImport);
		askAlways = new JRadioButton(jEdit.getProperty("projectviewer.options.ask_import.always"));
		bg.add(askAlways);
		pane.add(askAlways);
		askOnce = new JRadioButton(jEdit.getProperty("projectviewer.options.ask_import.once"));
		bg.add(askOnce);
		pane.add(askOnce);
		askNever = new JRadioButton(jEdit.getProperty("projectviewer.options.ask_import.never"));
		bg.add(askNever);
		pane.add(askNever);

		switch (config.getAskImport()) {
			case ProjectViewerConfig.AUTO_IMPORT:
				autoImport.setSelected(true);
				break;

			case ProjectViewerConfig.ASK_ALWAYS:
				askAlways.setSelected(true);
				break;

			case ProjectViewerConfig.ASK_ONCE:
				askOnce.setSelected(true);
				break;

			case ProjectViewerConfig.ASK_NEVER:
				askNever.setSelected(true);
				break;
		}

		addComponent(pane);
		//}}}

		//}}}

		//{{{ gui options
		addSeparator("options.projectviewer.gui-opt.label");

		showToolBar = new JCheckBox(jEdit.getProperty("projectviewer.options.show_toolbar"));
		showToolBar.setSelected(config.getShowToolBar());
		addComponent(showToolBar);

		showFoldersTree = new JCheckBox(jEdit.getProperty("projectviewer.options.show_folders"));
		showFoldersTree.setSelected(config.getShowFoldersTree());
		addComponent(showFoldersTree);

		showFilesTree = new JCheckBox(jEdit.getProperty("projectviewer.options.show_files"));
		showFilesTree.setSelected(config.getShowFilesTree());
		showFilesTree.setToolTipText(jEdit.getProperty("projectviewer.options.show_files.tooltip"));
		addComponent(showFilesTree);

		showWorkingFilesTree = new JCheckBox(jEdit.getProperty("projectviewer.options.show_working_files"));
		showWorkingFilesTree.setSelected(config.getShowWorkingFilesTree());
		addComponent(showWorkingFilesTree);

		showCompactTree = new JCheckBox(jEdit.getProperty("projectviewer.options.show_compact_tree"));
		showCompactTree.setSelected(config.getShowCompactTree());
		addComponent(showCompactTree);

		showFilteredTree = new JCheckBox(jEdit.getProperty("projectviewer.options.show_filtered_tree"));
		showFilteredTree.setSelected(config.getShowFilteredTree());
		addComponent(showFilteredTree);

		useSystemIcons = new JCheckBox(jEdit.getProperty("projectviewer.options.use_system_icons"));
		useSystemIcons.setSelected(config.getUseSystemIcons());
		addComponent(useSystemIcons);

		if (config.isJEdit43()) {
			showProjectInTitle = new JCheckBox(jEdit.getProperty("projectviewer.options.show_project_in_title"));
			showProjectInTitle.setSelected(config.getShowProjectInTitle());
			addComponent(showProjectInTitle);
		}

		caseInsensitiveSort = new JCheckBox(jEdit.getProperty("projectviewer.options.case_insensitive_sort"));
		caseInsensitiveSort.setSelected(config.getCaseInsensitiveSort());
		addComponent(caseInsensitiveSort);

		followCurrentBuffer = new JCheckBox(jEdit.getProperty("projectviewer.options.follow_current_buffer"));
		followCurrentBuffer.setSelected(config.getFollowCurrentBuffer());
		addComponent(followCurrentBuffer);

		//}}}

		//{{{ importer options
		addSeparator("options.projectviewer.importer-opt.label");

		importGlobs = new JTextArea(5, 4);
		importGlobs.setLineWrap(true);
		if (config.getImportGlobs() != null) {
			importGlobs.setText(config.getImportGlobs());
		}
		importGlobs.setToolTipText(jEdit.getProperty("projectviewer.options.import_globs.tooltip"));
		addComponent(jEdit.getProperty("projectviewer.options.import_globs"),
			new JScrollPane(importGlobs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
				GridBagConstraints.BOTH);

		excludeDirs = new JTextField(5);
		if (config.getExcludeDirs() != null) {
			excludeDirs.setText(config.getExcludeDirs());
		}
		addComponent(jEdit.getProperty("projectviewer.options.ignore_dir"),excludeDirs);
		//}}}

		//{{{ web project options
		addSeparator("options.projectviewer.web-prj-opt.label");

		useInfoViewer = new JCheckBox(jEdit.getProperty("projectviewer.options.use_info_viewer"));
		useInfoViewer.setSelected(config.getUseInfoViewer());
		useInfoViewer.addActionListener(this);
		useInfoViewer.setEnabled(config.isInfoViewerAvailable());
		addComponent(useInfoViewer);

		browserExecPath = new JTextField(5);
		if (config.getBrowserPath() != null) {
			browserExecPath.setText(config.getBrowserPath());
		}
		addComponent(jEdit.getProperty("projectviewer.options.browser_path"), browserExecPath);
		browserExecPath.setToolTipText(jEdit.getProperty("projectviewer.options.browser_path.tooltip"));
		browserExecPath.setEnabled(!useInfoViewer.isSelected());
		//}}}

	} //}}}

	//{{{ #_save() : void
	/** Saves the options. */
	protected void _save() {
		config.setUseExternalApps(useExternalApps.isSelected());
		config.setCloseFiles(closeFiles.isSelected());
		config.setRememberOpen(rememberOpen.isSelected());
		config.setDeleteNotFoundFiles(deleteNotFoundFiles.isSelected());

		config.setShowToolBar(showToolBar.isSelected());
		config.setShowFoldersTree(showFoldersTree.isSelected());
		config.setShowFilesTree(showFilesTree.isSelected());
		config.setShowWorkingFilesTree(showWorkingFilesTree.isSelected());
		config.setShowCompactTree(showCompactTree.isSelected());
		config.setShowFilteredTree(showFilteredTree.isSelected());
		config.setUseSystemIcons(useSystemIcons.isSelected());

		if (config.isJEdit43()) {
			config.setShowProjectInTitle(showProjectInTitle.isSelected());
		}

		config.setCaseInsensitiveSort(caseInsensitiveSort.isSelected());
		config.setFollowCurrentBuffer(followCurrentBuffer.isSelected());

		if (askAlways.isSelected()) {
			config.setAskImport(ProjectViewerConfig.ASK_ALWAYS);
		} else if (askOnce.isSelected()) {
			config.setAskImport(ProjectViewerConfig.ASK_ONCE);
		} else if (askNever.isSelected()) {
			config.setAskImport(ProjectViewerConfig.ASK_NEVER);
		} else {
			config.setAskImport(ProjectViewerConfig.AUTO_IMPORT);
		}

		config.setImportGlobs(importGlobs.getText());
		config.setExcludeDirs(excludeDirs.getText());
		config.setBrowserpath(browserExecPath.getText());
		config.setUseInfoViewer(useInfoViewer.isSelected());
		config.save();

		ProjectViewer pv = ProjectViewer.getViewer(jEdit.getActiveView());
		if (pv != null) {
			pv.repaint();
		}
	} //}}}

	//{{{ +getComponent() : Component
	public Component getComponent() {
		if (component == null) {
			component = new JScrollPane(super.getComponent());
		}
		return component;
	} //}}}

	//{{{ +actionPerformed(ActionEvent) : void
	/** Waits for events in some of the fields.. */
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == useInfoViewer) {
			browserExecPath.setEnabled(!useInfoViewer.isSelected());
		}

	} //}}}

}

