/*
 * AcceleratorOptionPane.java
 * Copyright (C) 2002 Calvin Yu, Steve Jakob
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
package templates;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;

/**
 * An option pane for configuring template accelerators.
 */
public class AcceleratorOptionPane extends AbstractOptionPane
   implements ListSelectionListener, ActionListener, TreeSelectionListener
{

   private final static String ADD_ACTION = "add";
   private final static String REMOVE_ACTION = "remove";
   private final static String SAVE_ACTION = "save";

   private JComboBox modes;
   private JList accelerators;
   private TemplateTree templates;
   private JButton removeButton;
   private JButton saveButton;

   /**
    * Create a new <code>AcceleratorOptionPane</code>.
    */
   public AcceleratorOptionPane()
   {
		super("Templates.accelerators");
   }

   //{{{ TreeSelectionListener Method
   /**
    * Handle a tree selection change.
    */
   public void valueChanged(TreeSelectionEvent evt)
   {
      saveButton.setEnabled(templates.isTemplateSelected());
   }
   //}}}

   //{{{ ListSelectionListener Method
   /**
    * Accelerator selection value changed.
    */
   public void valueChanged(ListSelectionEvent evt)
   {
      templates.setEnabled(getSelectedAccelerator() != null);
      removeButton.setEnabled(getSelectedAccelerator() != null);
      String path = AcceleratorManager.getInstance()
         .findTemplatePath(getSelectedMode(), getSelectedAccelerator());
      templates.setSelectedTemplate(path);
      saveButton.setEnabled(false);
   }
   //}}}

   //{{{ ActionListener Method
   /**
    * Action performed.
    */
   public void actionPerformed(ActionEvent evt)
   {
      if (ADD_ACTION.equals(evt.getActionCommand())) {
         String accelerator = GUIUtilities.input(this,
                    "options.Templates.accelerators.input.accelerator",
                    null);
         AcceleratorManager.getInstance()
            .addAccelerator(getSelectedMode(), accelerator, null);
         loadAcceleratorsForMode(getSelectedMode());
         accelerators.setSelectedValue(accelerator, true);
      } else if (REMOVE_ACTION.equals(evt.getActionCommand())) {
         AcceleratorManager.getInstance()
            .removeAccelerator(getSelectedMode(), getSelectedAccelerator());
         loadAcceleratorsForMode(getSelectedMode());
      } else if (SAVE_ACTION.equals(evt.getActionCommand())) {
         saveAccelerator();
      } else {
         String modeName = getSelectedMode();
         if (modeName == null) {
            modes.setSelectedIndex(0);
         }
         loadAcceleratorsForMode(modeName);
      }
   }
   //}}}

   /**
    * Save the accelerator.
    */
   public void saveAccelerator()
   {
      String accelerator = getSelectedAccelerator();
      if (!templates.isTemplateSelected()) {
         GUIUtilities.error(this,
		 		"plugin.TemplatesPlugin.error.invalid-template-path", null);
      } else {
         String path = templates.getSelectedTemplate();
         AcceleratorManager.getInstance().addAccelerator(getSelectedMode(),
                                                         accelerator,
                                                         path);
      }
      saveButton.setEnabled(false);
   }

   /**
    * Returns the selected keyword.
    */
   public String getSelectedAccelerator()
   {
      if (accelerators.getModel().getSize() == 0) {
         return null;
      }
      return (String) accelerators.getSelectedValue();
   }

   /**
    * Returns the selected mode.
    */
   public String getSelectedMode()
   {
      if (modes.getItemCount() == 0) {
         return null;
      }
      return (String) modes.getSelectedItem();
   }

   /**
    * Initialize this pane.
    */
   protected void _init()
   {
      modes = new JComboBox(getModeNames());
      modes.addActionListener(this);
      addComponent(jEdit.getProperty(
	  		"plugin.TemplatesPlugin.mode.label"), modes);

      JPanel acceleratorPanel = new JPanel(new GridBagLayout());
      acceleratorPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
      GridBagConstraints gbc = new GridBagConstraints();

      accelerators = new JList(new DefaultListModel());
      accelerators.setVisibleRowCount(7);
      accelerators.setPrototypeCellValue("XXXXXXXXXXXXXXX");
      accelerators.addListSelectionListener(this);
      gbc.anchor = gbc.WEST;
      gbc.fill = gbc.BOTH;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.insets = new Insets(0, 0, 11, 11);
      gbc.weightx = .2;
      gbc.weighty = .998;
      acceleratorPanel.add(new JScrollPane(accelerators), gbc);

      JButton add = new JButton(jEdit.getProperty(
	  		"plugin.TemplatesPlugin.add-accelerator.label"));
      add.setActionCommand(ADD_ACTION);
      add.addActionListener(this);
      gbc.fill = gbc.HORIZONTAL;
      gbc.gridy++;
      gbc.weighty = .001;
      acceleratorPanel.add(add, gbc);

      saveButton = new JButton(jEdit.getProperty(
	  		"plugin.TemplatesPlugin.save-accelerator.label"));
      saveButton.setActionCommand(SAVE_ACTION);
      saveButton.addActionListener(this);
      saveButton.setEnabled(false);
      gbc.gridy++;
      acceleratorPanel.add(saveButton, gbc);

      removeButton = new JButton(jEdit.getProperty(
	  		"plugin.TemplatesPlugin.remove-accelerator.label"));
      removeButton.setEnabled(false);
      removeButton.setActionCommand(REMOVE_ACTION);
      removeButton.addActionListener(this);
      gbc.gridy++;
      gbc.insets = new Insets(0, 0, 0, 11);
      acceleratorPanel.add(removeButton, gbc);

      templates = new TemplateTree();
      templates.setEnabled(false);
      templates.addTreeSelectionListener(this);
      gbc.fill = gbc.BOTH;
      gbc.gridy = 0;
      gbc.gridx++;
      gbc.gridheight = 4;
      gbc.insets = new Insets(0, 0, 0, 0);
      gbc.weightx = .8;
      gbc.weighty = 1;
      acceleratorPanel.add(new JScrollPane(templates), gbc);

      addComponent(acceleratorPanel);
      GridBagConstraints cons = gridBag.getConstraints(acceleratorPanel);
      cons.fill = cons.BOTH;
      gridBag.setConstraints(acceleratorPanel, cons);

      modes.setSelectedIndex(0);
   }

   /**
    * Load the accelerators for the given mode.
    */
   private void loadAcceleratorsForMode(String modeName)
   {
      Collection col = AcceleratorManager.getInstance().getAccelerators(modeName);
      accelerators.setEnabled(!col.isEmpty());
      List list = new ArrayList(col);
      Collections.sort(list);
      DefaultListModel listModel = (DefaultListModel) accelerators.getModel();
      listModel.clear();
      for (Iterator i = list.iterator(); i.hasNext();) {
         listModel.addElement(i.next());
      }
      accelerators.setSelectedIndex(0);
   }

   /**
    * Returns an array of mode names.
    */
   private static String[] getModeNames()
   {
      Mode[] modes = jEdit.getModes();
      String[] names = new String[modes.length];
      for (int i=0; i<modes.length; i++) {
         names[i] = modes[i].getName();
      }
      return names;
   }

}
