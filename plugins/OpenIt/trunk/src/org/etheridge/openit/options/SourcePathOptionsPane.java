/*
 * OpenIt jEdit Plugin (SourcePathOptionsPane.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
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
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.SourcePathManager;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * OptionPane for the source path options.
 */
public class SourcePathOptionsPane extends AbstractOptionPane   
{
  // gui components for user defined classpath section
  private JTextArea mSourcePathTextArea;
  
  private JTextField mPollingIntervalTextField;

  public SourcePathOptionsPane()
  {
    super("OpenIt.SourcePathOptionPane");
  }
  
	public void _init() 
  {
    setLayout(new BorderLayout());
    
    JPanel placerPanel = new JPanel(new BorderLayout());
    placerPanel.add(createSourcePathPanel(), BorderLayout.CENTER);
    placerPanel.add(createRefreshSourcePathPanel(), BorderLayout.SOUTH);
    add(placerPanel, BorderLayout.NORTH);
	}
    
  public void _save()
  {
    if (propertiesHaveChanged()) {
      setProperties();
      SourcePathManager.getInstance().refreshSourcePath();
    }
  }
  
  //
  // GUI Initialization
  //
  
  private JPanel createSourcePathPanel()
  {
    JPanel userDefinedClassPathPanel = new JPanel(new BorderLayout(6,6));
    userDefinedClassPathPanel.setBorder(BorderFactory.createCompoundBorder
      (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.title")), 
       BorderFactory.createEmptyBorder(6,6,6,6)));
   
    // text to explain what to put here
    JPanel instructionPanel = new JPanel(new GridLayout(2,1));
    JLabel instructionLabel = new JLabel(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.instruction.label"));
    JLabel instructionLabel2 = new JLabel(jEdit.getProperty("options.OpenIt.SourcePathOptionPane.SourcePath.instruction2.label"));
    instructionPanel.add(instructionLabel);
    instructionPanel.add(instructionLabel2);
    userDefinedClassPathPanel.add(instructionPanel, BorderLayout.NORTH);
    
    // text area for user-defined classpath textarea
    mSourcePathTextArea = new JTextArea();
    mSourcePathTextArea.setRows(10);
    mSourcePathTextArea.setColumns(20);
    mSourcePathTextArea.setText(jEdit.getProperty(OpenItProperties.SOURCE_PATH_STRING, ""));
    mSourcePathTextArea.setCaretPosition(0);
    mSourcePathTextArea.setLineWrap(true);
    userDefinedClassPathPanel.add(new JScrollPane(mSourcePathTextArea), BorderLayout.CENTER);
    
    return userDefinedClassPathPanel;
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
 
 
 
  private boolean propertiesHaveChanged()
  {
    String currentSourcePath = jEdit.getProperty(OpenItProperties.SOURCE_PATH_STRING, "");
    if (!currentSourcePath.equals(mSourcePathTextArea.getText()) ||
        jEdit.getIntegerProperty(OpenItProperties.SOURCE_PATH_POLLING_INTERVAL, SourcePathManager.DEFAULT_POLLING_INTERVAL) != getPollingIntervalValue()) {
      return true;
    }
    return false;
  }
  
  private void setProperties()
  {
    jEdit.setProperty(OpenItProperties.SOURCE_PATH_STRING, mSourcePathTextArea.getText());
    jEdit.setIntegerProperty(OpenItProperties.SOURCE_PATH_POLLING_INTERVAL, getPollingIntervalValue());
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
}
