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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

// Import jEdit
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.AbstractOptionPane;

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
    
    private JTextField importExts;
    private JTextField excludeDirs;
    private JTextField includeFiles;
    
    //-------------- Constructors
    
    public ProjectViewerOptionsPane(String name) {
        super(name);
        config = ProjectViewerConfig.getInstance();        
    }

    //-------------- Methods
    
    /** Initializes the option pane. */
    protected void _init() {
        
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
        
        // Importing files options: creates a nifty pane for the 3 fields
        JPanel pane = new JPanel();
        pane.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.black),
                "Importing files",
                TitledBorder.LEADING,
                TitledBorder.TOP
            )
        );
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        pane.setLayout(gridbag);
        
        JLabel label = new JLabel("Extensions to include:");
        gc.weightx = 0;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,gc);
        pane.add(label);
        
        importExts = new JTextField();
        if (config.getImportExts() != null) {
            importExts.setText(config.getImportExts());
        }
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(importExts,gc);
        pane.add(importExts);
        
        label = new JLabel("Directories to ignore:");
        gc.weightx = 0;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,gc);
        pane.add(label);

        excludeDirs = new JTextField();
        if (config.getExcludeDirs() != null) {
            excludeDirs.setText(config.getExcludeDirs());
        }
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(excludeDirs,gc);
        pane.add(excludeDirs);
        
        label = new JLabel("Files to include:");
        gc.weightx = 0;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(label,gc);
        pane.add(label);

        includeFiles = new JTextField();
        if (config.getIncludeFiles() != null) {
            includeFiles.setText(config.getIncludeFiles());
        }
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(includeFiles,gc);
        pane.add(includeFiles);
        
        addComponent(pane);
    }
    
    /** Saves the options. */
    protected void _save() {
        config.setCloseFiles(closeFiles.isSelected());
        config.setRememberOpen(rememberOpen.isSelected());
        config.setDeleteNotFoundFiles(deleteNotFoundFiles.isSelected());
        config.setSaveOnChange(saveOnChange.isSelected());
        
        config.setImportExts(importExts.getText());
        config.setExcludeDirs(excludeDirs.getText());
        config.setIncludeFiles(includeFiles.getText());
        
        config.save();
    }
}
