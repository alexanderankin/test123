/*
 * XMLFragmentsString.java - Represents a sequence of XML fragments as a string
 *
 * Copyright (c) 2002 Robert McKinnon
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

import org.gjt.sp.jedit.jEdit;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.text.MessageFormat;

/**
 * Represents a sequence of XML fragments as a string.
 *
 * @author Robert McKinnon
 */
public class XMLFragmentsString {

  private static final int NO_INDENT = Integer.MIN_VALUE;
  private static final String NL = System.getProperty("line.separator");

  /** Maximum number of characters in the XML fragments result string */
  private static final Integer MAX_CHARS_IN_FRAGMENTS_STRING = new Integer(1000000);


  private final StringBuffer buffer = new StringBuffer("");


  /**
   * Constructs string of XML fragments representing the nodes in the node list.
   * @param NodeList containing nodes to be represented as XML fragments.
   * @throws IllegalStateException if the generated string becomes too large.
   */
  public XMLFragmentsString(NodeList nodelist) {
    for(int i = 0; i < nodelist.getLength(); i++) {
      Node node = nodelist.item(i);
      appendNode(node, 0, false);

      if(buffer.length() > MAX_CHARS_IN_FRAGMENTS_STRING.intValue()) {
        String errorMessage = jEdit.getProperty("XPathTool.result.error.largeXmlFragment");
        String msg = MessageFormat.format(errorMessage, new Object[]{MAX_CHARS_IN_FRAGMENTS_STRING});
        throw new IllegalStateException(msg);
      }
    }
  }


  /**
   * Returns string of XML fragments representing the sequence of nodes in a node set.
   */
  public String getString() {
    return buffer.toString();
  }


  private void appendNode(Node node, int indentLevel, boolean insideElement) {
    short type = node.getNodeType();

    if(type == Node.ELEMENT_NODE) {
      appendElementNode(node, indentLevel);
    } else if(type == Node.TEXT_NODE) {
      appendTextNode(node, insideElement);
    } else if(type == Node.ATTRIBUTE_NODE) {
      appendAttributeNode(node);
    } else if(type == Node.DOCUMENT_NODE) {
      appendChildNodes(node.getChildNodes(), 0, -1);
    } else if(type == Node.COMMENT_NODE) {
      appendCommentNode(node, indentLevel);
    } else if(type == Node.PROCESSING_INSTRUCTION_NODE) {
      appendProcessingInstructionNode(node);
    } else {
      appendNode(node.getNextSibling(), indentLevel, true);
    }
  }


  private void appendProcessingInstructionNode(Node node) {
    buffer.append("<?");
    buffer.append(node.getNodeName());
    buffer.append(" ");
    buffer.append(node.getNodeValue());
    buffer.append("?>");
    buffer.append(NL);
  }


  private void appendCommentNode(Node node, int indentLevel) {
    appendIndent(indentLevel);
    buffer.append("<!--");
    buffer.append(node.getNodeValue());
    buffer.append("-->");
    buffer.append(NL);
  }


  private void appendTextNode(Node node, boolean insideElement) {
    String value = node.getNodeValue();
    String trimmedValue = value.trim();
    if(trimmedValue.length() > 0 || !insideElement) {
      buffer.append(value);
    }
    if(!insideElement) {
      buffer.append(NL);
    }
  }


  private void appendElementNode(Node node, int indentLevel) {
    if(indentLevel != NO_INDENT) {
      appendIndent(indentLevel);
    }
    buffer.append('<');
    buffer.append(node.getNodeName());

    NamedNodeMap attributes = node.getAttributes();

    if(attributes.getLength() > 0) {
      for(int i = 0; i < attributes.getLength(); i++) {
        Attr attribute = (Attr)attributes.item(i);
        buffer.append(' ');
        appendAttribute(attribute);
      }
    }

    NodeList nodes = node.getChildNodes();

    if(nodes.getLength() > 0) {
      buffer.append('>');

      Node firstNode = nodes.item(0);

      if(firstNode.getNodeType() == Node.TEXT_NODE && firstNode.getNodeValue().trim().length() > 0) {
        appendTextNode(nodes.item(0), true);
        appendChildNodes(nodes, 1, NO_INDENT);
      } else {
        buffer.append(NL);
        appendChildNodes(nodes, 0, indentLevel);
        appendIndent(indentLevel);
      }

      buffer.append("</");
      buffer.append(node.getNodeName());
      buffer.append('>');

      if(indentLevel != NO_INDENT) {
        buffer.append(NL);
      }
    } else {
      buffer.append("/>");
      if(indentLevel != NO_INDENT) {
        buffer.append(NL);
      }
    }
  }


  private void appendIndent(int indentLevel) {
    for(int i = 0; i < indentLevel; i++) {
      buffer.append("  ");
    }
  }


  private void appendAttribute(Attr attribute) {
    buffer.append(attribute.getName());
    buffer.append("=\"");
    buffer.append(attribute.getValue());
    buffer.append('\"');
  }


  private void appendAttributeNode(Node node) {
    buffer.append(node.getNodeName());
    buffer.append("=\"");
    buffer.append(node.getNodeValue());
    buffer.append('\"');
    buffer.append(NL);
  }


  private void appendChildNodes(NodeList nodes, int startIndex, int indentLevel) {
    if(indentLevel != NO_INDENT) {
      indentLevel++;
    }

    for(int i = startIndex; i < nodes.getLength(); i++) {
      appendNode(nodes.item(i), indentLevel, true);
    }
  }

}
