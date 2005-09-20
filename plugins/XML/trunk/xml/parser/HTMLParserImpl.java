/*
 * HTMLParserImpl.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Will Sargent
 * Portions copyright (C) 2000, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

//{{{ Import statements
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.util.Log;

import org.htmlparser.NodeReader;
import org.htmlparser.Parser;

import org.htmlparser.tags.EndTag;
import org.htmlparser.tags.Tag;

import org.htmlparser.util.ParserException;
import org.htmlparser.util.ParserFeedback;

import org.htmlparser.visitors.NodeVisitor;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

import xml.XmlParsedData;

import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.IDDecl;

import java.io.Reader;
import java.io.StringReader;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
//}}}

/**
 * An HTML parser which uses the parser engine from  <a
 * href="http://htmlparser.sourceforge.net">http://htmlparser.sourceforge.net</a>
 * to value the structure browser with the appropriate tree nodes.
 * 
 * <p>
 * This parser has a list of empty tags which indicate to the parser when a tag
 * cannot contain any content, even though it may be lacking an end tag.
 * </p>
 *
 * @author wsargent
 * @version $Revision$
 *
 * @since Jun 17, 2003
 */
public class HTMLParserImpl extends XmlParser
{
	protected static String[] sEmptyTags = 
	{
		"HR", "META", "BR", "INPUT",
		

		// JHTML specific tags.
		"IMPORTBEAN", "PARAM", "SETVALUE", "DECLAREPARAM",
	};

	/**
	 * DOCUMENT ME!
	 */
	public HTMLParserImpl()
	{
		super("html");
	}

	//{{{ parse() method
	public SideKickParsedData parse(Buffer buffer,
		DefaultErrorSource errorSource)
	{
		if ((buffer.getLength() >= 5) && buffer.getText(0, 5).equals("<?xml"))
		{
			SideKickParser xml = (SideKickParser) ServiceManager.getService(SideKickParser.SERVICE,
					"xml");

			return xml.parse(buffer, errorSource);
		}

		// Get the first line of text.
		String text;

		try
		{
			buffer.readLock();
			text = buffer.getText(0, buffer.getLength());
		}
		finally
		{
			buffer.readUnlock();
		}

		XmlParsedData data = new XmlParsedData(buffer.getName(), true);

		CompletionInfo info = CompletionInfo.getCompletionInfoForBuffer(buffer);

		if (info != null)
		{
			data.mappings.put("", info);
		}

		// Buffer size has to be more than 0, even though we're buffering from a 
		// String.  Yeah, it's dumb.
		int bufferSize = 8192;

		StringReader reader = new StringReader(text);
		ExtendedNodeReader nodeReader = new ExtendedNodeReader(reader,
				bufferSize);

		try
		{
			Parser parser = new Parser(nodeReader, new LogParserFeedback());
			Handler handler = new Handler(buffer, data, nodeReader);
			parser.visitAllNodesWith(handler);
		}
		catch (ParserException e)
		{
			Log.log(Log.ERROR, this, e);
			errorSource.addError(ErrorSource.ERROR, buffer.getPath(), 0, 0, 0,
				e.toString());
		}

		Collections.sort(data.ids, new IDDecl.Compare());

		return data;
	}

	//}}}
	//{{{ Handler class
	class Handler extends NodeVisitor
	{
		Buffer buffer;
		ExtendedNodeReader reader;
		Stack currentNodeStack;
		XmlParsedData data;

		//{{{ Handler constructor
		Handler(Buffer buffer, XmlParsedData data, ExtendedNodeReader reader)
		{
			this.buffer = buffer;
			this.data = data;
			this.reader = reader;
			this.currentNodeStack = new Stack();
		}

		//}}}
		//{{{ attributesToSAX() method
		private Attributes attributesToSAX(Hashtable a, String element,
			int line, int column)
		{
			ElementDecl elementDecl = data.getElementDecl(element);

			AttributesImpl attrs = new AttributesImpl();
			Enumeration itr = a.keys();

			while (itr.hasMoreElements())
			{
				Object attr = itr.nextElement();
				String name = attr.toString().toLowerCase();

				if (name.startsWith("$"))
				{
					// Skip this key/value pair -- the parser likes to put
					// $<TAGNAME>$ as a key for some reason.
					continue;
				}

				String value = a.get(attr).toString();

				String type = "CDATA";

				if (elementDecl != null)
				{
					ElementDecl.AttributeDecl attrDecl = (ElementDecl.AttributeDecl) elementDecl.attributeHash.get(name.toLowerCase());

					if (attrDecl != null)
					{
						type = attrDecl.type;

						if (type.equals("ID"))
						{
							if (! data.ids.contains(value))
							{
								data.ids.add(new IDDecl(buffer.getPath(),
										value, element, line, column));
							}
						}
					}
				}

				attrs.addAttribute(null, name, name, type, value);
			}

			return attrs;
		}

		//}}}

		/* (non-Javadoc)
		 * @see org.htmlparser.visitors.NodeVisitor#visitTag(org.htmlparser.tags.Tag)
		 */
		public void visitTag(Tag tag)
		{
			boolean isEmptyTag = tag.isEmptyXmlTag();
			String tagName = tag.getTagName().toLowerCase();

			// hack to get around the most commonly abused empty end tags.
			if (isEmptyTag(tagName))
			{
				isEmptyTag = true;
			}

			String line = tag.getTagLine();
			int lineOffset = line.length() - tag.elementBegin();

			//Log.log(Log.DEBUG, this, line);
			//String msg = "lineOffset = " + lineOffset + ", line " + line.length();
			//Log.log(Log.DEBUG, this, msg);
			int offset = reader.getOffset() - lineOffset;

			try
			{
				buffer.readLock();

				if (offset > buffer.getLength())
				{
					offset = buffer.getLength();
				}

				Hashtable attrsHash = tag.getAttributes();

				Position pos = buffer.createPosition(offset);
				int lineNumber = tag.getTagStartLine();
				int colNumber = tag.getTagBegin();

				Attributes attrs = attributesToSAX(attrsHash, tagName,
						lineNumber, colNumber);
				XmlTag xmlTag = new XmlTag(tagName, pos, attrs);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(xmlTag);

				if (! isEmptyTag)
				{
					if (! currentNodeStack.isEmpty())
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentNodeStack.peek();

						node.insert(newNode, node.getChildCount());
					}
					else
					{
						data.root.insert(newNode, 0);
					}

					currentNodeStack.push(newNode);
				}
				else
				{
					if (! currentNodeStack.isEmpty())
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentNodeStack.peek();

						node.add(newNode);
					}
					else
					{
						data.root.add(newNode);
					}
				}
			}
			finally
			{
				buffer.readUnlock();
			}
		}

		/**
		 * See if the tag is one which cannot have any elements.
		 *
		 * @param tagName
		 *
		 * @return
		 */
		private boolean isEmptyTag(String tagName)
		{
			if (tagName == null)
			{
				return true;
			}

			for (int i = 0; i < sEmptyTags.length; i++)
			{
				String emptyTag = sEmptyTags[i];

				if (tagName.equalsIgnoreCase(emptyTag))
				{
					return true;
				}
			}

			return false;
		}

		/* (non-Javadoc)
		 * @see org.htmlparser.visitors.NodeVisitor#visitEndTag(org.htmlparser.tags.EndTag)
		 */
		public void visitEndTag(EndTag endTag)
		{
			//Log.log(Log.DEBUG, this, "endTag = " + endTag.getTagName());
			int offset = reader.getOffset();

			try
			{
				buffer.readLock();

				if (! currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentNodeStack.pop();
					XmlTag tag = (XmlTag) node.getUserObject();

					if (offset > buffer.getLength())
					{
						offset = buffer.getLength();
					}

					tag.end = buffer.createPosition(offset);
				}
				else
				{
					Log.log(Log.ERROR, this,
						"visitEndTag: current node stack is empty.");
				}
			}
			finally
			{
				buffer.readUnlock();
			}
		}
	}

	//}}}
	class LogParserFeedback implements ParserFeedback
	{
		/**
		 * Logs an info message to the jEdit activity log.
		 *
		 * @param message the message to log.
		 */
		public void info(String message)
		{
			Log.log(Log.NOTICE, this, message);
		}

		/**
		 * Logs a warning message to the jEdit activity log.
		 *
		 * @param message the message to log.
		 */
		public void warning(String message)
		{
			Log.log(Log.WARNING, this, message);
		}

		/**
		 * Logs an error message to the jEdit activity log.
		 *
		 * @param message the message to log.
		 * @param e the exception (if any) to log.
		 */
		public void error(String message, ParserException e)
		{
			Log.log(Log.ERROR, this, message);
		}
	}

	class ExtendedNodeReader extends NodeReader
	{
		int mOffset;

		public ExtendedNodeReader(Reader pReader, int bufferSize)
		{
			super(pReader, bufferSize);
		}

		/**
		 * Returns the offset returned as the sum of the bytes in each line
		 * read.  Note that this does not give the exact offset to a specific
		 * tag.
		 *
		 * @return the byte offset of the previous lines read.
		 */
		public int getOffset()
		{
			return mOffset;
		}

		public String getNextLine()
		{
			String nextLine = super.getNextLine();

			if (nextLine != null)
			{
				// XXX how do we deal with the line terminator?
				// Adding one byte for the terminator seems to work for
				// both \r\n and \n.  I haven't looked at why it works.
				mOffset += nextLine.length();
				mOffset += 1;
			}

			return nextLine;
		}
	}
}
