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

import org.apache.xerces.parsers.DOMParser;
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
    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(summaryResultsPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 3;
    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    add(xpathResultsPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 4;
    gbc.weightx = 1;
    gbc.weighty = 4;
    gbc.fill = GridBagConstraints.BOTH;
    add(xmlResultsPanel, gbc);
  }


  private String getNodeTypeString(short nodeType) {
    switch(nodeType) {
      case Node.ATTRIBUTE_NODE:
        return "attribute";
      case Node.CDATA_SECTION_NODE:
        return "CDATA section";
      case Node.COMMENT_NODE:
        return "comment";
      case Node.DOCUMENT_FRAGMENT_NODE:
        return "document fragment";
      case Node.DOCUMENT_NODE:
        return "document";
      case Node.DOCUMENT_TYPE_NODE:
        return "document type";
      case Node.ELEMENT_NODE:
        return "element";
      case Node.ENTITY_NODE:
        return "entity";
      case Node.ENTITY_REFERENCE_NODE:
        return "entity reference";
      case Node.NOTATION_NODE:
        return "notation";
      case Node.PROCESSING_INSTRUCTION_NODE:
        return "processing instruction";
      case Node.TEXT_NODE:
        return "text";
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * @param xObject XObject to be converted
   * @return user-friendly string describing the supplied XObject
   */
  private String getSummaryString(XObject xObject) throws TransformerException {
    StringBuffer buf = new StringBuffer();

    if(xObject.getType() == XObject.CLASS_NODESET) {
      NodeList nodelist = xObject.nodelist();
      Object[] messageArgs = new Object[]{new Integer(nodelist.getLength())};
      buf.append(MessageFormat.format(jEdit.getProperty("XPathTool.result.nodes.summary"), messageArgs));
      buf.append("\n");

      for(int i = 0; i < nodelist.getLength(); i++) {
        Node node = nodelist.item(i);
        String nodeType = getNodeTypeString(node.getNodeType());
        String value = node.getNodeValue();

        if(value != null && node.getNodeType() == Node.TEXT_NODE) {
          value = value.trim();
        }

        messageArgs = new Object[]{"" + i, nodeType, node.getNodeName(), value};
        buf.append(MessageFormat.format(jEdit.getProperty("XPathTool.result.node"), messageArgs));
        buf.append("\n");
      }

    } else {
      Object[] messageArgs = new Object[]{xObject.getTypeString().substring(1).toLowerCase()};
      buf.append(MessageFormat.format(jEdit.getProperty("XPathTool.result.non-nodes.summary"), messageArgs));
      buf.append("\n");
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
    if(node.getNodeType() == Node.ELEMENT_NODE) {
      appendElementNode(node, buf, indentLevel);
    } else if(node.getNodeType() == Node.TEXT_NODE) {
      appendTextNode(node, buf, insideElement);
    } else if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
      appendAttributeNode(node, buf);
    } else if(node.getNodeType() == Node.DOCUMENT_NODE) {
      appendChildNodes(node.getChildNodes(), 0, buf, -1);
    } else if(node.getNodeType() == Node.COMMENT_NODE) {
      appendCommentNode(node, buf, indentLevel);
    } else {
      appendNode(node.getNextSibling(), buf, indentLevel, true);
    }
  }


  private void appendCommentNode(Node node, StringBuffer buf, int indentLevel) {
    appendIndent(buf, indentLevel);
    buf.append("<!--");
    buf.append(node.getNodeValue());
    buf.append("-->\n");
  }


  private void appendTextNode(Node node, StringBuffer buf, boolean insideElement) {
    String value = node.getNodeValue().trim();
    if(value.length() > 0 || !insideElement) {
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
            DOMParser parser = new DOMParser();
            parser.setFeature("http://xml.org/sax/features/validation", false);
            Buffer buffer = view.getBuffer();
            String text = buffer.getText(0, buffer.getLength());
            InputSource inputSource = new InputSource(new StringReader(text));
            inputSource.setSystemId(buffer.getFile().getPath());
            parser.parse(inputSource);
            XObject xObject = XPathAPI.eval(parser.getDocument(), expressionPanel.textArea.getText());

            summaryResultsPanel.textArea.setText(getSummaryString(xObject));
            xpathResultsPanel.textArea.setText(xObject.str());
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
  private ResultsPanel summaryResultsPanel;
  private ResultsPanel xmlResultsPanel;
  private ResultsPanel xpathResultsPanel;

}
