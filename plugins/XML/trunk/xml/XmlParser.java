/*
 * XmlParser.java
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

package xml;

//{{{ Imports
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import errorlist.*;
//}}}

class XmlParser
{
	//{{{ XmlParser constructor
	XmlParser(View view)
	{
		this.view = view;

		elements = new Vector();
		elementHash = new Hashtable();
		entities = new Vector();
		entityHash = new Hashtable();
		ids = new Vector();

		errorSource = new DefaultErrorSource("XML");
		ErrorSource.registerErrorSource(errorSource);

		handler = new Handler();
	} //}}}

	//{{{ parse() method
	void parse(Buffer buffer)
	{
		// prepare for parsing
		elements.removeAllElements();
		elementHash.clear();
		entities.removeAllElements();
		entityHash.clear();

		addEntity(new EntityDecl(EntityDecl.INTERNAL,"lt","<"));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"gt",">"));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"amp","&"));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"quot","\""));
		addEntity(new EntityDecl(EntityDecl.INTERNAL,"apos","'"));

		ids.removeAllElements();
		handler.currentNodeStack.removeAllElements();

		root = new DefaultMutableTreeNode(buffer.getName());
		model = new DefaultTreeModel(root);

		errorSource.clear();

		parser = new org.apache.xerces.parsers.SAXParser();
		try
		{
			parser.setFeature("http://apache.org/xml/features/validation/dynamic",
				jEdit.getBooleanProperty("xml.validate"));
			parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
			parser.setErrorHandler(handler);
			parser.setEntityResolver(handler);
			parser.setContentHandler(handler);
			parser.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se);
		}

		// get buffer text
		this.buffer = buffer;
		text = buffer.getText(0,buffer.getLength());

		// start parser thread
		stopThread();

		thread = new ParseThread();
		thread.start();
	} //}}}

	//{{{ stopThread() method
	void stopThread()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
	} //}}}

	//{{{ addError() method
	void addError(int type, String path, int line, String message)
	{
		// FIXME?
		if(path.startsWith("file://"))
			path = path.substring(7);
		path.replace('/',File.separatorChar);

		errorSource.addError(type,path,line,0,0,message);
	} //}}}

	//{{{ dispose() method
	void dispose()
	{
		stopThread();

		errorSource.clear();
		ErrorSource.unregisterErrorSource(errorSource);
	} //}}}

	//{{{ getErrorSource() method
	ErrorSource getErrorSource()
	{
		return errorSource;
	} //}}}

	//{{{ ElementDeclCompare class
	static class ElementDeclCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return MiscUtilities.compareStrings(
				((ElementDecl)obj1).name,
				((ElementDecl)obj2).name,true);
		}
	} //}}}

	//{{{ EntityDeclCompare class
	static class EntityDeclCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			EntityDecl entity1 = (EntityDecl)obj1;
			EntityDecl entity2 = (EntityDecl)obj2;

			if(entity1.type != entity2.type)
				return entity2.type - entity1.type;
			else
			{
				return MiscUtilities.compareStrings(
					entity1.name,
					entity2.name,true);
			}
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private Buffer buffer;

	private Vector elements;
	private Hashtable elementHash;
	private Vector entities;
	private Hashtable entityHash;
	private Vector ids;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	private ParseThread thread;

	private String text;

	private XMLReader parser;
	private Handler handler;

	private DefaultErrorSource errorSource;
	//}}}

	//{{{ doParse() method
	private void doParse()
	{
		try
		{
			InputSource source = new InputSource(new StringReader(text));
			source.setSystemId(buffer.getPath());

			parser.parse(source);
		}
		catch(SAXParseException spe)
		{
			// fatal error, already handled
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR,this,se.getException());
			if(se.getMessage() != null)
			{
				addError(ErrorSource.ERROR,buffer.getPath(),
				0,se.getMessage());
			}
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR,this,ioe);
			addError(ErrorSource.ERROR,buffer.getPath(),0,
			ioe.toString());
		}
	} //}}}

	//{{{ finish() method
	private void finish()
	{
		if(view.isClosed())
			return;

		thread = null;

		// to avoid keeping pointers to stale objects.
		// we could reuse a single parser instance to preserve
		// performance, but it eats too much memory, especially
		// if the file being parsed has an associated DTD.
		buffer = null;
		text = null;
		parser = null;

		MiscUtilities.quicksort(elements,new ElementDeclCompare());
		MiscUtilities.quicksort(entities,new EntityDeclCompare());
		MiscUtilities.quicksort(ids,new MiscUtilities.StringICaseCompare());

		CompletionInfo completionInfo = new CompletionInfo(false,
			elements,elementHash,entities,entityHash,ids);

		view.getEditPane().putClientProperty(
			XmlPlugin.COMPLETION_INFO_PROPERTY,
			completionInfo);

		XmlTree tree = (XmlTree)view.getDockableWindowManager()
			.getDockable(XmlPlugin.TREE_NAME);
		if(tree != null)
		{
			model.reload(root);
			tree.parsingComplete(model);
		}

		XmlInsert insert = (XmlInsert)view.getDockableWindowManager()
			.getDockable(XmlPlugin.INSERT_NAME);
		if(insert != null)
			insert.update();
	} //}}}

	//{{{ addEntity() method
	private void addEntity(EntityDecl entity)
	{
		entities.addElement(entity);
		if(entity.type == EntityDecl.INTERNAL
			&& entity.value.length() == 1)
		{
			Character ch = new Character(
				entity.value.charAt(0));
			entityHash.put(entity.name,ch);
			entityHash.put(ch,entity.name);
		}
	} //}}}

	//}}}

	//{{{ Handler class
	class Handler extends DefaultHandler implements DeclHandler
	{
		Stack currentNodeStack = new Stack();
		Locator loc = null;

		//{{{ setDocumentLocator() method
		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		} //}}}

		//{{{ resolveEntity() method
		public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException
		{
			InputSource source = null;

			try
			{
				source = CatalogManager.resolve(
					loc.getSystemId(),publicId,systemId);
			}
			catch(IOException io)
			{
				error(new SAXParseException(io.toString(),loc));
			}

			if(source == null)
				return new InputSource(new StringReader("<!-- -->"));
			else
				return source;
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			// add all attributes with type "ID" to the ids vector
			for(int i = 0; i < attrs.getLength(); i++)
			{
				if(attrs.getType(i).equals("ID"))
					ids.addElement(attrs.getValue(i));
			}

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			try
			{
				buffer.readLock();

				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength() - 1,
					buffer.getLineStartOffset(line)
					+ column - 1);

				offset = findTagStart(offset);

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					new XmlTag(qName,buffer.createPosition(offset),attrs));

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
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ endElement() method
		public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
			) throws SAXException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			// ignore tags inside nested files for now
			//if(!buffer.getPath().equals(loc.getSystemId()))
			//	return;

			try
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();
				XmlTag tag = (XmlTag)node.getUserObject();

				buffer.readLock();

				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(buffer.getLength() - 1,
					buffer.getLineStartOffset(line)
					+ column);

				tag.end = buffer.createPosition(offset);

				currentNodeStack.pop();
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}

		//{{{ error() method
		public void error(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ warning() method
		public void warning(SAXParseException spe)
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			addError(ErrorSource.WARNING,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ fatalError() method
		public void fatalError(SAXParseException spe)
			throws SAXParseException
		{
			if(Thread.currentThread().isInterrupted())
			{
				return;
			}

			addError(ErrorSource.ERROR,spe.getSystemId(),
				Math.max(0,spe.getLineNumber()-1),
				spe.getMessage());
		} //}}}

		//{{{ elementDecl() method
		public void elementDecl(String name, String model)
		{
			boolean empty = "EMPTY".equals(model);
			ElementDecl elementDecl = new ElementDecl(name,empty,false);
			elementHash.put(name,elementDecl);
			elements.addElement(elementDecl);
		} //}}}

		//{{{ attributeDecl() method
		public void attributeDecl(String eName, String aName,
			String type, String valueDefault, String value)
		{
			ElementDecl element = (ElementDecl)elementHash.get(eName);
			if(element == null)
				return;

			Vector values;

			if(type.startsWith("("))
			{
				values = new Vector();

				StringTokenizer st = new StringTokenizer(
					type.substring(1,type.length() - 1),"|");
				while(st.hasMoreTokens())
				{
					values.addElement(st.nextToken());
				}
			}
			else
				values = null;

			boolean required = "#REQUIRED".equals(valueDefault);

			element.addAttribute(new ElementDecl.AttributeDecl(
				aName,value,values,type,required));
		} //}}}

		//{{{ internalEntityDecl() method
		public void internalEntityDecl(String name, String value)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			addEntity(new EntityDecl(
				EntityDecl.INTERNAL,name,value));
		} //}}}

		//{{{ externalEntityDecl() method
		public void externalEntityDecl(String name, String publicId,
			String systemId)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			addEntity(new EntityDecl(
				EntityDecl.EXTERNAL,name,publicId,systemId));
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
	} //}}}

	//{{{ ParseThread class
	class ParseThread extends Thread
	{
		ParseThread()
		{
			super("XML parser thread");
			setPriority(Thread.MIN_PRIORITY);
		}

		public void run()
		{
			doParse();

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					finish();
				}
			});
		}
	} //}}}
}
