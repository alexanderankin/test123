/*
 * SwingHTMLParserImpl.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001 Slava Pestov
 * Portions copyright (C) 2001 David Walend
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.parser;

//{{{ Imports
import javax.swing.text.html.parser.*;
import javax.swing.text.html.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.tree.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Stack;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import errorlist.*;
import xml.completion.*;
import xml.*;
//}}}

public class SwingHTMLParserImpl implements XmlParser.Impl
{
	//{{{ parse() method
	public void parse(XmlParser parser, Reader in) throws IOException
	{
		this.parser = parser;
		buffer = parser.getBuffer();
		root = new DefaultMutableTreeNode(buffer.getName());

		// XXX
		htmlParser = new DocumentParser(DTD.getDTD("html32"));
		htmlParser.parse(in,new Handler(),true);
	} //}}}

	//{{{ getElementTree() method
	public TreeNode getElementTree()
	{
		return root;
	} //}}}

	//{{{ getCompletionInfo() method
	public CompletionInfo getCompletionInfo()
	{
		return null;
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private XmlParser parser;
	private DocumentParser htmlParser;
	private Buffer buffer;
	private DefaultMutableTreeNode root;
	//}}}

	//{{{ attributesToSAX() method
	private Attributes attributesToSAX(MutableAttributeSet a)
	{
		AttributesImpl attrs = new AttributesImpl();
		Enumeration enum = a.getAttributeNames();
		while(enum.hasMoreElements())
		{
			Object attr = enum.nextElement();
			String name = attr.toString();
			String value = a.getAttribute(attr).toString();

			// TODO: is CDATA really appropriate here?
			// does anyone even check the type value?
			attrs.addAttribute(null,null,name,"CDATA",value);
		}

		return attrs;
	} //}}}

	//{{{ Handler class
	class Handler extends HTMLEditorKit.ParserCallback
	{
		Stack currentNodeStack = new Stack();

		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
		{
			try
			{
				buffer.readLock();

				if(pos > buffer.getLength())
					pos = buffer.getLength();

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(t.toString(),
					buffer.createPosition(pos),
					attributesToSAX(a)));

				//if(!Boolean.TRUE.equals(a.getAttribute(IMPLIED)))
				{
					if(!currentNodeStack.isEmpty())
					{
						DefaultMutableTreeNode node
							= (DefaultMutableTreeNode)
							currentNodeStack.peek();

						node.insert(newNode,node.getChildCount());
					}
					else
						root.insert(newNode,0);
				}

				currentNodeStack.push(newNode);
			}
			finally
			{
				buffer.readUnlock();
			}
		}

		public void handleEndTag(HTML.Tag t, int pos)
		{
			try
			{
				buffer.readLock();

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.pop();
					XmlTag tag = (XmlTag)node.getUserObject();

					if(pos > buffer.getLength())
						pos = buffer.getLength();

					tag.end = buffer.createPosition(pos);
				}
				else
					/* ? */;
			}
			finally
			{
				buffer.readUnlock();
			}
		}

		public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos)
		{
			try
			{
				buffer.readLock();

				if(pos > buffer.getLength())
					pos = buffer.getLength();

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(t.toString(),
					buffer.createPosition(pos),
					attributesToSAX(a)));

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.peek();

					node.insert(newNode,node.getChildCount());
				}
				else
					root.insert(newNode,0);
			}
			finally
			{
				buffer.readUnlock();
			}
		}

		public void handleError(String errorMsg, int pos)
		{
			// The Swing parser's error reporting is broken.
			/*try
			{
				buffer.readLock();

				if(pos > buffer.getLength())
					pos = buffer.getLength();
				int line = buffer.getLineOfOffset(pos);

				parser.addError(ErrorSource.ERROR,buffer.getPath(),
					line,errorMsg);
			}
			finally
			{
				buffer.readUnlock();
			}*/
		}
	} //}}}
}
