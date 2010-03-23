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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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


  private final StringBuffer buffer = new StringBuffer();

  /** Holds an array of the start position of each XML fragment in the fragments String */
  private int[] fragmentPositions;


  public XMLFragmentsString(int nodeCount) {
  	  this.fragmentPositions = new int[nodeCount];
  }
  
  /**
   * Constructs string of XML fragments representing the nodes in the node list.
   * @param nodelist containing nodes to be represented as XML fragments.
   * @throws IllegalStateException if the generated string becomes too large.
   */
  public XMLFragmentsString(NodeList nodelist) throws IllegalArgumentException {
    int nodeCount = nodelist.getLength();
    this.fragmentPositions = new int[nodeCount];

    for(int i = 0; i < nodeCount; i++) {
      Node node = nodelist.item(i);
      this.fragmentPositions[i] = buffer.length();

      appendNode(node, 0, false);

      if(buffer.length() > MAX_CHARS_IN_FRAGMENTS_STRING.intValue()) {
        String errorMessage = jEdit.getProperty("xpath.result.message.large-xml-fragment");
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


  /**
   * Returns the position in the string of fragment at the given index, or
   * if the given index is higher than the largest fragment index, returns the length of the string.
   *
   * @param index index of the fragment
   * @return fragment position in string, or length of string if index is too high
   */
  public int getFragmentPosition(int index) {
    if(index >= this.fragmentPositions.length) {
      return buffer.length();
    } else {
      return this.fragmentPositions[index];
    }
  }


  public int getFragmentCount() {
    return this.fragmentPositions.length;
  }

  public void setNode(int index, Node node){
  	  if(index >= fragmentPositions.length) {
  	  	  throw new IllegalArgumentException("index too big ("+index+">="+fragmentPositions.length);
  	  } else if(fragmentPositions[index] != 0) {
  	  	  throw new IllegalStateException("fragment "+index+" already initialized");
  	  } else {
  	  	  fragmentPositions[index] = buffer.length();
  	  	  appendNode(node, 0, false);
      }
  }
  
  public void setText(int index, String value){
  	  if(index >= fragmentPositions.length) {
  	  	  throw new IllegalArgumentException("index too big ("+index+">="+fragmentPositions.length);
  	  } else if(fragmentPositions[index] != 0) {
  	  	  throw new IllegalStateException("fragment "+index+" already initialized");
  	  } else {
  	  	  fragmentPositions[index] = buffer.length();
  	  	  String trimmedValue = value.trim();
		  buffer.append(value);
		  buffer.append(NL);
      }
  }

  private void appendNode(Node node, int indentLevel, boolean insideElement) {
    if(node != null) {
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
    appendAttributes(node.getAttributes());
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


  private void appendAttribute(Node node) {
    buffer.append(node.getNodeName());
    buffer.append("=\"");
    buffer.append(node.getNodeValue());
    buffer.append('\"');
  }


  private void appendAttributes(NamedNodeMap attributes) {
    for(int i = 0; i < attributes.getLength(); i++) {
      buffer.append(' ');
      appendAttribute(attributes.item(i));
    }
  }


  private void appendAttributeNode(Node node) {
    appendAttribute(node);
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
