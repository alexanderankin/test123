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

import org.apache.xml.dtm.DTM;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.w3c.dom.*;
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
    selectedResultPanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.xpath.evaluted.label"));
    summaryResultsPanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.summary.label"));
    xmlResultsPanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.xml.string.label"));
    xpathResultsPanel = new ResultsPanel(jEdit.getProperty("XPathTool.result.xpath.string.label"));

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
//    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(selectedResultPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 3;
    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(summaryResultsPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 4;
    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(xpathResultsPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 5;
    gbc.weightx = 1;
    gbc.weighty = 4;
    gbc.fill = GridBagConstraints.BOTH;
    add(xmlResultsPanel, gbc);
  }

  /**
   * There are seven XPath nodes: root node, element,
   * text node, attribute node, processing instruction node,
   * namespace node, and comment node.
   */
  private String getNodeTypeString(short nodeType) {
    switch(nodeType) {
      case DTM.ATTRIBUTE_NODE:
        return "attribute";
//      case DTM.CDATA_SECTION_NODE:
//        return "CDATA section";
      case DTM.COMMENT_NODE:
        return "comment";
//      case DTM.DOCUMENT_FRAGMENT_NODE:
//        return "document fragment";
      case DTM.DOCUMENT_NODE:
        return "root";
//      case DTM.DOCUMENT_TYPE_NODE:
//        return "document type";
      case DTM.ELEMENT_NODE:
        return "element";
//      case DTM.ENTITY_NODE:
//        return "entity";
//      case DTM.ENTITY_REFERENCE_NODE:
//        return "entity reference";
      case DTM.NAMESPACE_NODE:
        return "notation";
//      case DTM.NOTATION_NODE:
//        return "notation";
      case DTM.PROCESSING_INSTRUCTION_NODE:
        return "processing instruction";
      case DTM.TEXT_NODE:
        return "text";
      default:
        throw new IllegalArgumentException();
    }
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
  private String getSummaryString(XObject xObject) throws TransformerException {
    StringBuffer buf = new StringBuffer();

    if(xObject.getType() == XObject.CLASS_NODESET) {
      NodeSetDTM nodeSet = xObject.mutableNodeset();

      for(int i = 0; i < nodeSet.size(); i++) {
        int nodeHandle = nodeSet.item(i);
        DTM dtm = nodeSet.getDTM(nodeHandle);
        short nodeType = dtm.getNodeType(nodeHandle);

        try {
          String nodeTypeString = getNodeTypeString(nodeType);
          String value = dtm.getNodeValue(nodeHandle);
          String nodeName = dtm.getNodeNameX(nodeHandle);

          String messageProperty = "XPathTool.result.node";
          Object[] messageArgs = new Object[]{"" + i, nodeTypeString, nodeName, value};

          if(nodeType == DTM.ELEMENT_NODE) {
            messageProperty = "XPathTool.result.element";
            messageArgs = new Object[]{"" + i, nodeTypeString, nodeName};
          } else if(nodeType == DTM.TEXT_NODE || nodeType == DTM.COMMENT_NODE) {
            messageProperty = "XPathTool.result.no-name-node";
            messageArgs = new Object[]{"" + i, nodeTypeString, value};
          } else if(nodeType == DTM.DOCUMENT_NODE) {
            messageProperty = "XPathTool.result.root";
            messageArgs = new Object[]{"" + i, nodeTypeString};
          }

          buf.append(MessageFormat.format(jEdit.getProperty(messageProperty), messageArgs));
          buf.append("\n");
        } catch(IllegalArgumentException e) {
          ;// do nothing
        }
      }
    } else {
      buf.append(xObject.toString());
    }

    return buf.toString();
  }


  /**
   * @param xObject XObject to be converted
   * @return xml representing nodes in the supplied XObject, if it had nodes
   */
  private String getXmlString(XObject xObject) throws TransformerException {
    StringBuffer buf = new StringBuffer("");

    if(xObject.getType() == XObject.CLASS_NODESET) {
      NodeList nodelist = xObject.nodelist();

      for(int i = 0; i < nodelist.getLength(); i++) {
        Node node = nodelist.item(i);
        appendNode(node, buf, 0, false);
      }
    }

    return buf.toString();
  }


  private void appendNode(Node node, StringBuffer buf, int indentLevel, boolean insideElement) {
    short type = node.getNodeType();

    if(type == Node.ELEMENT_NODE) {
      appendElementNode(node, buf, indentLevel);
    } else if(type == Node.TEXT_NODE) {
      appendTextNode(node, buf, insideElement);
    } else if(type == Node.ATTRIBUTE_NODE) {
      appendAttributeNode(node, buf);
    } else if(type == Node.DOCUMENT_NODE) {
      appendChildNodes(node.getChildNodes(), 0, buf, -1);
    } else if(type == Node.COMMENT_NODE) {
      appendCommentNode(node, buf, indentLevel);
    } else if(type == Node.PROCESSING_INSTRUCTION_NODE) {
      appendProcessingInstructionNode(node, buf);
    } else {
      appendNode(node.getNextSibling(), buf, indentLevel, true);
    }
  }


  private void appendProcessingInstructionNode(Node node, StringBuffer buf) {
    buf.append("<?");
    buf.append(node.getNodeName());
    buf.append(" ");
    buf.append(node.getNodeValue());
    buf.append("?>\n");
  }

  private void appendCommentNode(Node node, StringBuffer buf, int indentLevel) {
    appendIndent(buf, indentLevel);
    buf.append("<!--");
    buf.append(node.getNodeValue());
    buf.append("-->\n");
  }


  private void appendTextNode(Node node, StringBuffer buf, boolean insideElement) {
    String value = node.getNodeValue();
    String trimmedValue = value.trim();
    if(trimmedValue.length() > 0 || !insideElement) {
      buf.append(value);
    }
    if(!insideElement) {
      buf.append('\n');
    }
  }


  private static final int NO_INDENT = Integer.MIN_VALUE;

  private void appendElementNode(Node node, StringBuffer buf, int indentLevel) {
    if(indentLevel != NO_INDENT) {
      appendIndent(buf, indentLevel);
    }
    buf.append('<');
    buf.append(node.getNodeName());

    NamedNodeMap attributes = node.getAttributes();

    if(attributes.getLength() > 0) {
      for(int i = 0; i < attributes.getLength(); i++) {
        Attr attribute = (Attr)attributes.item(i);
        buf.append(' ');
        appendAttribute(attribute, buf);
      }
    }

    NodeList nodes = node.getChildNodes();

    if(nodes.getLength() > 0) {
      buf.append('>');

      Node firstNode = nodes.item(0);

      if(firstNode.getNodeType() == Node.TEXT_NODE && firstNode.getNodeValue().trim().length() > 0) {
        appendTextNode(nodes.item(0), buf, true);
        appendChildNodes(nodes, 1, buf, NO_INDENT);
      } else {
        buf.append(System.getProperty("line.separator"));
        appendChildNodes(nodes, 0, buf, indentLevel);
        appendIndent(buf, indentLevel);
      }

      buf.append("</");
      buf.append(node.getNodeName());
      buf.append('>');

      if(indentLevel != NO_INDENT) {
        buf.append(System.getProperty("line.separator"));
      }
    } else {
      buf.append("/>");
      if(indentLevel != NO_INDENT) {
        buf.append(System.getProperty("line.separator"));
      }
    }
  }


  private void appendIndent(StringBuffer buf, int indentLevel) {
    for(int i = 0; i < indentLevel; i++) {
      buf.append("  ");
    }
  }


  private void appendAttribute(Attr attribute, StringBuffer buf) {
    buf.append(attribute.getName());
    buf.append("=\"");
    buf.append(attribute.getValue());
    buf.append('\"');
  }

  private void appendAttributeNode(Node node, StringBuffer buf) {
    buf.append(node.getNodeName());
    buf.append("=\"");
    buf.append(node.getNodeValue());
    buf.append('\"');
    buf.append(System.getProperty("line.separator"));
  }

  private void appendChildNodes(NodeList nodes, int startIndex, StringBuffer buf, int indentLevel) {
    if(indentLevel != NO_INDENT) {
      indentLevel++;
    }

    for(int i = startIndex; i < nodes.getLength(); i++) {
      appendNode(nodes.item(i), buf, indentLevel, true);
    }
  }


  /**
   * @return "Evaluate" buttons
   */
  public JButton getEvaluateButton() {
    return evaluatePanel.button;
  }


  /**
   * Panel housing the "XPath Expression" label & text area
   */
  class ExpressionPanel extends JPanel {
    ExpressionPanel() {
      super(new BorderLayout());
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.add(new JLabel(jEdit.getProperty("XPathTool.xpathExpression.label")), BorderLayout.WEST);
      add(topPanel, BorderLayout.NORTH);
      textArea = new JTextArea();
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

    JTextArea textArea;
  }


  /**
   * Panel housing the "Evaluate" button
   */
  class EvaluatePanel extends JPanel {
    EvaluatePanel() {
      button = new JButton(jEdit.getProperty("XPathTool.evaluate.button"));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          try {
            Buffer buffer = view.getBuffer();
            String text = buffer.getText(0, buffer.getLength());
            InputSource inputSource = new InputSource(new StringReader(text));
            inputSource.setSystemId(buffer.getFile().getPath());
            Document document = parse(inputSource);
            XObject xObject = XPathAPI.eval(document, expressionPanel.textArea.getText());

            selectedResultPanel.textArea.setText(getDataTypeMessage(xObject));
            summaryResultsPanel.textArea.setText(getSummaryString(xObject));
            xpathResultsPanel.textArea.setText(xObject.xstr().toString());
            xmlResultsPanel.textArea.setText(getXmlString(xObject));

            summaryResultsPanel.textArea.setCaretPosition(0);
            xpathResultsPanel.textArea.setCaretPosition(0);
            xmlResultsPanel.textArea.setCaretPosition(0);
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
      });
      add(button);
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

    private JButton button;
  }


  /**
   * Panel housing the "Results" label & text area
   */
  class ResultsPanel extends JPanel {
    ResultsPanel(String label) {
      super(new BorderLayout());
      add(new JLabel(label), BorderLayout.NORTH);
      textArea = new JTextArea();
      textArea.setEditable(false);
      add(new JScrollPane(textArea));
    }

    JTextArea textArea;
  }

  private View view;
  private ExpressionPanel expressionPanel;
  private EvaluatePanel evaluatePanel;
  private ResultsPanel selectedResultPanel;
  private ResultsPanel summaryResultsPanel;
  private ResultsPanel xmlResultsPanel;
  private ResultsPanel xpathResultsPanel;

}
