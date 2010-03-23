/*
 * Saxon9XPathAdapter.java - use the XPath 2.0 Saxon engine
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
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collections;


import net.sf.saxon.xpath.XPathFactoryImpl;
import net.sf.saxon.xpath.XPathExpressionImpl;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.om.SequenceIterator; 
import net.sf.saxon.om.NodeInfo; 
/*import net.sf.saxon.om.GroundedIterator;
import net.sf.saxon.om.GroundedValue;
*/
import net.sf.saxon.om.Item;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.dom.DOMNodeList; 
import net.sf.saxon.value.SequenceExtent;
import net.sf.saxon.value.Cardinality; 
import net.sf.saxon.Configuration;
import net.sf.saxon.type.TypeHierarchy;
import net.sf.saxon.type.ItemType;

/**
 * Implementation of XPathAdapter using the Saxon 9 engine
 */
public class Saxon9XPathAdapter implements XPathAdapter {
	final XPathFactoryImpl factory = new XPathFactoryImpl();
	
	public Result evaluateExpression(Document doc, Map<String,String> prefixes, String expression) throws XPathException,XPathExpressionException {
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new NamespaceContextImpl(prefixes));
		
		XPathExpressionImpl expr = (XPathExpressionImpl)xpath.compile(expression);
		
		return new SaxonResult(expr.rawIterator(new DocumentWrapper(doc,doc.getBaseURI(),expr.getConfiguration())),expr.getConfiguration());
	}
	
	
	static class SaxonXPathNode implements XPathNode {
		private Item item;
		private ItemType it;

		SaxonXPathNode(Item i,TypeHierarchy hierarchy){
			this.item = i;
			this.it = Type.getItemType(item,hierarchy);
		}

		public boolean hasExpandedName(){
			int primitiveType=it.getPrimitiveType();
			switch(primitiveType){
			case Type.ATTRIBUTE:
			case Type.ELEMENT:
			case Type.FUNCTION:
			case Type.NAMESPACE:
			case Type.PROCESSING_INSTRUCTION:
				return true;
			default:
				return false;
			}
		}
		
		public boolean hasDomValue(){
			switch (it.getPrimitiveType()) {
				case Type.DOCUMENT:
				case Type.ELEMENT:
					return false;
				default:
					return true;
			}
		}
		
		public String getType(){
			return it.toString();
		}
		
		public String getName(){
			if(item instanceof NodeInfo){
				return ((NodeInfo)item).getDisplayName();
			}else {
				return null;
			}
		}
		
		public String getDomValue() throws XPathException{
			return SequenceExtent.makeSequenceExtent(item.getTypedValue()).getStringValue(); 
		}
		
	}
	
	static class SaxonResult implements Result{
		private SequenceExtent se;
		private Configuration config;
		
		SaxonResult(SequenceIterator si,Configuration c) throws XPathException{
			SequenceExtent se = new SequenceExtent(si);
			ItemType it = se.getItemType(c.getTypeHierarchy());

			this.se = se;
			this.config = c;
		}
		
		public String getType(){
			ItemType it = se.getItemType(config.getTypeHierarchy());
			if(se.getLength()==0) {
				return "empty sequence";
			} else if(se.getLength() == 1) {
				return it.toString();
			} else {
				return "sequence of "+it.toString();
			}
		}
		
		public boolean isNodeSet(){
			return true;
		}
		
		public String getStringValue() throws XPathException{
			return se.getStringValue();
		}
		
		public int size(){
			return se.getLength();
		}
		
		public XPathNode get(int i){
			return new SaxonXPathNode(se.itemAt(i),config.getTypeHierarchy());
		}
		
		public XMLFragmentsString toXMLFragmentsString() throws XPathException {
			XMLFragmentsString res = new XMLFragmentsString(se.getLength());
			for(int i=0;i<se.getLength();i++) {
				Item it = se.itemAt(i);
				if(it instanceof NodeWrapper) {
					res.setNode(i,(Node)((NodeWrapper)it).getRealNode());
				} else {
					res.setText(i,it.getStringValue());
				}
			}
			return res;
		}
		
		public String toString(){
			return "Saxon9Adapter.Result{"+se+"}";
		}
	}
	
	public static class NamespaceContextImpl implements NamespaceContext{
		private Map<String,String> mappings;
		private Map<String,List<String>> reverseMappings;
		
		public NamespaceContextImpl(Map<String,String> mappings) {
			this.mappings = mappings;
		}
		
		public String getNamespaceURI(String prefix){
			if(XMLConstants.XML_NS_PREFIX.equals(prefix)) {
				return XMLConstants.XML_NS_URI;
			} else if(XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
					return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
			} else if(mappings.containsKey(prefix)) {
				return mappings.get(prefix);
			} else if(prefix == null){
				throw new IllegalArgumentException("null prefix");
			} else {
				return XMLConstants.NULL_NS_URI;
			}
		}
		
		public String getPrefix(String namespaceURI){
			Iterator it = getPrefixes(namespaceURI);
			if(it.hasNext()) return (String)it.next();
			else return null;
		}
		
		public Iterator getPrefixes(String namespaceURI){
			if(reverseMappings == null) {
				initReverseMappings();
			}
			
			if(XMLConstants.XML_NS_URI.equals(namespaceURI)) {
				return Collections.singletonList(XMLConstants.XML_NS_PREFIX).iterator();
			} else if(XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
				return Collections.singletonList(XMLConstants.XMLNS_ATTRIBUTE).iterator();
			} else if(reverseMappings.containsKey(namespaceURI)) {
				return Collections.unmodifiableList(reverseMappings.get(namespaceURI)).iterator();
			} else if(namespaceURI == null){
				throw new IllegalArgumentException("null namespaceURI");
			} else {
				return Collections.emptyList().iterator();
			}
		}
		
		private void initReverseMappings() {
			reverseMappings = new HashMap<String,List<String>>();
			for(Map.Entry<String,String> entry: mappings.entrySet()) {
				List<String> l;
				if(reverseMappings.containsKey(entry.getValue())) {
					l = reverseMappings.get(entry.getValue());
				} else {
					l = new LinkedList<String>();
					reverseMappings.put(entry.getValue(),l);
				}
				l.add(entry.getKey());
			}
		}
	}
}
