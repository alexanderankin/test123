/*
 * XmlDaemon.java
 * Copyright (C) 2000 Slava Pestov
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

import com.microstar.xml.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import java.io.StringReader;
import java.util.Stack;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class XmlDaemon extends Thread
{
	public XmlDaemon(Buffer buffer)
	{
		super("XML parser thread");
		this.buffer = buffer;

		root = new DefaultMutableTreeNode(buffer.getName());
		model = new DefaultTreeModel(root);

		try
		{
			text = buffer.getText(0,buffer.getLength());
		}
		catch(BadLocationException ble)
		{
			Log.log(Log.ERROR,this,ble);
			return;
		}
	}

	public void run()
	{
		parse();

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				model.reload(root);
				EditBus.send(new XmlTreeParsed(buffer,model));
			}
		});

		XmlPlugin.daemonFinished();
	}

	// private members
	private Buffer buffer;
	private String text;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private XmlParser parser;

	private void parse()
	{
		parser = new XmlParser();
		parser.setHandler(new Handler());
		try
		{
			parser.parse(null,null,new StringReader(text));
		}
		catch(Exception e)
		{
			XmlPlugin.addError(buffer.getPath(),0,e.toString());
			//Log.log(Log.ERROR,this,e);
		}
	}

	class Handler extends HandlerBase
	{
		Stack currentNodeStack = new Stack();

		public void startElement(String name) throws Exception
		{
			int line = parser.getLineNumber() - 1;
			int column = parser.getColumnNumber() - 1;
			int offset = buffer.getDefaultRootElement()
				.getElement(line).getStartOffset() + column;
			offset = findTagStart(offset);

			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new XmlTag(name,buffer.createPosition(offset)));

			if(!currentNodeStack.isEmpty())
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();

				node.insert(newNode,node.getChildCount());
			}
			else
				root.insert(newNode,0);

			currentNodeStack.push(newNode);
		}

		public void endElement(String name) throws Exception
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				currentNodeStack.peek();
			XmlTag tag = (XmlTag)node.getUserObject();

			int line = parser.getLineNumber() - 1;
			int column = parser.getColumnNumber() - 1;
			int offset = buffer.getDefaultRootElement()
				.getElement(line).getStartOffset() + column;
			offset = findTagEnd(offset);

			tag.end = buffer.createPosition(offset);

			currentNodeStack.pop();
		}

		public void error(String message, String systemId, int line, int column)
		{
			if(systemId == null)
				systemId = buffer.getPath();

			XmlPlugin.addError(systemId,line - 1,message);
		}

		public Object resolveEntity(String publicId, String systemId)
			throws java.lang.Exception
		{
			// hack
			return new StringReader("<!-- yo -->");
		}

		// private members
		private int findTagStart(int offset)
		{
			for(int i = offset; i >= 0; i--)
			{
				if(text.charAt(i) == '<')
					return i;
			}

			return 0;
		}

		private int findTagEnd(int offset)
		{
			for(int i = offset; i < text.length(); i++)
			{
				if(text.charAt(i) == '>')
					return i + 1;
			}

			return 0;
		}
	}
}
