/*
 * Saxon9XPathAdapter.java - use the XPath 2.0 Saxon engine
 *
 * Copyright (c) 2010,2013 Eric Le Lay
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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.gjt.sp.jedit.Buffer;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collections;

import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.ItemTypeFactory;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.xpath.XPathFactoryImpl;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.om.NodeInfo; 
import net.sf.saxon.Configuration;

import org.gjt.sp.util.Log;

import xml.CharSequenceReader;
import xml.PathUtilities;

/**
 * Implementation of XPathAdapter using the Saxon 9 engine
 */
public class Saxon9XPathAdapter implements XPathAdapter {
	final XPathFactoryImpl factory;
	final Configuration config;
	final Processor processor;

	public Saxon9XPathAdapter() {
		config = new Configuration();
		config.setLineNumbering(true);
		factory = new XPathFactoryImpl(config);
		processor = new Processor(config);
	}

	@Override
	public Document buildDocument(Buffer source) throws IOException,SAXException,SaxonApiException {
		CharSequence chars = source.getSegment(0,source.getLength());
		CharSequenceReader reader = new CharSequenceReader(chars);
		StreamSource ss = new StreamSource(reader, PathUtilities.pathToURL(source.getPath()));
		return buildWrappedDocument(ss);
	}

	@Override
	public Document buildDocument(URI source) throws IOException,SAXException,SaxonApiException {
		//TODO: would it make sense to set the resolver to xml.Resolver ?
		StreamSource ss = new StreamSource(source.toString());
		return buildWrappedDocument(ss);
	}

	private Document buildWrappedDocument(Source source) throws SaxonApiException,IOException,SAXException {
		try {
			DocumentBuilder builder = processor.newDocumentBuilder();
			XdmNode doc = builder.build(source);
			return (Document)NodeOverNodeInfo.wrap(doc.getUnderlyingNode());
		} catch(SaxonApiException e) {
			if(e.getCause() instanceof IOException) {
				throw (IOException)e.getCause();
			} else if(e.getCause() instanceof SAXException) {
				throw (SAXException)e.getCause();
			} else {
				throw e;
			}
		}
	}


	public Map<String,List<String>> grabNamespaces(Document doc) throws IllegalArgumentException {
		if(!(doc instanceof NodeOverNodeInfo))throw new IllegalArgumentException("Document givent to Saxon9XPathAdapter.grabNamespaces not of the right class");
		XdmNode node = new XdmNode(((NodeOverNodeInfo)doc).getUnderlyingNodeInfo());

		XPathCompiler comp = processor.newXPathCompiler();

		Map<String,List<String>> bindings = new HashMap<String,List<String>>();

		try{
			XdmValue res = comp.evaluate(
				  "for $n in //*/namespace::* return ($n/name(), string($n))"
				, node);

			for(int i=0;i<res.size()-1;){
				String prefix = res.itemAt(i++).getStringValue();
				String ns = res.itemAt(i++).getStringValue();
				if(XMLConstants.XML_NS_URI.equals(ns))continue;
				List<String> bound = bindings.get(prefix);
				if(bound == null){
					bound = new ArrayList<String>();
					bindings.put(prefix, bound);
				}
				if(!bound.contains(ns)){
					bound.add(ns);
				}
			}
		}catch(SaxonApiException e){
			Log.log(Log.ERROR,this,"known node query failed",e);
		}
		return bindings;
	}

	@Override
	public Result evaluateExpression(Document doc, Map<String,String> prefixes, String expression) 
		throws SaxonApiException,XPathException
	{
		if(!(doc instanceof NodeOverNodeInfo))throw new IllegalArgumentException("Document givent to Saxon9XPathAdapter.evaluateExpression not of the right class");
		XdmNode node = new XdmNode(((NodeOverNodeInfo)doc).getUnderlyingNodeInfo());
		XPathCompiler comp = processor.newXPathCompiler();
		for(Map.Entry<String,String> en: prefixes.entrySet()){
			comp.declareNamespace(en.getKey(),en.getValue());
		}

		XdmValue res = comp.evaluate(expression, node);

		return new SaxonResult(res, processor);
	}


	static class SaxonXPathNode implements XPathNode {
		private XdmItem item;
		private ItemType it;

		SaxonXPathNode(XdmItem i,ItemType it){
			this.item = i;
			this.it = it;
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
			// too much details if not calling getPrimitiveItemType (like "element(Q{urn:joe}hello)")
			return it.getPrimitiveItemType().toString();
		}

		public String getName(){
			if(item.getUnderlyingValue() instanceof NodeInfo){
				return ((NodeInfo)item.getUnderlyingValue()).getDisplayName();
			}else {
				return null;
			}
		}

		public String getDomValue() throws XPathException{
			return item.getStringValue();
		}

		/**
		 * @return the line number of the node
		 */
		public int getLineNumber(){
			if(item.getUnderlyingValue() instanceof NodeInfo){
				return ((NodeInfo)item.getUnderlyingValue()).getLineNumber();
			}else {
				throw new UnsupportedOperationException("I told you I didn't have location information !");
			}
		}

		/**
		 * @return the column number of the node
		 */
		public int getColumnNumber(){
			if(item.getUnderlyingValue() instanceof NodeInfo){
				return ((NodeInfo)item.getUnderlyingValue()).getColumnNumber();
			}else {
				throw new UnsupportedOperationException("I told you I didn't have location information !");
			}
		}

		/**
		 * @return true for nodes, false overwise
		 */
		public boolean hasLocation() {
			return (item.getUnderlyingValue() instanceof NodeInfo);
		}

	}

	static class SaxonResult implements Result{
		private XdmValue value;
		private ItemTypeFactory typeFactory;

		SaxonResult(XdmValue value, Processor processor) throws XPathException{
			this.value = value;
			this.typeFactory = new ItemTypeFactory(processor);
		}

		public String getType(){
			if(value.size()==0) {
				return "empty sequence";
			} else {
				XdmItem itm = value.itemAt(0);
				// too much details if not calling getPrimitiveItemType (like "element(Q{urn:joe}hello)")
				ItemType type = typeFactory.getItemType(itm).getUnderlyingItemType().getPrimitiveItemType();
				if(value.size() == 1){
					return type.toString();
				}else{
					return "sequence of "+type.toString();
				}
			}
		}

		public boolean isNodeSet(){
			return true;
		}

		public String getStringValue() throws XPathException {
			if(value.size()==0) {
				return "()";
			} else if(value.size() == 1) {
					return value.itemAt(0).getStringValue();
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append('(');
				for(int i=0; i< value.size(); i++){
					XdmItem itm = value.itemAt(i);
					if(i>0){
						sb.append(',');
					}
					sb.append(itm.getStringValue());
				}
				sb.append(')');
				return sb.toString();
			}
		}

		public int size(){
			return value.size();
		}

		public XPathNode get(int i){
			XdmItem itm = value.itemAt(i);
			return new SaxonXPathNode(itm, typeFactory.getItemType(itm).getUnderlyingItemType());
		}

		public XMLFragmentsString toXMLFragmentsString() throws XPathException {
			XMLFragmentsString res = new XMLFragmentsString(value.size());
			for(int i=0;i<value.size();i++) {
				XdmItem it = value.itemAt(i);
				if(it instanceof XdmNode) {
					res.setNode(i,NodeOverNodeInfo.wrap(((XdmNode)it).getUnderlyingNode()));
				} else {
					res.setText(i,it.getStringValue());
				}
			}
			return res;
		}

		public String toString(){
			return "Saxon9Adapter.Result{"+value+"}";
		}
	}

}
