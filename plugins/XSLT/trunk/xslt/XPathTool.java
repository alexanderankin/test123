/*
 * XPathTool.java - GUI for evaluating XPath expressions
 *
 * Copyright (c) 2002 Greg Merrill
 * Portions copyright (c) 2002 Robert McKinnon
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

import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.net.URL;

/**
 * GUI for evaluating XPath expressions.
 *
 * @author Greg Merrill
 * @author Robert McKinnon
 */
public class XPathTool extends JPanel implements ListSelectionListener {

  private final ExpressionPanel expressionPanel = new ExpressionPanel();
  private final EvaluatePanel evaluatePanel = new EvaluatePanel();
  private final JTextField dateTypeField = new JTextField();
  private final ResultsPanel resultValuePanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.value.label"));
  private final NodeSetResultsPanel nodeSetTablePanel = new NodeSetResultsPanel(jEdit.getProperty("XPathTool.result.summary.label"));
  private final XmlFragmentsPanel xmlFragmentsPanel = new XmlFragmentsPanel(jEdit.getProperty("XPathTool.result.xml.string.label"));
  private View view;


  public XPathTool(View view) {
    super(new GridBagLayout());
    this.view = view;

    JPanel dataTypePanel = new JPanel(new BorderLayout());
    dataTypePanel.add(new JLabel(jEdit.getProperty("XPathTool.result.xpath.evaluted.label")), BorderLayout.NORTH);
    dataTypePanel.add(this.dateTypeField);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    add(expressionPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    add(evaluatePanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.BOTH;
    add(dataTypePanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 3;
    gbc.weightx = 1;
    gbc.weighty = 6;
    gbc.fill = GridBagConstraints.BOTH;
    add(getSplitPane(), gbc);
  }


  private JSplitPane getSplitPane() {
    JSplitPane bottomSplitPane = getSplitPane(nodeSetTablePanel, xmlFragmentsPanel, 150);
    JSplitPane topSplitPane = getSplitPane(resultValuePanel, bottomSplitPane, 150);
    return topSplitPane;
  }


  private JSplitPane getSplitPane(final JComponent top, final JComponent bottom, final int dividerLocation) {
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(dividerLocation);
    return splitPane;
  }


  /**
   * Returns a message containing the data type selected by the XPath expression.
   * Note: there are four data types in the XPath 1.0 data model: node-set, string,
   * number, and boolean.
   *
   * @param xObject XObject to be converted
   * @return string describing the selected data type
   */
  private String getDataTypeMessage(XObject xObject) throws TransformerException {
    String typeMessage = null;

    if(xObject.getType() == XObject.CLASS_NODESET) {
      NodeSetDTM nodeSet = xObject.mutableNodeset();

      Object[] messageArgs = new Object[]{new Integer(nodeSet.size())};
      typeMessage = MessageFormat.format(jEdit.getProperty("XPathTool.result.xpath.node-set"), messageArgs);
    } else {
      Object[] messageArgs = new Object[]{xObject.getTypeString().substring(1).toLowerCase()};
      typeMessage = MessageFormat.format(jEdit.getProperty("XPathTool.result.xpath.not-node-set"), messageArgs);
    }

    return typeMessage;
  }


  /**
   * Populates a node set table model with the results of an XPath evaluation, if
   * the results are of type node-set.
   * Note: there are four data types in the XPath 1.0 data model: node-set, string,
   * number, and boolean.
   *
   * @param xObject XObject containing results
   * @param tableModel the table model to populate
   */
  private void setNodeSetResults(XObject xObject, NodeSetTableModel tableModel) throws TransformerException {
    if(xObject.getType() == XObject.CLASS_NODESET) {
      NodeSetDTM nodeSet = xObject.mutableNodeset();
      tableModel.resetRows(nodeSet.size());
      boolean isNodeWithName = false;
      boolean isNodeWithValue = false;
      XPathNode node;

      for(int i = 0; i < nodeSet.size(); i++) {
        node = XPathNode.getXPathNode(nodeSet, i);

        if(node != null) {
          isNodeWithName = isNodeWithName || node.hasExpandedName();
          isNodeWithValue = isNodeWithValue || node.hasDomValue();

          tableModel.setNodeType(node.getType(), i);
          tableModel.setNodeName(node.getName(), i);
          tableModel.setNodeValue(node.getDomValue(), i);
        }
      }

      if(!isNodeWithName && !isNodeWithValue) {
        tableModel.removeNameOrValueColumn();
      } else if(!isNodeWithName) {
        tableModel.removeNameColumn();
      } else if(!isNodeWithValue) {
        tableModel.removeValueColumn();
      }
    } else {
      tableModel.resetRows(0);
    }
  }


  class JTextAreaWithoutTab extends JTextArea {
    public boolean isManagingFocus() {
      return false;
    }
  }

  /**
   * Panel housing the "XPath Expression" label & text area
   */
  class ExpressionPanel extends JPanel {
    JTextArea textArea = new JTextAreaWithoutTab();


    ExpressionPanel() {
      super(new BorderLayout());
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.add(new JLabel(jEdit.getProperty("XPathTool.xpathExpression.label")), BorderLayout.WEST);
      add(topPanel, BorderLayout.NORTH);
      String text = jEdit.getProperty("XPathTool.lastExpression");
      textArea.setText((text == null) ? "" : text);
      textArea.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
          updateProps();
        }


        public void insertUpdate(DocumentEvent e) {
          updateProps();
        }


        public void removeUpdate(DocumentEvent e) {
          updateProps();
        }


        private void updateProps() {
          String text = ExpressionPanel.this.textArea.getText();
          jEdit.setProperty("XPathTool.lastExpression", (text == null) ? "" : text);
        }
      });

      add(new JScrollPane(textArea));
    }
  }


  private static boolean isNodeSet(XObject xObject) {
    return xObject.getType() == XObject.CLASS_NODESET;
  }


  /**
   * @return "Evaluate" buttons
   */
  public JButton getEvaluateButton() {
    return evaluatePanel.button;
  }


  /**
   * Panel housing the "Evaluate" button
   */
  class EvaluatePanel extends JPanel {
    private JButton button;


    EvaluatePanel() {
      String iconName = jEdit.getProperty("XPathTool.evaluate.button.icon");
      String toolTipText = jEdit.getProperty("XPathTool.evaluate.button.tooltip");

      URL url = XSLTProcessor.class.getResource(iconName);
      button = new JButton(new ImageIcon(url));
      button.setToolTipText(toolTipText);
      button.addActionListener(new EvaluateAction());

      Dimension dimension = new Dimension(76, 30);
      button.setMinimumSize(dimension);
      button.setPreferredSize(dimension);

      add(button);
    }
  }


  private class EvaluateAction implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      try {
        Buffer buffer = view.getBuffer();
        String text = buffer.getText(0, buffer.getLength());
        InputSource inputSource = new InputSource(new StringReader(text));
        inputSource.setSystemId(buffer.getFile().getPath());
        Document document = parse(inputSource);
        String expression = expressionPanel.textArea.getText();
        XObject xObject = XPathAPI.eval(document, expression);

        dateTypeField.setText(getDataTypeMessage(xObject));
        resultValuePanel.setText(xObject.xstr().toString());
        resultValuePanel.resetCaretPosition();

        if(isNodeSet(xObject)) {
          resultValuePanel.setLabelText(jEdit.getProperty("XPathTool.result.string-value.label"));
        } else {
          resultValuePanel.setLabelText(jEdit.getProperty("XPathTool.result.value.label"));
        }

        setNodeSetResults(xObject, nodeSetTablePanel.getTableModel());

        if(isNodeSet(xObject)) {
          try {
            XMLFragmentsString xmlFragments = new XMLFragmentsString(xObject.nodelist());
            xmlFragmentsPanel.setXmlFragments(xmlFragments);
          } catch(Exception e) {
            xmlFragmentsPanel.setXmlFragments(null);
            throw e;
          }
        } else {
          xmlFragmentsPanel.setXmlFragments(null);
        }

      } catch(IllegalStateException e) {
        XSLTPlugin.processException(e, e.getMessage(), XPathTool.this);
      } catch(SAXException e) { // parse problem
        XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.result.error.bufferUnparseable"), XPathTool.this);
      } catch(IOException e) { // parse problem
        XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.result.error.bufferUnparseable"), XPathTool.this);
      } catch(TransformerException e) { // evaluation problem
        XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.result.error.expressionUnevaluateable"), XPathTool.this);
      } catch(Exception e) { // catch-all
        XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.result.error.unkownProblem"), XPathTool.this);
      }
    }


    /**
     * Creates parser, parses input source and returns resulting document.
     */
    private Document parse(InputSource source) throws ParserConfigurationException, IOException, SAXException {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);

      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(source);
      return document;
    }

  }


  /**
   * Panel housing the "Results" label & text area
   */
  class ResultsPanel extends JPanel {
    protected final JTextArea textArea = new JTextAreaWithoutTab();
    private final JLabel label = new JLabel();


    ResultsPanel(String labelString) {
      super(new BorderLayout());
      setLabelText(labelString);
      this.textArea.setEditable(false);
      int width = (int) textArea.getMinimumSize().getWidth();
      int height = (int) textArea.getPreferredSize().getHeight();
      this.textArea.setMinimumSize(new Dimension(width, height));

      add(label, BorderLayout.NORTH);
      add(new JScrollPane(this.textArea));
    }


    void setLabelText(String text) {
      this.label.setText(text);
    }


    void setText(String text) {
      this.textArea.setText(text);
    }


    void resetCaretPosition() {
      this.textArea.setCaretPosition(0);
    }

  }


  /**
   * Handles the selection of a row on the node set results table,
   * implements interface {@link javax.swing.event.ListSelectionListener}.
   */
  public void valueChanged(ListSelectionEvent event) {
    int selectedRow = this.nodeSetTablePanel.table.getSelectedRow();
    this.xmlFragmentsPanel.highlightFragment(selectedRow);
  }


  /**
   * Panel housing the XML fragments results.
   */
  class XmlFragmentsPanel extends ResultsPanel {

    private XMLFragmentsString xmlFragments;
    private int selected;


    XmlFragmentsPanel(String labelString) {
      super(labelString);
    }


    public void highlightFragment(int index) {
      if(this.xmlFragments != null && index >= 0) {
        int startIndex = this.xmlFragments.getFragmentPosition(index);
        int endIndex = this.xmlFragments.getFragmentPosition(index + 1);

        textArea.getHighlighter().removeAllHighlights();

        try {
          textArea.getHighlighter().addHighlight(startIndex, endIndex, new DefaultHighlighter.DefaultHighlightPainter(new Color(200, 200, 200)));
        } catch(BadLocationException e) {
          throw new IllegalArgumentException(e.toString());
        }

        if(this.selected > index) {
          int temp = startIndex;
          startIndex = endIndex;
          endIndex = temp;
        }

        this.selected = index;

        textArea.setCaretPosition(startIndex);
        textArea.moveCaretPosition(endIndex);
      }
    }


    public void setXmlFragments(XMLFragmentsString xmlFragments) {
      this.xmlFragments = xmlFragments;
      this.selected = -1;

      if(xmlFragments == null) {
        setText("");
      } else {
        setText(xmlFragments.getString());
      }

      resetCaretPosition();
    }
  }


  /**
   * Panel housing the "Results" label & text area
   */
  class NodeSetResultsPanel extends JPanel {
    private final NodeSetTableModel tableModel = new NodeSetTableModel();
    private final JTable table = new JTable();


    NodeSetResultsPanel(String label) {
      super(new BorderLayout());

      table.getSelectionModel().addListSelectionListener(XPathTool.this);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.setModel(tableModel);

      add(new JLabel(label), BorderLayout.NORTH);
      JScrollPane tablePane = new JScrollPane(table);
      add(tablePane);
    }


    NodeSetTableModel getTableModel() {
      return this.tableModel;
    }

  }


}
