/*
 *  XSLTProcessor.java - GUI for performing XSL Transformations
 *
 *  Copyright (c) 2002 Greg Merrill
 *  Portions copyright (c) 2002 Robert McKinnon
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package xslt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * GUI for performing XSL Transformations.
 *
 *@author   Greg Merrill, Robert McKinnon
 */
public class XSLTProcessor extends JPanel {

  private View view;
  private JTextField sourceDocumentTextField;
  private JTextField resultDocumentTextField;
  private JButton selectButton;
  private JButton nameResultButton;
  private DefaultListModel stylesheetsListModel;
  private JList stylesheetsList;
  private JButton addButton;
  private JButton deleteButton;
  private JButton upButton;
  private JButton downButton;
  private JButton transformButton;
  private double widestButtonWidth = -1;



  /**
   * Constructor for the XSLTProcessor object.
   *
   *@param theView
   */
  public XSLTProcessor(View theView) {
    super(new GridBagLayout());
    this.view = theView;
    this.sourceDocumentTextField = initFileTextField("XSLTProcessor.lastSource", "XSLTProcessor.source.pleaseSelect");
    this.resultDocumentTextField = initFileTextField("XSLTProcessor.lastResult", "XSLTProcessor.result.pleaseName");
    this.selectButton = initFileChooserButton("XSLTProcessor.select.button");
    this.nameResultButton = initFileChooserButton("XSLTProcessor.name.button");

    this.stylesheetsListModel = initStylesheetListModel();
    this.stylesheetsList = initStylesheetList();

    this.addButton = initAddButton();
    this.deleteButton = initDeleteButton();
    this.upButton = initMoveButton("XSLTProcessor.up.button");
    this.downButton = initMoveButton("XSLTProcessor.down.button");
    this.transformButton = initTransformButton();

    setSize(addButton);
    setSize(deleteButton);
    setSize(upButton);
    setSize(downButton);
    setSize(selectButton);
    setSize(nameResultButton);

    addComponents();
  }


  private double getWidest(double widest, JButton button) {
    if(button.getPreferredSize().getWidth() > widest) {
      widest = button.getMinimumSize().getWidth();
    }
    return widest;
  }


  private Dimension getPreferredButtonDimension(JButton button) {
    if(widestButtonWidth == -1) {
      widestButtonWidth = getWidest(widestButtonWidth, this.addButton);
      widestButtonWidth = getWidest(widestButtonWidth, this.deleteButton);
      widestButtonWidth = getWidest(widestButtonWidth, this.downButton);
      widestButtonWidth = getWidest(widestButtonWidth, this.upButton);
      widestButtonWidth = getWidest(widestButtonWidth, this.nameResultButton);
      widestButtonWidth = getWidest(widestButtonWidth, this.selectButton);
    }

    return new Dimension((int)widestButtonWidth, (int)button.getPreferredSize().getHeight());
  }


  private void setSize(JButton button) {
    button.setMinimumSize(getPreferredButtonDimension(button));
    button.setPreferredSize(getPreferredButtonDimension(button));
  }


  /**
   * Returns transform button
   *
   *@return   The transformButton value
   */
  public JButton getTransformButton() {
    return transformButton;
  }


  private JTextField initFileTextField(String lastProperty, String descriptionProperty) {
    JTextField textField = new JTextField();
    textField.setEditable(false);
    String lastSource = jEdit.getProperty(lastProperty);

    if(lastSource == null) {
      textField.setText(jEdit.getProperty(descriptionProperty));
    } else {
      textField.setText(lastSource);
    }

    return textField;
  }


  private JButton initFileChooserButton(String propertyName) {
    JButton button = new JButton(jEdit.getProperty(propertyName));
    button.setActionCommand(jEdit.getProperty(propertyName));
    button.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String[] selections = GUIUtilities.showVFSFileDialog(view, null, JFileChooser.OPEN_DIALOG, false);
          if(selections != null) {
            if(e.getActionCommand() == jEdit.getProperty("XSLTProcessor.select.button")) {
              sourceDocumentTextField.setText(selections[0]);
              jEdit.setProperty("XSLTProcessor.lastSource", selections[0]);
            } else {
              resultDocumentTextField.setText(selections[0]);
              jEdit.setProperty("XSLTProcessor.lastResult", selections[0]);
            }
          }
          Container topLevelAncestor = XSLTProcessor.this.getTopLevelAncestor();
          if(topLevelAncestor instanceof JFrame) {
            ((JFrame)topLevelAncestor).toFront();
          }
        }
      });
    return button;
  }


  private DefaultListModel initStylesheetListModel() {
    DefaultListModel stylesheetsListModel = new DefaultListModel();
    List values = PropertyUtil.getEnumeratedProperty("XSLTProcessor.lastStylesheet", jEdit.getProperties());
    Iterator it = values.iterator();
    while(it.hasNext()) {
      stylesheetsListModel.addElement(it.next());
    }
    return stylesheetsListModel;
  }


  private JList initStylesheetList() {
    JList list = new JList(stylesheetsListModel);
    list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          boolean selectionExists = stylesheetsList.getSelectedIndex() != -1;
          deleteButton.setEnabled(selectionExists);
          upButton.setEnabled(selectionExists && (stylesheetsListModel.getSize() > 1)
              && (stylesheetsList.getSelectedIndex() != 0));
          downButton.setEnabled(selectionExists && (stylesheetsListModel.getSize() > 1)
              && (stylesheetsList.getSelectedIndex() < stylesheetsListModel.getSize() - 1));
        }
      });
    return list;
  }


  private JButton initAddButton() {
    JButton addButton = new JButton(jEdit.getProperty("XSLTProcessor.add.button"));
    addButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String[] selections = GUIUtilities.showVFSFileDialog(view, null, JFileChooser.OPEN_DIALOG, false);
          if(selections != null) {
            stylesheetsListModel.addElement(selections[0]);
            transformButton.setEnabled(true);
            if((stylesheetsList.getSelectedIndex() != -1)
                && (stylesheetsListModel.getSize() > 1)) {
              downButton.setEnabled(true);
            }
            PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet",
                Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
          }
          Container topLevelAncestor = XSLTProcessor.this.getTopLevelAncestor();
          if(topLevelAncestor instanceof JFrame) {
            ((JFrame)topLevelAncestor).toFront();
          }
        }
      });
    return addButton;
  }


  private JButton initDeleteButton() {
    JButton button = new JButton(jEdit.getProperty("XSLTProcessor.delete.button"));
    button.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          stylesheetsListModel.remove(stylesheetsList.getSelectedIndex());
          if(stylesheetsListModel.getSize() > 0) {
            stylesheetsList.setSelectedIndex(0);
          } else {
            deleteButton.setEnabled(false);
            transformButton.setEnabled(false);
          }
          PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet",
              Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
        }
      });
    button.setEnabled(false);
    return button;
  }


  private JButton initMoveButton(String propertyName) {
    JButton button = new JButton(jEdit.getProperty(propertyName));
    button.setActionCommand(jEdit.getProperty(propertyName));
    button.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          int move = 1;

          if(e.getActionCommand() == jEdit.getProperty("XSLTProcessor.up.button")) {
            move = -1;
          }

          int selectedIndex = stylesheetsList.getSelectedIndex();
          Object selected = stylesheetsListModel.get(selectedIndex);
          stylesheetsListModel.remove(selectedIndex);
          stylesheetsListModel.insertElementAt(selected, selectedIndex + move);
          stylesheetsList.setSelectedIndex(selectedIndex + move);
          PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet",
              Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
        }
      });
    button.setEnabled(false);
    return button;
  }


  private JButton initTransformButton() {
    JButton transformButton = new JButton(jEdit.getProperty("XSLTProcessor.transform.button"));
    transformButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          Date start = new Date();
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          String inputFile = null;
          String stylesheetFileName = null;
          Object[] stylesheets = stylesheetsListModel.toArray();

          try {
            inputFile = sourceDocumentTextField.getText();
            String docBeingTransformed = XSLTUtilities.transform(inputFile, stylesheets);

            Buffer newBuffer = jEdit.newFile(view);
            newBuffer.insert(0, docBeingTransformed);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            if(!resultDocumentTextField.getText().equals(jEdit.getProperty("XSLTProcessor.result.pleaseName"))) {
              newBuffer.save(view, resultDocumentTextField.getText());
            }

            Date end = new Date();
            long timeTaken = end.getTime() - start.getTime();
            Object[] param = {new Integer((int)timeTaken)};
            String status = jEdit.getProperty("XSLTProcessor.transform.status", param);
            Log.log(Log.MESSAGE, this, status);
          } catch(Exception e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if(stylesheetFileName == null) {
              XSLTPlugin.processException(e, jEdit.getProperty("XSLTProcessor.error.preProcessProblem"), XSLTProcessor.this);
            } else {
              String msg = MessageFormat.format(jEdit.getProperty("XSLTProcessor.error.stylesheetProblem"),
                  new Object[]{stylesheetFileName});
              XSLTPlugin.processException(e, msg, XSLTProcessor.this);
            }
          }
        }
      });
    transformButton.setEnabled(stylesheetsListModel.size() > 0);
    return transformButton;
  }


  private GridBagConstraints getConstraints(int gridy, Insets insets) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridy = gridy;
    constraints.insets = insets;
    return constraints;
  }


  private void addComponents() {
    addSourceComponents();
    addStylesheetComponents();
    addResultComponents();
    addTransformComponents();
  }


  private void addSourceComponents() {
    GridBagConstraints constraints = getConstraints(0, new Insets(4, 4, 0, 4));
    constraints.anchor = GridBagConstraints.WEST;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.source.label")), constraints);

    constraints = getConstraints(1, new Insets(4, 4, 4, 4));
    constraints.weightx = 5;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    add(sourceDocumentTextField, constraints);

    constraints = getConstraints(1, new Insets(2, 2, 4, 2));
    constraints.anchor = GridBagConstraints.EAST;
    add(selectButton, constraints);
  }


  private void addStylesheetComponents() {
    GridBagConstraints constraints = getConstraints(2, new Insets(4, 4, 0, 4));
    constraints.anchor = GridBagConstraints.NORTHWEST;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.stylesheets.label")), constraints);

    constraints = getConstraints(3, new Insets(4, 4, 4, 4));
    constraints.gridheight = 5;
    constraints.weightx = constraints.weighty = 5;
    constraints.fill = GridBagConstraints.BOTH;
    add(new JScrollPane(stylesheetsList), constraints);

    constraints = getConstraints(3, new Insets(2, 2, 0, 2));
    constraints.anchor = GridBagConstraints.EAST;
    add(addButton, constraints);

    constraints = getConstraints(4, new Insets(0, 2, 2, 2));
    constraints.anchor = GridBagConstraints.EAST;
    add(deleteButton, constraints);

    constraints = getConstraints(5, new Insets(2, 2, 0, 2));
    constraints.anchor = GridBagConstraints.EAST;
    add(upButton, constraints);

    constraints = getConstraints(6, new Insets(0, 2, 2, 2));
    constraints.anchor = GridBagConstraints.EAST;
    add(downButton, constraints);

    constraints = getConstraints(7, new Insets(4, 4, 4, 4));
    constraints.fill = GridBagConstraints.VERTICAL;
    add(new JPanel(), constraints);
  }


  private void addResultComponents() {
    GridBagConstraints constraints = getConstraints(8, new Insets(4, 4, 0, 4));
    constraints.anchor = GridBagConstraints.WEST;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.result.label")), constraints);

    constraints = getConstraints(9, new Insets(4, 4, 4, 4));
    constraints.weightx = 5;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    add(this.resultDocumentTextField, constraints);

    constraints = getConstraints(9, new Insets(2, 2, 4, 2));
    constraints.anchor = GridBagConstraints.EAST;
    add(nameResultButton, constraints);
  }


  private void addTransformComponents() {
    GridBagConstraints constraints = getConstraints(10, new Insets(4, 4, 4, 4));
    constraints.gridwidth = 2;
    constraints.anchor = GridBagConstraints.CENTER;
    add(transformButton, constraints);
  }

}

