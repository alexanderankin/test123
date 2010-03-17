/*
 * XPathNode.java - Represents an XPath 1.0 node
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

import org.apache.xml.dtm.DTM;
import org.apache.xpath.NodeSetDTM;

/**
 * Represents a <a href="http://www.w3.org/TR/xpath#data-model">XPath 1.0 node</a>.
 *
 * @author Robert McKinnon
 */
public class XalanXPathNode implements XPathAdapter.XPathNode {

	private final int nodeHandle;
	private final DTM dtm;
	private final short nodeType;


	/**
	 * Returns a XPathNode representing the node at the given index in the
	 * supplied node set, or null if the node at the given index is does not
	 * have a node type from the
	 * <a href="http://www.w3.org/TR/xpath#data-model">XPath 1.0 data model</a>.
	 *
	 * @return an XPathNode or null.
	 */
	public static XalanXPathNode getXPathNode(NodeSetDTM nodeSet, int index) {
		int nodeHandle = nodeSet.item(index);
		DTM dtm = nodeSet.getDTM(nodeHandle);
		short nodeType = dtm.getNodeType(nodeHandle);

		if (isXPathNodeType(nodeType)) {
			return new XalanXPathNode(nodeHandle, dtm, nodeType);
		} else {
			return null;
		}
	}

	/**
	 * Returns true if the node type from the
	 * <a href="http://www.w3.org/TR/xpath#data-model">XPath 1.0 data model</a>.
	 */
	private static boolean isXPathNodeType(short nodeType) {
		switch (nodeType) {
			case DTM.ATTRIBUTE_NODE:
			case DTM.ELEMENT_NODE:
			case DTM.NAMESPACE_NODE:
			case DTM.PROCESSING_INSTRUCTION_NODE:
			case DTM.COMMENT_NODE:
			case DTM.DOCUMENT_NODE:
			case DTM.TEXT_NODE:
				return true;
			default:
				return false;
		}
	}


	/**
	 * Constructs node with the given node type identifier.
	 *
	 * @param nodeType from {@link org.apache.xml.dtm.DTM} interface
	 * @throws IllegalArgumentException if nodeType is not valid.
	 */
	private XalanXPathNode(int nodeHandle, DTM dtm, short nodeType) {
		this.nodeHandle = nodeHandle;
		this.dtm = dtm;
		this.nodeType = nodeType;
	}

	public boolean isElement() {
		return nodeType == DTM.ELEMENT_NODE;
	}

	public boolean isText() {
		return nodeType == DTM.TEXT_NODE;
	}

	/**
	 * Returns the <a href="http://www.w3.org/TR/xpath#data-model">node type</a> name.
	 *
	 * @return node type name, one of:
	 *         root, element, text, attribute,
	 *         processing instruction,
	 *         namespace, or comment.
	 */
	public String getType() {
		switch (this.nodeType) {
			case DTM.ATTRIBUTE_NODE:
				return "attribute";
			case DTM.COMMENT_NODE:
				return "comment";
			case DTM.ELEMENT_NODE:
				return "element";
			case DTM.NAMESPACE_NODE:
				return "namespace";
			case DTM.PROCESSING_INSTRUCTION_NODE:
				return "processing instruction";
			case DTM.TEXT_NODE:
				return "text";
			default:
				return "root";
		}
	}


	/**
	 * Returns true if the node has an
	 * <a href="http://www.w3.org/TR/xpath#dt-expanded-name">expanded-name</a>.
	 */
	public boolean hasExpandedName() {
		switch (this.nodeType) {
			case DTM.COMMENT_NODE:
			case DTM.DOCUMENT_NODE:
			case DTM.TEXT_NODE:
				return false;
			default:
				return true;
		}
	}


	/**
	 * There are seven XPath nodes: root, element,
	 * text, attribute, processing instruction,
	 * namespace, and comment.
	 */
	public boolean hasDomValue() {
		switch (this.nodeType) {
			case DTM.DOCUMENT_NODE:
			case DTM.ELEMENT_NODE:
				return false;
			default:
				return true;
		}
	}


	public String getName() {
		return this.dtm.getNodeNameX(this.nodeHandle);
	}


	public String getDomValue() {
		String domValue = this.dtm.getNodeValue(this.nodeHandle);

		if (hasDomValue()) {
			domValue = XSLTUtilities.removeIn(domValue, (char) 10); //remove '\r' to temporarily fix a bug in the display of results in Windows
		}

		return domValue;
	}

}
