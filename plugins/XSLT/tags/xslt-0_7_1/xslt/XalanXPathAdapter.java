/*
 * XalanXPathAdapter.java - implements XPathAdapter using Xalan
 *
 * Copyright (c) 2010 Eric Le Lay
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

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collections;

import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.PrefixResolver;

import org.gjt.sp.jedit.jEdit;

/**
 * XPathAdapter implementation using Xalan.
 * Historically, it was included in XPathTool and the XPathAdapter
 * has been abstracted from its usage. 
 */
public class XalanXPathAdapter implements XPathAdapter {
	
	public Result evaluateExpression(Document doc, Map<String,String> prefixes, String expression)
	throws TransformerException {

		PrefixResolverImpl res = new PrefixResolverImpl(prefixes);
		XObject xObject = XPathAPI.eval(doc, expression, res);

		return new XalanResult(doc, expression, xObject);
	}
	
	/**
	 * Returns a message containing the data type selected by the XPath expression.
	 * Note: there are four data types in the XPath 1.0 data model: node-set, string,
	 * number, and boolean.
	 *
	 * @param xObject XObject to be converted
	 * @return string describing the selected data type
	 */
	private static String getDataTypeMessage(XObject xObject) throws TransformerException {
		String typeMessage = null;

		if (xObject.getType() == XObject.CLASS_NODESET) {
			NodeSetDTM nodeSet = xObject.mutableNodeset();

			Object[] messageArgs = new Object[]{new Integer(nodeSet.size())};
			typeMessage = jEdit.getProperty("xpath.result.data-type.node-set", messageArgs);
		} else {
			Object[] messageArgs = new Object[]{xObject.getTypeString().substring(1).toLowerCase()};
			typeMessage = jEdit.getProperty("xpath.result.data-type.not-node-set", messageArgs);
		}

		return typeMessage;
	}
	
	class XalanResult implements Result{
		private XObject xObject;
		private String expression;
		private Document document;
		
		XalanResult(Document document, String expression, XObject xObject){
			this.xObject = xObject;
			this.document = document;
			this.expression = expression;
		}
		
		public String getType() throws TransformerException{
			return getDataTypeMessage(xObject);
		}
		
		public boolean isNodeSet() {
			return xObject.getType() == XObject.CLASS_NODESET;
		}
		
		public String getStringValue(){
			if(isNodeSet()) {
				try {
					return XPathAPI.eval(document, "string(" + expression + ")").toString();
				} catch (TransformerException e) {
					return "";
				}
			} else {
				return xObject.xstr().toString();
			}
		}
		
		public int size() throws TransformerException {
			if (isNodeSet()) {
				return xObject.mutableNodeset().size();
			}else {
				return 0;
			}
		}
		
		public XPathNode get(int i) throws TransformerException {
			NodeSetDTM nodeSet = xObject.mutableNodeset();
			return XalanXPathNode.getXPathNode(nodeSet,i);
		}
		
		public XMLFragmentsString toXMLFragmentsString() throws TransformerException {
			// If I remove this line the tests fail in XSLTPluginXPathTest.
			// The xObject must be "materialized" before calling nodelist()
			xObject.toString();
			return new XMLFragmentsString(xObject.nodelist());
		}
	}
	
	public static class PrefixResolverImpl implements PrefixResolver{
		private Map<String,String> map;
		
		public PrefixResolverImpl(Map<String,String> map) {
			this.map = map;
		}
		
		public String getBaseIdentifier(){
			return null;
		}
		
		public String getNamespaceForPrefix(String prefix, Node context) {
			return map.get(prefix);
		}
		
		public String getNamespaceForPrefix(java.lang.String prefix) {
			return map.get(prefix);
		}
		
		public boolean handlesNullPrefixes(){
			return false;
		}
	}

}
