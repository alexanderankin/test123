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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public class XPathTool extends JPanel {

  public XPathTool(View view) {
    super(new GridBagLayout());
    this.view = view;

    expressionPanel = new ExpressionPanel();
    evaluatePanel = new EvaluatePanel();
    dataTypePanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.xpath.evaluted.label"));
    nodeSetTablePanel = new NodeSetResultsPanel(jEdit.getProperty("XPathTool.result.summary.label"));
    xmlFragmentsPanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.xml.string.label"));
    resultValuePanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.value.label"));

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
   * Returns a summary of the evaluation of an XPath expression.
   * Note: there are four data types in the XPath 1.0 data model: node-set, string,
   * number, and boolean.
   *
   * @param xObject XObject to be converted
   * @return user-friendly string describing the supplied XObject
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


  /**
   * Panel housing the "XPath Expression" label & text area
   */
  class ExpressionPanel extends JPanel {
    JTextArea textArea = new JTextArea();

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
        getEvaluateButton().setFocusPainted(false);

        Buffer buffer = view.getBuffer();
        String text = buffer.getText(0, buffer.getLength());
        InputSource inputSource = new InputSource(new StringReader(text));
        inputSource.setSystemId(buffer.getFile().getPath());
        Document document = parse(inputSource);
        String expression = expressionPanel.textArea.getText();
        XObject xObject = XPathAPI.eval(document, expression);

        dataTypePanel.textArea.setText(getDataTypeMessage(xObject));
        resultValuePanel.textArea.setText(xObject.xstr().toString());
        resultValuePanel.textArea.setCaretPosition(0);

        if(isNodeSet(xObject)) {
          resultValuePanel.label.setText(jEdit.getProperty("XPathTool.result.string-value.label"));
        } else {
          resultValuePanel.label.setText(jEdit.getProperty("XPathTool.result.value.label"));
        }

        setNodeSetResults(xObject, nodeSetTablePanel.tableModel);

        if(isNodeSet(xObject)) {
          XMLFragmentsString xmlString = new XMLFragmentsString(xObject.nodelist());
          xmlFragmentsPanel.textArea.setText(xmlString.getString());
          xmlFragmentsPanel.textArea.setCaretPosition(0);
        } else {
          xmlFragmentsPanel.textArea.setText("");
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
    ResultsPanel(String labelString) {
      super(new BorderLayout());

      label = new JLabel(labelString);
      textArea = new JTextArea();
      textArea.setEditable(false);

      add(label, BorderLayout.NORTH);
      add(new JScrollPane(textArea));
    }

    JTextArea textArea;
    JLabel label;
  }


  /**
   * Panel housing the "Results" label & text area
   */
  class NodeSetResultsPanel extends JPanel {
    NodeSetResultsPanel(String label) {
      super(new BorderLayout());

      tableModel = new NodeSetTableModel();
      JTable table = new JTable(tableModel);

      add(new JLabel(label), BorderLayout.NORTH);
      JScrollPane tablePane = new JScrollPane(table);
      add(tablePane);
    }


    NodeSetTableModel tableModel;
  }

  private final ExpressionPanel expressionPanel;
  private final EvaluatePanel evaluatePanel;
  private final ResultsPanel dataTypePanel;
  private final NodeSetResultsPanel nodeSetTablePanel;
  private final ResultsPanel xmlFragmentsPanel;
  private final ResultsPanel resultValuePanel;
  private View view;

}
