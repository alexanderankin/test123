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
import javax.swing.text.Position;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import errorlist.*;
import sidekick.*;
import xml.completion.*;
import xml.*;
//}}}

public class SwingHTMLParserImpl implements XmlSideKickParser
{
	//{{{ getName() method
	public String getName()
	{
		return "html";
	} //}}}

	//{{{ parse() method
	public SideKickParsedData parse(SideKick sidekick, String text)
	{
		if(text.startsWith("<?xml"))
			return XmlPlugin.XML_PARSER_INSTANCE.parse(sidekick,text);

		Buffer buffer = sidekick.getBuffer();

		XmlParsedData data = new XmlParsedData(buffer.getName(),true);

		CompletionInfo info = CompletionInfo.getCompletionInfoForBuffer(buffer);
		if(info != null)
			data.mappings.put("",info);

		try
		{
			DocumentParser htmlParser = new DocumentParser(DTD.getDTD("html32"));

			htmlParser.parse(new StringReader(text),
				new Handler(sidekick,data),
				true);
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR,this,ioe);
			sidekick.addError(ErrorSource.ERROR,buffer.getPath(),0,
				ioe.toString());
		}

		// need to do some cleanup...
		for(int i = 0; i < data.root.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)data.root.getChildAt(i);
			XmlTag tag = (XmlTag)node.getUserObject();
			if(tag.attributes.getValue("_implied_") != null)
			{
				data.root.remove(i);

				int j = 0;

				while(node.getChildCount() != 0)
				{
					data.root.insert((DefaultMutableTreeNode)node.getChildAt(0),i + j);
					j++;
				}

				i--;
			}
		}

		Collections.sort(data.ids,new IDDecl.Compare());

		return data;
	} //}}}

	//{{{ Handler class
	class Handler extends HTMLEditorKit.ParserCallback
	{
		Buffer buffer;

		SideKick sidekick;
		XmlParsedData data;
		Stack currentNodeStack;

		//{{{ Handler constructor
		Handler(SideKick sidekick, XmlParsedData data)
		{
			this.sidekick = sidekick;
			this.data = data;
			this.currentNodeStack = new Stack();
			buffer = sidekick.getBuffer();
		} //}}}

		//{{{ handleStartTag() method
		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int offset)
		{
			try
			{
				buffer.readLock();

				if(offset > buffer.getLength())
					offset = buffer.getLength();

				Position pos = buffer.createPosition(offset);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(t.toString(),
					pos,attributesToSAX(a,t.toString(),pos)));

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
						data.root.insert(newNode,0);
				}

				currentNodeStack.push(newNode);
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ handleEndTag() method
		public void handleEndTag(HTML.Tag t, int offset)
		{
			try
			{
				buffer.readLock();

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.pop();
					XmlTag tag = (XmlTag)node.getUserObject();

					if(offset > buffer.getLength())
						offset = buffer.getLength();

					tag.end = buffer.createPosition(offset);
				}
				else
					/* ? */;
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ handleSimpleTag() method
		public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int offset)
		{
			try
			{
				buffer.readLock();

				if(offset > buffer.getLength())
					offset = buffer.getLength();

				Position pos = buffer.createPosition(offset);

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(t.toString(),
					pos,attributesToSAX(a,t.toString(),pos)));

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.peek();

					node.add(newNode);
				}
				else
					data.root.add(newNode);
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ handleError() method
		public void handleError(String errorMsg, int pos)
		{
			// The Swing parser's error reporting is broken.
			/*try
			{
				buffer.readLock();

				if(pos > buffer.getLength())
					pos = buffer.getLength();
				int line = buffer.getLineOfOffset(pos);

				sidekick.addError(ErrorSource.ERROR,buffer.getPath(),
					line,errorMsg);
			}
			finally
			{
				buffer.readUnlock();
			}*/
		} //}}}

		//{{{ attributesToSAX() method
		private Attributes attributesToSAX(MutableAttributeSet a,
			String element, Position pos)
		{
			ElementDecl elementDecl = data.getElementDecl(element);

			AttributesImpl attrs = new AttributesImpl();
			Enumeration enum = a.getAttributeNames();
			while(enum.hasMoreElements())
			{
				Object attr = enum.nextElement();
				String name = attr.toString();
				String value = a.getAttribute(attr).toString();

				String type = "CDATA";
				if(elementDecl != null)
				{
					ElementDecl.AttributeDecl attrDecl
						= (ElementDecl.AttributeDecl)
						elementDecl.attributeHash
						.get(name.toLowerCase());
					if(attrDecl != null)
					{
						type = attrDecl.type;

						if(type.equals("ID"))
						{
							if(!data.ids.contains(value))
								data.ids.add(new IDDecl(value,element,pos));
						}
					}
				}

				attrs.addAttribute(null,name,name,type,value);
			}

			return attrs;
		} //}}}
	} //}}}
}
