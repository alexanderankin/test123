/*
 * XSLTProcessor.java - GUI for performing XSL Transformations
 *
 * Copyright (c) 2002 Greg Merrill
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

package xslt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.text.MessageFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

/**
 * GUI for performing XSL Transformations. 
 */
public class XSLTProcessor extends JPanel {

  public XSLTProcessor (View theView) {
    super(new GridBagLayout());
    this.view = theView;

    // initialize components

    sourceDocumentTextField = new JTextField();
    sourceDocumentTextField.setEditable(false);
    String lastSource = jEdit.getProperty("XSLTProcessor.lastSource");
    if (lastSource == null) {
      sourceDocumentTextField.setText(jEdit.getProperty("XSLTProcessor.source.pleaseSelect"));
    } else {
      sourceDocumentTextField.setText(lastSource);
    }

    selectButton = new JButton(jEdit.getProperty("XSLTProcessor.select.button"));
    selectButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        String[] selections = GUIUtilities.showVFSFileDialog(view, null, JFileChooser.OPEN_DIALOG, false);
        if (selections != null) {
          sourceDocumentTextField.setText(selections[0]);
          jEdit.setProperty("XSLTProcessor.lastSource", selections[0]);
        }
        Container topLevelAncestor = XSLTProcessor.this.getTopLevelAncestor();
        if (topLevelAncestor instanceof JFrame) {
          ((JFrame)topLevelAncestor).toFront();
        }
      }
    });

    stylesheetsListModel = new DefaultListModel();
    List values = PropertyUtil.getEnumeratedProperty("XSLTProcessor.lastStylesheet", jEdit.getProperties());
    Iterator it = values.iterator();
    while (it.hasNext()) { stylesheetsListModel.addElement(it.next()); }
    stylesheetsList = new JList(stylesheetsListModel);
    stylesheetsList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    stylesheetsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged (ListSelectionEvent e) {
        boolean selectionExists = stylesheetsList.getSelectedIndex() != -1;
        deleteButton.setEnabled(selectionExists);
        upButton.setEnabled(selectionExists && (stylesheetsListModel.getSize() > 1)
          && (stylesheetsList.getSelectedIndex() != 0));
        downButton.setEnabled(selectionExists && (stylesheetsListModel.getSize() > 1)
          && (stylesheetsList.getSelectedIndex() < stylesheetsListModel.getSize()-1));
      }
    });

    addButton = new JButton(jEdit.getProperty("XSLTProcessor.add.button"));
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        String[] selections = GUIUtilities.showVFSFileDialog(view, null, JFileChooser.OPEN_DIALOG, false);
        if (selections != null) {
          stylesheetsListModel.addElement(selections[0]);
          transformButton.setEnabled(true);
          if ((stylesheetsList.getSelectedIndex() != -1)
            && (stylesheetsListModel.getSize() > 1)) {
            downButton.setEnabled(true);
          }
          PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet", 
            Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
        }
        Container topLevelAncestor = XSLTProcessor.this.getTopLevelAncestor();
        if (topLevelAncestor instanceof JFrame) {
          ((JFrame)topLevelAncestor).toFront();
        }
      }
    });

    deleteButton = new JButton(jEdit.getProperty("XSLTProcessor.delete.button"));
    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        stylesheetsListModel.remove(stylesheetsList.getSelectedIndex());
        if (stylesheetsListModel.getSize() > 0) {
          stylesheetsList.setSelectedIndex(0);
        }
        else { 
          deleteButton.setEnabled(false);
          transformButton.setEnabled(false);
        }
        PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet", 
          Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
      }
    });
    deleteButton.setEnabled(false);

    upButton = new JButton(jEdit.getProperty("XSLTProcessor.up.button"));
    upButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        int selectedIndex = stylesheetsList.getSelectedIndex();
        Object selected = stylesheetsListModel.get(selectedIndex);
        stylesheetsListModel.remove(selectedIndex);
        stylesheetsListModel.insertElementAt(selected, selectedIndex-1);
        stylesheetsList.setSelectedIndex(selectedIndex-1);
        PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet", 
          Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
      }
    });
    upButton.setEnabled(false);

    downButton = new JButton(jEdit.getProperty("XSLTProcessor.down.button"));
    downButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        int selectedIndex = stylesheetsList.getSelectedIndex();
        Object selected = stylesheetsListModel.get(selectedIndex);
        stylesheetsListModel.remove(selectedIndex);
        stylesheetsListModel.insertElementAt(selected, selectedIndex+1);
        stylesheetsList.setSelectedIndex(selectedIndex+1);
        PropertyUtil.setEnumeratedProperty("XSLTProcessor.lastStylesheet", 
          Arrays.asList(stylesheetsListModel.toArray()), jEdit.getProperties());
      }
    });
    downButton.setEnabled(false);

    transformButton = new JButton(jEdit.getProperty("XSLTProcessor.transform.button"));
    transformButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent evt) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String docFileName = null;
        String stylesheetFileName = null;
        try {
          TransformerFactory transformerFactory = new TransformerFactoryImpl();
          docFileName = (String)sourceDocumentTextField.getText();
          String docBeingTransformed = null;
          for (int i=0; i < stylesheetsListModel.getSize(); i++) {
            stylesheetFileName = (String)stylesheetsListModel.getElementAt(i);
            StreamSource stylesheetSource = new StreamSource(new FileReader(stylesheetFileName));
            stylesheetSource.setSystemId(stylesheetFileName);
            Templates templates = transformerFactory.newTemplates(stylesheetSource);
            Transformer transformer = templates.newTransformer();
            StreamSource docSource = (docBeingTransformed == null)
              ? new StreamSource(new FileReader(docFileName))
              : new StreamSource(new StringReader(docBeingTransformed));
            docSource.setSystemId(docFileName);
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            transformer.transform(docSource, result);
            docBeingTransformed = stringWriter.toString();
          }
          Buffer newBuffer = jEdit.newFile(view);
          newBuffer.insert(0, docBeingTransformed);
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        catch (Exception e) {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          if (stylesheetFileName == null) {
            XSLTPlugin.processException(e, jEdit.getProperty("XSLTProcessor.error.preProcessProblem"), XSLTProcessor.this);
          }
          else {
            String msg = MessageFormat.format(jEdit.getProperty("XSLTProcessor.error.stylesheetProblem"),
              new Object[]{stylesheetFileName});
            XSLTPlugin.processException(e, msg, XSLTProcessor.this);
          }
        }
      }
    });
    transformButton.setEnabled(stylesheetsListModel.size() > 0);

    // perform layout 

    Insets insets = new Insets(4, 4, 4, 4);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = insets;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.source.label")), gbc);

    gbc = new GridBagConstraints();
    gbc.weightx = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = insets;
    add(sourceDocumentTextField, gbc);

    gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = insets;
    add(selectButton, gbc);
    
    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.gridheight = 5;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = insets;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.stylesheets.label")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.gridheight = 5;
    gbc.weightx = gbc.weighty = 5;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = insets;
    add(new JScrollPane(stylesheetsList), gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.insets = new Insets(4, 4, 0, 4);
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(addButton, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 4, 4, 4);
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(deleteButton, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 3;
    gbc.insets = new Insets(4, 4, 0, 4);
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(upButton, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 4;
    gbc.insets = new Insets(0, 4, 4, 4);
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(downButton, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 5;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.insets = insets;
    add(new JPanel(), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.insets = insets;
    gbc.anchor = GridBagConstraints.CENTER;
    add(transformButton, gbc);
  }

  /**
   * @return "Transform" button
   */
  public JButton getTransformButton () { return transformButton; }

  private View view;
  private JTextField sourceDocumentTextField;
  private JButton selectButton;
  private DefaultListModel stylesheetsListModel;
  private JList stylesheetsList;
  private JButton addButton;
  private JButton deleteButton;
  private JButton upButton;
  private JButton downButton;
  private JButton transformButton;

}

