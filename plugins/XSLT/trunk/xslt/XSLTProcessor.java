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
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;

/**
 * GUI for performing XSL Transformations. 
 */
public class XSLTProcessor extends JPanel implements EBComponent {

  public XSLTProcessor (View theView) {
    super(new GridBagLayout());
    this.view = theView;
    EditBus.addToBus(this);

    Insets insets = new Insets(4, 4, 4, 4);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = insets;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.source.label")), gbc);

    gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = insets;
    sourceModel = new DefaultComboBoxModel(jEdit.getBuffers());
    add(new JComboBox(sourceModel), gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = insets;
    add(new JLabel(jEdit.getProperty("XSLTProcessor.stylesheets.label")), gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = insets;
    stylesheetsPanel = new StylesheetsPanel();
    add(stylesheetsPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = insets;
    transformButton = new JButton(jEdit.getProperty("XSLTProcessor.transform.button"));
    transformButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent evt) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Buffer stylesheetBuffer = null;
        try {
          TransformerFactory transformerFactory = new TransformerFactoryImpl();
          Buffer docBuffer = (Buffer)sourceModel.getSelectedItem();
          String docBeingTransformed = docBuffer.getText(0, docBuffer.getLength());
          for (int i=0; i < stylesheetsPanel.stylesheetsList.getModel().getSize(); i++) {
            stylesheetBuffer = (Buffer)stylesheetsPanel.stylesheetsList.getModel().getElementAt(i);
            String stylesheetText = stylesheetBuffer.getText(0, stylesheetBuffer.getLength());
            StreamSource stylesheetSource = new StreamSource(new StringReader(stylesheetText));
            stylesheetSource.setSystemId(stylesheetBuffer.getFile().getPath());
            Templates templates = transformerFactory.newTemplates(stylesheetSource);
            Transformer transformer = templates.newTransformer();
            StreamSource docSource = new StreamSource(new StringReader(docBeingTransformed));
            docSource.setSystemId(docBuffer.getFile().getPath());
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            transformer.transform(docSource, result);
            docBeingTransformed = stringWriter.toString();
          }
          Buffer newBuffer = jEdit.newFile(view);
          newBuffer.insert(0, docBeingTransformed);
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        catch (TransformerException e) {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          if (stylesheetBuffer == null) {
            XSLTPlugin.processException(e, jEdit.getProperty("XSLTProcessor.error.preProcessProblem"), XSLTProcessor.this);
          }
          else {
            String msg = MessageFormat.format(jEdit.getProperty("XSLTProcessor.error.stylesheetProblem"),
              new Object[]{stylesheetBuffer.getName()});
            XSLTPlugin.processException(e, msg, XSLTProcessor.this);
          }
        }
      }
    });
    transformButton.setEnabled(false);
    add(transformButton, gbc);
  }

  private View view;
  private DefaultComboBoxModel sourceModel;
  private StylesheetsPanel stylesheetsPanel;
  private JButton transformButton;

  /**
   * @see org.gjt.sp.jedit.EBComponent#handleMessage(EBMessage)
   */
  public void handleMessage (EBMessage msg) {
    if (msg instanceof BufferUpdate) {
      BufferUpdate bufferUpdate = (BufferUpdate)msg;
      if (BufferUpdate.CLOSED == bufferUpdate.getWhat()) {
        sourceModel.removeElement(bufferUpdate.getBuffer());
        stylesheetsPanel.buttonPanel.addStylesheetDialog.
          addStylesheetComboBoxModel.removeElement(bufferUpdate.getBuffer());
      }
      else if (BufferUpdate.LOADED == bufferUpdate.getWhat()) {
        Buffer buffer = bufferUpdate.getBuffer();
        if (sourceModel.getIndexOf(buffer) == -1) {
          sourceModel.addElement(buffer);
        }
        if (stylesheetsPanel.buttonPanel.addStylesheetDialog.
          addStylesheetComboBoxModel.getIndexOf(buffer) == -1) {
          stylesheetsPanel.buttonPanel.addStylesheetDialog.
            addStylesheetComboBoxModel.addElement(bufferUpdate.getBuffer());
        }
      }
    }
  }

  /**
   * Panel housing the "Stylesheets" list & accompanying buttons
   */
  class StylesheetsPanel extends JPanel {
    StylesheetsPanel () {
      super(new GridBagLayout());

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.weightx = gbc.weighty = 1;
      gbc.fill = GridBagConstraints.BOTH;
      stylesheetsListModel = new DefaultListModel();
      stylesheetsList = new JList(stylesheetsListModel);
      stylesheetsList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      add(new JScrollPane(stylesheetsList), gbc);

      gbc = new GridBagConstraints();
      gbc.weighty = 1;
      gbc.fill = GridBagConstraints.VERTICAL;
      gbc.insets = new Insets(0, 4, 0, 0);
      buttonPanel = new ButtonPanel();
      add(buttonPanel, gbc);
    }

    private JList stylesheetsList;
    private DefaultListModel stylesheetsListModel;
    private ButtonPanel buttonPanel;

    /**
     * Panel housing the "Add" & "Delete" buttons which accompany the
     * stylesheets list
     */
    class ButtonPanel extends JPanel {
      ButtonPanel () {
        super(new GridBagLayout());

        addStylesheetDialog = new AddStylesheetDialog();
        addStylesheetDialog.pack();
        JButton addButton = new JButton(jEdit.getProperty("XSLTProcessor.add.button"));
        addButton.addActionListener(new ActionListener() {
          public void actionPerformed (ActionEvent e) {
            addStylesheetDialog.setLocationRelativeTo(XSLTProcessor.this);
            addStylesheetDialog.show();
          }
        });
        final JButton deleteButton = new JButton(jEdit.getProperty("XSLTProcessor.delete.button"));
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
          }
        });
        deleteButton.setEnabled(false);

        stylesheetsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
          public void valueChanged (ListSelectionEvent e) {
            deleteButton.setEnabled(stylesheetsList.getSelectedIndex() != -1);
          }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(addButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(deleteButton, gbc);
  
        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(new JPanel(), gbc);
      }

      private AddStylesheetDialog addStylesheetDialog;

      /**
       * Dialog displayed when the "Add" button is pressed.
       */
      class AddStylesheetDialog extends JDialog {
        AddStylesheetDialog () {
          setTitle(jEdit.getProperty("XSLTProcessor.addStylesheet.title"));
          JPanel contentPane = (JPanel)getContentPane();
          contentPane.setLayout(new GridBagLayout());

          Insets insets = new Insets(4, 4, 4, 4);

          GridBagConstraints gbc = new GridBagConstraints();
          gbc.weightx = 1;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          gbc.insets = insets;
          addStylesheetComboBoxModel = new DefaultComboBoxModel(jEdit.getBuffers());
          contentPane.add(new JComboBox(addStylesheetComboBoxModel), gbc);

          JPanel buttons = new JPanel();

          gbc = new GridBagConstraints();
          JButton okButton = new JButton(jEdit.getProperty("XSLTProcessor.ok.button"));
          okButton.setDefaultCapable(true);
          okButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
              stylesheetsListModel.addElement(addStylesheetComboBoxModel.getSelectedItem());
              transformButton.setEnabled(true);
              AddStylesheetDialog.this.hide();
            }
          });
          getRootPane().setDefaultButton(okButton);
          buttons.add(okButton, gbc);

          gbc = new GridBagConstraints();
          JButton cancelButton = new JButton(jEdit.getProperty("XSLTProcessor.cancel.button"));
          cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
              AddStylesheetDialog.this.hide();
            }
          });
          buttons.add(cancelButton, gbc);

          gbc = new GridBagConstraints();
          gbc.gridy = 1;
          gbc.anchor = GridBagConstraints.CENTER;
          contentPane.add(buttons, gbc);
        }
        private DefaultComboBoxModel addStylesheetComboBoxModel;
      }

    }

  }

}

