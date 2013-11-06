/*
 * DOMXPathAdapter.java - common code for DOM based XPathAdapters
 *
 * Copyright (c) 2013 Eric Le Lay
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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import xml.CharSequenceReader;
import xml.PathUtilities;

/**
 * Common code for XPathAdapters based on DOM input.
 * There is only xalan for now, but it's mainly to extract
 * standard DOM operations from Xalan specific class.
 */
public abstract class DOMXPathAdapter implements XPathAdapter {

	@Override
	public Document buildDocument(Buffer source)
		throws IOException,ParserConfigurationException,SAXException
	{
		CharSequence chars = source.getSegment(0,source.getLength());
		CharSequenceReader reader = new CharSequenceReader(chars);
		InputSource ss = new InputSource(reader);
		ss.setSystemId(PathUtilities.pathToURL(source.getPath()));
		return XPathTool.parse(ss);
	}
	
	@Override
	public Document buildDocument(URI source)
		throws IOException,ParserConfigurationException,SAXException
	{
		String sourceURL = source.toString();
		InputSource inputSource = xml.Resolver.instance().resolveEntity("",sourceURL);
		return XPathTool.parse(inputSource);
	}

	@Override
	public Map<String,List<String>> grabNamespaces(Document document)
	{
		Map<String,List<String>> bindings = new HashMap<String,List<String>>();
		
		if (document.getImplementation().hasFeature("traversal", "2.0")) {
			DocumentTraversal traversable = (DocumentTraversal) document;
			NodeIterator iterator = traversable.createNodeIterator(
				document, NodeFilter.SHOW_ELEMENT,null, true);
			
			Node node;
			while ((node = iterator.nextNode()) != null) {
				NamedNodeMap attrs = node.getAttributes();
				for(int i=0;i<attrs.getLength();i++) {
					Node a  = attrs.item(i);
					
					if("xmlns".equals(a.getPrefix())
						|| (a.getPrefix() == null && "xmlns".equals(a.getLocalName()))) {
						String prefix = a.getPrefix() == null ? "" : a.getLocalName();
						String ns = a.getNodeValue();
						List l;
						if(bindings.containsKey(prefix)){
							l = bindings.get(prefix);
						} else {
							l = new ArrayList<String>();
							bindings.put(prefix,l);
						}
						if(!l.contains(ns)) {
							l.add(ns);
						}
					}
				}
			}
		} else {
			Log.log(Log.ERROR,this,"DomImplementation doesn't support DOM Traversal");
		}
		return Collections.emptyMap();
	}
}
