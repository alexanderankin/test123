/*
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

// Import Java
import java.util.Properties;

import java.io.OutputStream;
import java.io.IOException;

// Import AWT/Swing
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

// Import jEdit
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.*;
import projectviewer.ProjectPlugin;

/**
 *  <p>Option pane to configure the ProjectViewer plugin.</p> 
 *
 *  @author     Marcelo Vanzin
 */
public class ProjectViewerOptionsPane extends AbstractOptionPane {

    //-------------- Instance variables
    
    private ProjectViewerConfig config;

    private JCheckBox closeFiles;
    private JCheckBox rememberOpen;
    private JCheckBox deleteNotFoundFiles;
    private JCheckBox saveOnChange;
    
    private JCheckBox showToolBar;
    private JCheckBox showFoldersTree;
    private JCheckBox showFilesTree;
    private JCheckBox showWorkingFilesTree;
    
    private JTextField importExts;
    private JTextField excludeDirs;
    private JTextField includeFiles;
    private JTextField browserExecPath;
    private JTextField browseExts; 
    
    
    //-------------- Constructors
    
    public ProjectViewerOptionsPane(String name) {
        super(name);
        config = ProjectViewerConfig.getInstance();        
    }

    //-------------- Methods
    
    /** Initializes the option pane. */
    protected void _init() {

	//-- general options
	addSeparator("options.projectviewer.general-opt.label");
        
        // Checkbox: "close project files on switch"
        closeFiles = new JCheckBox("Close files on project change");
        closeFiles.setToolTipText("Close current project's files when switching to another project?");
        closeFiles.setSelected(config.getCloseFiles());
        addComponent(closeFiles);
        
        // Checkbox: "remember open project files"
        rememberOpen = new JCheckBox("Remember open project files");
        rememberOpen.setToolTipText("Reload the set of files previously opened when loading a project?");
        rememberOpen.setSelected(config.getCloseFiles());
        addComponent(rememberOpen);

        // Checkbox: "delete non-existant files"
        deleteNotFoundFiles = new JCheckBox("Delete non-existant files from list");
        deleteNotFoundFiles.setToolTipText("If checked, files not found on disk will be removed from the project at startup");
        deleteNotFoundFiles.setSelected(config.getDeleteNotFoundFiles());
        addComponent(deleteNotFoundFiles);
        
        // Checkbox: "save on change"
        saveOnChange = new JCheckBox("Save project data on change");
        saveOnChange.setToolTipText("If checked, project data will be saved when you import/remove files");
        saveOnChange.setSelected(config.getSaveOnChange());
        addComponent(saveOnChange);
        
        //-- gui options
        addSeparator("options.projectviewer.gui-opt.label");
        
        showToolBar = new JCheckBox("Show toolbar");
        showToolBar.setSelected(config.getShowToolBar());
        addComponent(showToolBar);
        
        showFoldersTree = new JCheckBox("Show folders tree");
        showFoldersTree.setSelected(config.getShowFoldersTree());
        addComponent(showFoldersTree);
        
        showFilesTree = new JCheckBox("Show files tree");
        showFilesTree.setSelected(config.getShowFilesTree());
        showFilesTree.setToolTipText(
            "Disabling this tree will improve performance in projects with many files");
        addComponent(showFilesTree);
        
        showWorkingFilesTree = new JCheckBox("Show working files tree");
        showWorkingFilesTree.setSelected(config.getShowWorkingFilesTree());
        addComponent(showWorkingFilesTree);
        
		//-- importer options
		addSeparator("options.projectviewer.importer-opt.label");

        importExts = new JTextField(5);
        if (config.getImportExts() != null) {
            importExts.setText(config.getImportExts());
        }
		addComponent("Extensions to include:",importExts);

        excludeDirs = new JTextField(5);
        if (config.getExcludeDirs() != null) {
            excludeDirs.setText(config.getExcludeDirs());
        }
		addComponent("Directories to ignore:",excludeDirs);

        includeFiles = new JTextField(5);
        if (config.getIncludeFiles() != null) {
            includeFiles.setText(config.getIncludeFiles());
        }
		addComponent("Files to include:",includeFiles);
		
		//-- web project options
		addSeparator("options.projectviewer.web-prj-opt.label");
	
		browserExecPath = new JTextField(5);
		browserExecPath.setToolTipText("This is used for web projects, to Launch Files in the Browser");
		if (config.getBrowserPath() != null) {
			browserExecPath.setText(config.getBrowserPath());
		}
		//jEdit.getProperty("options.jcompiler.autosave.ask")
		addComponent("Browser Path:", browserExecPath);
	
		browseExts = new JTextField(5);
		addComponent("Browse-able Extensions:", browseExts);
	
    }
    
    /** Saves the options. */
    protected void _save() {
        config.setCloseFiles(closeFiles.isSelected());
        config.setRememberOpen(rememberOpen.isSelected());
        config.setDeleteNotFoundFiles(deleteNotFoundFiles.isSelected());
        config.setSaveOnChange(saveOnChange.isSelected());
        
        config.setShowToolBar(showToolBar.isSelected());
        config.setShowFoldersTree(showFoldersTree.isSelected());
        config.setShowFilesTree(showFilesTree.isSelected());
        config.setShowWorkingFilesTree(showWorkingFilesTree.isSelected());
        
        config.setImportExts(importExts.getText());
        config.setExcludeDirs(excludeDirs.getText());
        config.setIncludeFiles(includeFiles.getText());
        config.setBrowserpath(browserExecPath.getText());
        config.save();
    }
}
