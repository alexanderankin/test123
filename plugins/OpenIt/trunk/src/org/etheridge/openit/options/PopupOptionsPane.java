/*
 * OpenIt jEdit Plugin (PopupOptionsPane.java) 
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.SourcePathManager;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * OptionPane for the popup window options.
 */
public class PopupOptionsPane extends AbstractOptionPane   
{
  private JCheckBox mAllowSubstringMatchesCheckBox;
  private JCheckBox mClearWindowCheckBox;
  private JCheckBox mCaseSensitiveCheckBox;

  private JCheckBox mDisplayDirectoriesCheckBox;
  private JCheckBox mDisplayExtensionsCheckBox;
  private JCheckBox mDisplayIconsCheckBox;
  private JCheckBox mDisplaySizeCheckBox;
  
  private JCheckBox mJavaFileDisplayPackagesCheckBox;
  private JCheckBox mJavaFileDisplayDirectoriesCheckBox;
  
  public PopupOptionsPane()
  {
    super("OpenIt.PopupOptionsPane");
  }
  
	public void _init() 
  {
    setLayout(new BorderLayout());
    
    // top panel
    JPanel topPlacerPanel = new JPanel(new BorderLayout());
    topPlacerPanel.add(createSubstringMatchesPanel(), BorderLayout.NORTH);
    JPanel topGridPlacerPanel = new JPanel(new GridLayout(2,1));
    topGridPlacerPanel.add(createClearWindowPanel());
    topGridPlacerPanel.add(createCaseSensitivePanel());
    topPlacerPanel.add(topGridPlacerPanel);
        
    // bottom panel
    JPanel placerPanel = new JPanel(new BorderLayout());
    placerPanel.add(topPlacerPanel, BorderLayout.NORTH);
    placerPanel.add(createDisplayOptionsPanel(), BorderLayout.CENTER);
    placerPanel.add(createJavaFileOptionsPanel(), BorderLayout.SOUTH);
    add(placerPanel, BorderLayout.NORTH);
	}
    
  public void _save()
  {
    // if the substring matching property has changed, then we want to refresh
    // the sourcepath
    if (jEdit.getBooleanProperty(OpenItProperties.ALLOW_SUBSTRING_MATCHING, true) !=
        mAllowSubstringMatchesCheckBox.isSelected()) {
      setProperties();
      SourcePathManager.getInstance().refreshSourcePath();
    } else {
      setProperties();
    }
  }
  
  //
  // GUI Initialization
  //
  
  private JPanel createSubstringMatchesPanel()
  {
    JPanel substringMatchesPanel = new JPanel(new BorderLayout(6,6));
    substringMatchesPanel.setBorder(BorderFactory.createCompoundBorder
      (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.PopupOptionsPane.SubstringMatching.title")), 
       BorderFactory.createEmptyBorder(6,6,6,6)));
   
    JPanel labelPanel = new JPanel(new GridLayout(2,1));
    JLabel label1 = new JLabel(jEdit.getProperty("options.OpenIt.PopupOptionsPane.SubstringMatching.instruction.label.1")); 
    JLabel label2 = new JLabel(jEdit.getProperty("options.OpenIt.PopupOptionsPane.SubstringMatching.instruction.label.2")); 
    labelPanel.add(label1);
    labelPanel.add(label2);
    substringMatchesPanel.add(labelPanel, BorderLayout.NORTH);
   
    mAllowSubstringMatchesCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.SubstringMatching.label"));
    mAllowSubstringMatchesCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.ALLOW_SUBSTRING_MATCHING, true)); // default to true
    substringMatchesPanel.add(mAllowSubstringMatchesCheckBox);
    
    return substringMatchesPanel;
  }
  
  private JPanel createClearWindowPanel()
  {
    JPanel clearWindowPanel = new JPanel(new BorderLayout(6,6));
    clearWindowPanel.setBorder(BorderFactory.createCompoundBorder
      (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.PopupOptionsPane.ClearPopup.title")), 
       BorderFactory.createEmptyBorder(6,6,6,6)));
   
    mClearWindowCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.ClearPopup.ShouldClearPopup"));
    mClearWindowCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.POP_UP_CLEAN_ON_VISIBLE, true)); // default to true
    clearWindowPanel.add(mClearWindowCheckBox);
    
    return clearWindowPanel;
  }
  
  private JPanel createCaseSensitivePanel()
  {
    JPanel caseSensitivePanel = new JPanel(new BorderLayout(6,6));
    caseSensitivePanel.setBorder(BorderFactory.createCompoundBorder
      (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.PopupOptionsPane.CaseSensitive.title")), 
       BorderFactory.createEmptyBorder(6,6,6,6)));
   
    mCaseSensitiveCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.CaseSensitive.label"));
    mCaseSensitiveCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.POP_UP_CASE_SENSITIVE_FILE_MATCHING, false)); // default to true
    caseSensitivePanel.add(mCaseSensitiveCheckBox);
    
    return caseSensitivePanel;
  }
  
  private JPanel createDisplayOptionsPanel()
  {
    JPanel displayOptionsPanel = new JPanel(new GridLayout(4,1));
    displayOptionsPanel.setBorder(BorderFactory.createCompoundBorder
      (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.PopupOptionsPane.Display.title")), 
       BorderFactory.createEmptyBorder(6,6,6,6)));
     
    mDisplayDirectoriesCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.Display.DisplayDirectory"));
    mDisplayDirectoriesCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.DISPLAY_DIRECTORIES, false));
    displayOptionsPanel.add(mDisplayDirectoriesCheckBox);
    
    mDisplayExtensionsCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.Display.DisplayExtensions"));
    mDisplayExtensionsCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.DISPLAY_EXTENSIONS, true));
    displayOptionsPanel.add(mDisplayExtensionsCheckBox);
    
    mDisplayIconsCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.Display.DisplayIcons"));
    mDisplayIconsCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.DISPLAY_ICONS, true));
    displayOptionsPanel.add(mDisplayIconsCheckBox);
    
    mDisplaySizeCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.Display.DisplaySize"));
    mDisplaySizeCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.DISPLAY_SIZE, false));
    displayOptionsPanel.add(mDisplaySizeCheckBox);
    
    return displayOptionsPanel;
  }
  
  private JPanel createJavaFileOptionsPanel()
  {
    JPanel javaFileOptionsPanel = new JPanel(new GridLayout(2,1));
    javaFileOptionsPanel.setBorder(BorderFactory.createCompoundBorder
      (BorderFactory.createTitledBorder(jEdit.getProperty("options.OpenIt.PopupOptionsPane.JavaFileDisplay.title")), 
       BorderFactory.createEmptyBorder(6,6,6,6)));
     
    mJavaFileDisplayPackagesCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.JavaFileDisplay.DisplayPackages"));
    mJavaFileDisplayPackagesCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.JAVA_FILE_DISPLAY_PACKAGES, true));
    javaFileOptionsPanel.add(mJavaFileDisplayPackagesCheckBox);
    
    mJavaFileDisplayDirectoriesCheckBox = new JCheckBox(jEdit.getProperty("options.OpenIt.PopupOptionsPane.JavaFileDisplay.DisplayDirectory"));
    mJavaFileDisplayDirectoriesCheckBox.setSelected(jEdit.getBooleanProperty(OpenItProperties.JAVA_FILE_DISPLAY_DIRECTORIES, false));
    javaFileOptionsPanel.add(mJavaFileDisplayDirectoriesCheckBox);
   
    return javaFileOptionsPanel;
  }
 
  private void setProperties()
  {
    jEdit.setBooleanProperty(OpenItProperties.POP_UP_CLEAN_ON_VISIBLE, mClearWindowCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.POP_UP_CASE_SENSITIVE_FILE_MATCHING, mCaseSensitiveCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.DISPLAY_DIRECTORIES, mDisplayDirectoriesCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.DISPLAY_EXTENSIONS, mDisplayExtensionsCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.DISPLAY_ICONS, mDisplayIconsCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.DISPLAY_SIZE, mDisplaySizeCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.JAVA_FILE_DISPLAY_PACKAGES, mJavaFileDisplayPackagesCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.JAVA_FILE_DISPLAY_DIRECTORIES, mJavaFileDisplayDirectoriesCheckBox.isSelected());
    jEdit.setBooleanProperty(OpenItProperties.ALLOW_SUBSTRING_MATCHING, mAllowSubstringMatchesCheckBox.isSelected());
  }

}
