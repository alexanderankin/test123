/*
* OpenIt jEdit Plugin (SourcePathOptionsPane.java) 
*  
* Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
* Copyright (C) 2006 Denis Koryavov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
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

package org.etheridge.openit.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

import java.io.File;

import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.SourcePathManager;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import projectviewer.ProjectViewer;

import projectviewer.vpt.VPTProject;

/**
 * OptionPane for the source path options.
 */
public class SourcePathOptionsPane extends AbstractOptionPane
implements ActionListener  
{
        
        // gui components for user defined classpath section
        private JCheckBox mGetRootFromProjectViewer;
        private JLabel mInstructionLabel;
        private SourcePathList mSourcePathList;
        private JButton mAddButton;
        private JButton mRemoveButton;
        private JFileChooser msSourcePathElementFileChooser;
        
        private JTextField mExcludesRegularExpressionTextField;
        private JTextField mPollingIntervalTextField;
        private JTextField mExcludesDirectoriesRegularExpressionTextField;
        
        private JCheckBox mIgnoreCaseExcludeFilesCheckBox;
        private JCheckBox mIgnoreCaseExcludeDirectoriesCheckBox;
        
        public SourcePathOptionsPane()
        {
                super("OpenIt.SourcePathOptionPane");
        }
        
	public void _init() 
        {
                setLayout(new BorderLayout());
                
                JPanel placerPanel = new JPanel(new BorderLayout());
                placerPanel.add(createSourcePathPanel(), BorderLayout.CENTER);
                
                // bottom panel
                JPanel bottomPanel = new JPanel(new BorderLayout());
                
                // excludes panel
                JPanel excludesPanel = new JPanel(new GridLayout(2,1));
                excludesPanel.add(createExcludesPanel());
                excludesPanel.add(createExcludesDirectoryPanel());
                
                bottomPanel.add(excludesPanel, BorderLayout.CENTER);
                bottomPanel.add(createRefreshSourcePathPanel(), BorderLayout.SOUTH);
                placerPanel.add(bottomPanel, BorderLayout.SOUTH);
                
                add(placerPanel, BorderLayout.NORTH);
	}
        
        public void _save() {
                setProperties();
                SourcePathManager.getInstance().refreshSourcePath();
        }
        
        //
        // GUI Initialization
        //
        
        private JPanel createSourcePathPanel()
        {
                JPanel sourcePathPanel = new JPanel(new BorderLayout(6,6));
                sourcePathPanel.setBorder(BorderFactory.createCompoundBorder
                        (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.title")),
                                BorderFactory.createEmptyBorder(6,6,6,6)));
                
                // top panel will contain a JList that holds a list of source paths
                JPanel topPanel = new JPanel(new BorderLayout());
                mGetRootFromProjectViewer  = new JCheckBox(
                        jEdit.getProperty("options.OpenIt.SourcePathOptionPane.mGetRootFromProjectViewer.label"));
                mGetRootFromProjectViewer.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                               boolean selected = mGetRootFromProjectViewer.isSelected();
                                jEdit.setBooleanProperty(OpenItProperties.IMPORT_FILES_FROM_CURRENT_PROJECT, selected);
                                toggleClassPathChoiceMethod(!selected);
                        }
                });
                topPanel.add(mGetRootFromProjectViewer, BorderLayout.NORTH);
                
                JPanel box = new JPanel(new BorderLayout());
                
                box.add(mInstructionLabel = new JLabel(jEdit.getProperty(
                        "options.OpenIt.SourcePathOptionPane.SourcePath.instruction.label")),
                        BorderLayout.NORTH);
                mSourcePathList = new SourcePathList();
                mSourcePathList.addPathElements(jEdit.getProperty(OpenItProperties.SOURCE_PATH_STRING, ""));
                box.add(new JScrollPane(mSourcePathList), BorderLayout.CENTER);
                topPanel.add(box, BorderLayout.CENTER);
                
                // bottom panel - add/remove buttons
                JPanel bottomPanel = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel(new GridLayout(1,2,5,5));
                mAddButton = new JButton(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.AddButton.label"));
                mAddButton.addActionListener(this);
                mRemoveButton = new JButton(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.RemoveButton.label"));
                mRemoveButton.addActionListener(this);
                mRemoveButton.setEnabled(false); // initially not enabled as nothing selected
                buttonPanel.add(mAddButton);
                buttonPanel.add(mRemoveButton);
                bottomPanel.add(buttonPanel, BorderLayout.CENTER);
                
                // add to top level sourcepathpanel
                sourcePathPanel.add(topPanel, BorderLayout.CENTER);
                sourcePathPanel.add(bottomPanel, BorderLayout.SOUTH);
                
                if (isProjectViewerInstalled()) {
                        if (jEdit.getBooleanProperty(OpenItProperties.IMPORT_FILES_FROM_CURRENT_PROJECT)) {
                                mGetRootFromProjectViewer.setSelected(true);
                                toggleClassPathChoiceMethod(false);
                        } else {
                                mGetRootFromProjectViewer.setSelected(false);
                        }
                } else {
                        mGetRootFromProjectViewer.setSelected(false);
                        mGetRootFromProjectViewer.setEnabled(false);
                }
                
                // add a seleciton listener to the JList, so we can make sure the remove
                // button is in correct state
                mSourcePathList.addListSelectionListener(new ListSelectionListener()
                        {
                                public void valueChanged(ListSelectionEvent e)
                                {
                                        mRemoveButton.setEnabled(mSourcePathList.getSelectedValue() != null);
                                }
                        });
                
                return sourcePathPanel;
        }
        
        private JPanel createExcludesPanel()
        {
                JPanel excludesPanel = new JPanel(new GridLayout(2,1));
                excludesPanel.setBorder(BorderFactory.createCompoundBorder
                        (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.ExcludeRegularExpression.title")), 
                                BorderFactory.createEmptyBorder(6,6,6,6)));
                
                JLabel instructionLabel = new JLabel(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.ExcludeRegularExpression.label"));
                excludesPanel.add(instructionLabel);
                
                JPanel textFieldPanel = new JPanel(new BorderLayout());
                mExcludesRegularExpressionTextField = new JTextField();
                mExcludesRegularExpressionTextField.setText
                (jEdit.getProperty(OpenItProperties.EXCLUDES_REGULAR_EXPRESSION, ""));
                mIgnoreCaseExcludeFilesCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.IgnoreCase.label"));
                mIgnoreCaseExcludeFilesCheckBox.setSelected
                (jEdit.getBooleanProperty(OpenItProperties.IGNORE_CASE_EXCLUDES_FILE_REGULAR_EXPRESSION, false));
                textFieldPanel.add(mExcludesRegularExpressionTextField);
                textFieldPanel.add(mIgnoreCaseExcludeFilesCheckBox, BorderLayout.EAST);
                excludesPanel.add(textFieldPanel);
                
                return excludesPanel;
        }
        
        private JPanel createExcludesDirectoryPanel()
        {
                JPanel excludesPanel = new JPanel(new GridLayout(2,1));
                excludesPanel.setBorder(BorderFactory.createCompoundBorder
                        (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.ExcludeDirectoryRegularExpression.title")), 
                                BorderFactory.createEmptyBorder(6,6,6,6)));
                
                JLabel instructionLabel = new JLabel(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.ExcludeDirectoryRegularExpression.label"));
                excludesPanel.add(instructionLabel);
                
                JPanel textFieldPanel = new JPanel(new BorderLayout());
                mExcludesDirectoriesRegularExpressionTextField = new JTextField();
                mExcludesDirectoriesRegularExpressionTextField.setText
                (jEdit.getProperty(OpenItProperties.EXCLUDES_DIRECTORIES_REGULAR_EXPRESSION, ""));
                mIgnoreCaseExcludeDirectoriesCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.IgnoreCase.label"));
                mIgnoreCaseExcludeDirectoriesCheckBox.setSelected
                (jEdit.getBooleanProperty(OpenItProperties.IGNORE_CASE_EXCLUDES_DIRECTORIES_REGULAR_EXPRESSION, false));
                textFieldPanel.add(mExcludesDirectoriesRegularExpressionTextField);
                textFieldPanel.add(mIgnoreCaseExcludeDirectoriesCheckBox, BorderLayout.EAST);
                excludesPanel.add(textFieldPanel);
                
                return excludesPanel;
        }
        
        private JPanel createRefreshSourcePathPanel()
        {
                JPanel pollingIntervalPanel = new JPanel(new BorderLayout());
                pollingIntervalPanel.setBorder(BorderFactory.createCompoundBorder
                        (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.RefreshRate.title")), 
                                BorderFactory.createEmptyBorder(6,6,6,6)));
                
                JLabel instructionLabel = new JLabel(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.RefreshRate.CheckBox.label"));
                pollingIntervalPanel.add(instructionLabel, BorderLayout.WEST);
                
                mPollingIntervalTextField = new JTextField(String.valueOf(jEdit.getIntegerProperty(OpenItProperties.SOURCE_PATH_POLLING_INTERVAL, SourcePathManager.DEFAULT_POLLING_INTERVAL)));
                mPollingIntervalTextField.setColumns(5);
                pollingIntervalPanel.add(mPollingIntervalTextField, BorderLayout.CENTER);
                
                return pollingIntervalPanel;
        }
        
        
        private void toggleClassPathChoiceMethod(boolean toggle) 
        {
                mInstructionLabel.setEnabled(toggle);
                mSourcePathList.setEnabled(toggle);
                mAddButton.setEnabled(toggle);
                mRemoveButton.setEnabled(toggle);
        }
        
        private void setProperties()
        {
                jEdit.setProperty(OpenItProperties.SOURCE_PATH_STRING, getSourcePathString());
                jEdit.setIntegerProperty(OpenItProperties.SOURCE_PATH_POLLING_INTERVAL, getPollingIntervalValue());
                jEdit.setProperty(OpenItProperties.EXCLUDES_REGULAR_EXPRESSION, mExcludesRegularExpressionTextField.getText());
                jEdit.setProperty(OpenItProperties.EXCLUDES_DIRECTORIES_REGULAR_EXPRESSION, mExcludesDirectoriesRegularExpressionTextField.getText());
                jEdit.setBooleanProperty(OpenItProperties.IGNORE_CASE_EXCLUDES_DIRECTORIES_REGULAR_EXPRESSION, mIgnoreCaseExcludeDirectoriesCheckBox.isSelected()); 
                jEdit.setBooleanProperty(OpenItProperties.IGNORE_CASE_EXCLUDES_FILE_REGULAR_EXPRESSION, mIgnoreCaseExcludeFilesCheckBox.isSelected());
        }
        
        private String getSourcePathString() 
        {
                String result = mSourcePathList.getPathElementsString();
                if (jEdit.getBooleanProperty(OpenItProperties.IMPORT_FILES_FROM_CURRENT_PROJECT)) {
                        if (isProjectViewerInstalled()) {
                                VPTProject project = ProjectViewer.getActiveProject(jEdit.getActiveView());
                                if (project != null) result = project.getRootPath();
                        }
                        
                }
                return result;
        }
        
        private boolean isProjectViewerInstalled() 
        {
                EditPlugin pv = jEdit.getPlugin("projectviewer.ProjectPlugin", false);
                if (pv != null) {
                        return true;
                }
                return false;
        }
        
        private int getPollingIntervalValue()
        {
                // attempt to convert the string entered in the text field to an integer 
                try {
                        int pollingInterval = Integer.parseInt(mPollingIntervalTextField.getText());
                        return Math.max(SourcePathManager.MINIMUM_POLLING_INTERVAL, pollingInterval);
                } catch (NumberFormatException nfe) {
                        // if we get a number format exception, then just return the current
                        // value set in the property
                        return jEdit.getIntegerProperty(OpenItProperties.SOURCE_PATH_POLLING_INTERVAL, 
                                SourcePathManager.DEFAULT_POLLING_INTERVAL);
                }
        }
        
        
        //
        // Inner Class
        //
        
        private class SourcePathList extends JList
        {
                // default list model
                private DefaultListModel mDefaultListModel;
                
                public SourcePathList()
                {
                        super();
                        init();
                }
                
                private void init()
                {
                        // set the model
                        mDefaultListModel = new DefaultListModel();
                        setModel(mDefaultListModel);
                }
                
                public void addPathElement(String elementName) 
                {
                        if (!mDefaultListModel.contains(elementName)) {
                                mDefaultListModel.addElement(elementName);
                        }
                }
                
                public void addPathElements(String elementNames) 
                {
                        // tokenize on the element string
                        StringTokenizer tokenizer = new StringTokenizer(elementNames, File.pathSeparator);
                        while (tokenizer.hasMoreTokens()) {
                                addPathElement(tokenizer.nextToken());
                        }
                }
                
                public void removePathElement(String elementName)
                {
                        if (mDefaultListModel.contains(elementName)) {
                                mDefaultListModel.removeElement(elementName);
                        }
                }
                
                public String getPathElementsString()
                {
                        // for each element, add to buffer and delimit with path separator
                        StringBuffer buffer = new StringBuffer();
                        Enumeration enumeration = mDefaultListModel.elements();
                        while (enumeration.hasMoreElements()) {
                                buffer.append(enumeration.nextElement());
                                if (enumeration.hasMoreElements()) {
                                        buffer.append(File.pathSeparator);
                                }
                        }
                        return buffer.toString();
                }
        }
        
        //
        // ActionListener implementation
        //
        
        public void actionPerformed(ActionEvent ae) 
        {
                if (ae.getSource() == mAddButton) {
                        
                        // lazily create file chooser
                        if (msSourcePathElementFileChooser == null) {
                                msSourcePathElementFileChooser = new JFileChooser();
                                msSourcePathElementFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                msSourcePathElementFileChooser.setMultiSelectionEnabled(true);
                                msSourcePathElementFileChooser.setApproveButtonText("Choose");
                                msSourcePathElementFileChooser.setDialogTitle("Choose Search Path(s)");
                                msSourcePathElementFileChooser.setFileFilter(new SearchPathElementFileFilter());
                        }
                        
                        // get the persisted directory string, and use this to set the directory
                        // on the file chooser (if the directory exists).
                        File currentDirectory = new File(jEdit.getProperty
                                (OpenItProperties.LAST_OPENED_FILE_CHOOSER_DIRECTORY, ""));
                        if (currentDirectory.exists()) {
                                msSourcePathElementFileChooser.setCurrentDirectory(currentDirectory);
                        }
                        
                        int returnVal = msSourcePathElementFileChooser.showOpenDialog(jEdit.getActiveView());
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                                // get the current directory and persist it as a property
                                jEdit.setProperty(OpenItProperties.LAST_OPENED_FILE_CHOOSER_DIRECTORY, 
                                        msSourcePathElementFileChooser.getCurrentDirectory().getAbsolutePath());
                                
                                // get the file(s) selected from the file chooser and add to search file list
                                File[] selectedFiles = msSourcePathElementFileChooser.getSelectedFiles();
                                for (int i = 0; i < selectedFiles.length; i++) {
                                        mSourcePathList.addPathElement(selectedFiles[i].getAbsolutePath());
                                }
                        }
                } else if (ae.getSource() == mRemoveButton) {
                        // get selected values and remove them from them
                        Object[] selectedValues = mSourcePathList.getSelectedValues();
                        for (int i = 0; i < selectedValues.length; i++) {
                                mSourcePathList.removePathElement(selectedValues[i].toString());
                        }
                }
                
        }
        
        private class SearchPathElementFileFilter extends FileFilter
        {
                public boolean accept(File f)
                {
                        return f.isDirectory();
                }
                
                public String getDescription()
                {
                        return jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.SearchPathsFileFilter");
                }
        }
}

