/*
 *  Copyright (c) 2008 TownsfolkDesigns.com
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.townsfolkdesigns.jedit.plugins.scripting.forms;

import com.townsfolkdesigns.jedit.plugins.scripting.ScriptEnginePlugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 *
 * @author elberry
 */
public class CreateMacroForm extends JPanel {

   private static final Dimension INPUT_FIELD_SIZE = new Dimension(300, 30);
   public JTextField directoryField;
   public JLabel directoryLabel;
   public GridBagConstraints gbc;
   public JComboBox languageField;
   public JLabel languageLabel;
   public GridBagLayout layout;
   public JTextField nameField;

   public JLabel nameLabel;

   public CreateMacroForm() {
      initComponents();
      initLayout();
   }

   public static void main(String[] args) {
      new CreateMacroForm().show(null);
   }

   public void show(Component parent) {
      int value = JOptionPane.showConfirmDialog(parent, this, "Create New Macro", JOptionPane.OK_CANCEL_OPTION, -1);

   }

   private void initComponents() {
      nameLabel = new JLabel("Name:");
      directoryLabel = new JLabel("Directory:");
      languageLabel = new JLabel("Language:");
      nameField = new JTextField();
      nameField.setSize(INPUT_FIELD_SIZE);
      nameField.setPreferredSize(INPUT_FIELD_SIZE);
      directoryField = new JTextField();
      directoryField.setSize(INPUT_FIELD_SIZE);
      directoryField.setPreferredSize(INPUT_FIELD_SIZE);
      languageField = new JComboBox(ScriptEnginePlugin.getRegisteredModes().toArray());
      languageField.setSize(INPUT_FIELD_SIZE);
      languageField.setPreferredSize(INPUT_FIELD_SIZE);
   }

   private void initLayout() {
      layout = new GridBagLayout();
      gbc = new GridBagConstraints();
      setLayout(layout);

      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 0.0f;
      gbc.weighty = 0.0f;
      gbc.fill = GridBagConstraints.BOTH;
      layout.setConstraints(nameLabel, gbc);
      add(nameLabel);
      gbc.gridy = 1;
      layout.setConstraints(directoryLabel, gbc);
      add(directoryLabel);
      gbc.gridy = 2;
      layout.setConstraints(languageLabel, gbc);
      add(languageLabel);

      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.weightx = 1.0f;
      layout.setConstraints(nameField, gbc);
      add(nameField);
      gbc.gridy = 1;
      layout.setConstraints(directoryField, gbc);
      add(directoryField);
      gbc.gridy = 2;

      //gbc.insets = new Insets(0, 5, 0, 5);
      layout.setConstraints(languageField, gbc);
      add(languageField);
   }

}
