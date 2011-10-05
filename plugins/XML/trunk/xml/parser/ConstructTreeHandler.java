/*
 * ConstructTreeHandler.java - construcs the SideKick tree from SAX parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import java.util.HashMap;
import java.util.Stack;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import xml.XmlParsedData;

//{{{ Handler class
class ConstructTreeHandler extends DefaultHandler2 implements ContentHandler
{
    /**
	 * 
	 */
	private final XercesParserImpl xercesParserImpl;

	// {{{ members
	Buffer buffer;

	ErrorListErrorHandler errorHandler;
	CharSequence text;
	XmlParsedData data;
	
	HashMap<String, String> declaredPrefixes;
	Stack<DefaultMutableTreeNode> currentNodeStack;
	Locator loc;
	boolean empty;

	/** used to install the locator in the resolver */
	private MyEntityResolver resolver;
	
	// }}}
	// {{{ Handler constructor
	ConstructTreeHandler(XercesParserImpl xercesParserImpl, Buffer buffer, CharSequence text, ErrorListErrorHandler errorHandler,
		XmlParsedData data, MyEntityResolver resolver)
	{
		this.xercesParserImpl = xercesParserImpl;
		this.buffer = buffer;
		this.text = text;
		this.errorHandler = errorHandler;
		this.data = data;
		this.currentNodeStack = new Stack<DefaultMutableTreeNode>();
		this.empty = true;
		this.resolver = resolver;
	} // }}}

	//{{{ setDocumentLocator() method
	public void setDocumentLocator(Locator locator)
	{
		loc = locator;
		resolver.setDocumentLocator(locator);
	} //}}}

	//{{{ startPrefixMapping() method
	public void startPrefixMapping(String prefix, String uri)
	{
		if(declaredPrefixes == null)declaredPrefixes = new HashMap<String,String>();
		declaredPrefixes.put(uri,prefix);

		
	} //}}}


	//{{{ startElement() method
	public void startElement(String namespaceURI,
		String lName, // local name
		String qName, // qualified name
		Attributes attrs) throws SAXException
	{
		if(this.xercesParserImpl.stopped)
			throw new XercesParserImpl.StoppedException();

		empty = true;

		String currentURI = xml.PathUtilities.urlToPath(loc.getSystemId());

		// what do we do in this case?
		if(loc.getLineNumber() == -1){
			Log.log(Log.WARNING,XercesParserImpl.class,"no location for "+qName);
			return;
		}
		
		if(!buffer.getPath().equals(currentURI))
			return;

		buffer.readLock();

		try
		{
			int line = Math.min(buffer.getLineCount() - 1,
				loc.getLineNumber() - 1);
			int column = loc.getColumnNumber() - 1;
			int offset = Math.min(text.length() - 1,
				buffer.getLineStartOffset(line)
				+ column - 1);

			offset = findTagStart(offset);
			Position pos = buffer.createPosition(offset);

			XmlTag newTag = createTag(qName, namespaceURI==null ? "" : namespaceURI, pos, attrs);
			newTag.namespaceBindings = declaredPrefixes;
			declaredPrefixes = null;
			
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTag);

			if(!currentNodeStack.isEmpty())
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();

				node.insert(newNode,node.getChildCount());
			}
			else
				data.root.insert(newNode,0);

			currentNodeStack.push(newNode);
		}
		finally
		{
			buffer.readUnlock();
		}
	} //}}}
	
	private XmlTag createTag(String qname, String namespaceURI, Position pos, Attributes attrs) {
	    String tagClassName = jEdit.getProperty("xml.xmltag." + buffer.getMode().toString());
	    if (tagClassName != null) {
	        try {
	            Class tagClass = Class.forName(tagClassName);
	            java.lang.reflect.Constructor con = tagClass.getConstructor(String.class, String.class, Position.class, Attributes.class);
	            return (XmlTag)con.newInstance(qname, namespaceURI, pos, attrs);
	        }
	        catch (Exception e) {
	             // ignored, just return an XmlTag if this fails   
	             e.printStackTrace();
	        }
	    }
	    return new XmlTag(qname, namespaceURI, pos, attrs);   
	}

	//{{{ endElement() method
	public void endElement(String namespaceURI,
		String sName, // simple name
		String qName  // qualified name
		) throws SAXException
	{
		if(this.xercesParserImpl.stopped)
			throw new XercesParserImpl.StoppedException();

		if(!buffer.getPath().equals(xml.PathUtilities.urlToPath(loc.getSystemId())))
			return;
              
		// what do we do in this case?
		if(loc.getLineNumber() == -1)
			return;

		buffer.readLock();

		try
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				currentNodeStack.peek();
			XmlTag tag = (XmlTag)node.getUserObject();
			if(tag.getName().equals(qName))
			{
				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength(),
					buffer.getLineStartOffset(line)
					+ column);

				tag.setEnd(buffer.createPosition(offset));
				tag.empty = empty;
				currentNodeStack.pop();
			}
		}
		finally
		{
			buffer.readUnlock();
		}

		empty = false;
	} //}}}

	//{{{ characters() method
	public void characters (char ch[], int start, int length)
		throws SAXException
	{
		if(this.xercesParserImpl.stopped)
			throw new XercesParserImpl.StoppedException();

		empty = false;
		// currentNodeStack is empty for compound documents in the "root" document
		// where text appears in nodes that are not kept in the Sidekick tree
		// see test_data/compound_documents
		if(!currentNodeStack.isEmpty()){
			DefaultMutableTreeNode node = currentNodeStack.peek();
			XmlTag tag = (XmlTag)node.getUserObject();
			if (tag.canAddCharacters()) {
				 char[] chBis = new char[length];
				 System.arraycopy(ch,start,chBis,0,length);
				 tag.addCharacters(chBis);   
			}
		}
	} //}}}

	
	//{{{ findTagStart() method
	private int findTagStart(int offset)
	{
		for(int i = offset; i >= 0; i--)
		{
			if(text.charAt(i) == '<')
				return i;
		}

		return 0;
	} //}}}

}// }}}