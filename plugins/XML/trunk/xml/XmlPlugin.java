/*
 * XmlPlugin.java
 * Copyright (C) 2000, 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import javax.swing.text.BadLocationException;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class XmlPlugin extends EBPlugin
{
	public static final String TREE_NAME = "xml-tree";
	public static final String INSERT_NAME = "xml-insert";

	// We store the list of declared elements in this edit pane client
	// property
	public static final String ELEMENTS_PROPERTY = "xml.declared-elements";

	// We store the list of declared elements in this edit pane client
	// property
	public static final String ELEMENT_HASH_PROPERTY = "xml.declared-element-hash";

	// We store the list of declared entities in this edit pane client
	// property
	public static final String ENTITIES_PROPERTY = "xml.declared-entities";

	// We store the list of id attribute values here
	public static final String IDS_PROPERTY = "xml.declared-ids";

	public void start()
	{
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,TREE_NAME);
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,INSERT_NAME);
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("xml-menu"));
	}

	public void createOptionPanes(OptionsDialog dialog)
	{
		//OptionGroup grp = new OptionGroup("xml");
		//grp.addOptionPane(new GeneralOptionPane());
		//dialog.addOptionGroup(grp);

		dialog.addOptionPane(new GeneralOptionPane());
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow)msg;
			if(cmsg.getDockableWindowName().equals(TREE_NAME))
			{
				cmsg.setDockableWindow(new XmlTree(cmsg.getView(),
					!cmsg.getPosition().equals(
					DockableWindowManager.FLOATING)));
			}
			else if(cmsg.getDockableWindowName().equals(INSERT_NAME))
			{
				cmsg.setDockableWindow(new XmlInsert(
					cmsg.getView()));
			}
		}
		else if(msg instanceof PropertiesChanged)
			EntityManager.propertiesChanged();
	}

	public static void showEditTagDialog(View view)
	{
		showEditTagDialog(view,view.getEditPane(),false);
	}

	public static void showEditTagDialog(View view, EditPane editPane,
		boolean removeIfCancel)
	{
		Hashtable elements = (Hashtable)editPane.getClientProperty(
			ELEMENT_HASH_PROPERTY);

		if(elements == null)
		{
			GUIUtilities.error(view,"xml-edit-tag.no-tree",null);
			return;
		}

		Vector ids = (Vector)editPane.getClientProperty(IDS_PROPERTY);

		// XXX: to do
		boolean html = false;

		JEditTextArea textArea = editPane.getTextArea();

		String text = textArea.getText();

		int caret = textArea.getCaretPosition();

		int start = -1;
		int end = -1;

		// scan backwards looking for <
		// if we find a >, then assume we're not inside a tag
		for(int i = caret - 1; i >= 0; i--)
		{
			char ch = text.charAt(i);
			if(ch == '>')
				break;
			else if(ch == '<')
			{
				start = i;
				break;
			}
		}

		// scan forwards looking for >
		// if we find a <, then assume we're not inside a tag
		for(int i = caret; i < text.length(); i++)
		{
			char ch = text.charAt(i);
			if(ch == '<')
				break;
			else if(ch == '>')
			{
				end = i;
				break;
			}
		}

		if(start == -1 || end == -1)
		{
			view.getToolkit().beep();
			return;
		}

		String tag = text.substring(start + 1,end);

		// use a StringTokenizer to parse the tag
		String elementName = null;
		Hashtable attributes = new Hashtable();
		String attributeName = null;
		boolean seenEquals = false;
		boolean empty = false;

		/* StringTokenizer does not support disabling or changing
		 * the escape character, so we have to work around it here. */
		char backslashSub = 127;
		StreamTokenizer st = new StreamTokenizer(new StringReader(
			tag.replace('\\',backslashSub)));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');
		st.ordinaryChar('/');
		st.ordinaryChar('=');

		// this is an incredibly brain-dead parser.
		try
		{
loop:			for(;;)
			{
				switch(st.nextToken())
				{
				case StreamTokenizer.TT_EOF:
					if(attributeName != null)
					{
						// in HTML, can have attributes
						// without values.
						attributes.put(attributeName,
							Boolean.TRUE);
					}
					break loop;
				case '=':
					seenEquals = true;
					break;
				case StreamTokenizer.TT_WORD:
					if(elementName == null)
					{
						elementName = st.sval;
						break;
					}
					else if(attributeName == null)
					{
						attributeName = (html
							? st.sval.toLowerCase()
							: st.sval);
						break;
					}
				case '"':
				case '\'':
					if(attributeName != null)
					{
						if(seenEquals)
						{
							attributes.put(attributeName,
								st.sval.replace(backslashSub,'\\'));
							seenEquals = false;
						}
						else
						{
							attributes.put(attributeName,
								Boolean.TRUE);
						}
						attributeName = null;
					}
					break;
				case '/':
					empty = true;
					break;
				}
			}
		}
		catch(IOException io)
		{
			// won't happen
		}

		ElementDecl elementDecl = (ElementDecl)elements.get(
			(html ? elementName.toLowerCase() : elementName));
		if(elementDecl == null)
		{
			String[] pp = { elementName };
			GUIUtilities.error(view,"xml-edit-tag.undefined-element",pp);
			return;
		}

		EditTagDialog dialog = new EditTagDialog(view,elementDecl,
			attributes,empty,ids);

		String newTag = dialog.getNewTag();

		Buffer buffer = editPane.getBuffer();
		try
		{
			buffer.beginCompoundEdit();

			if(newTag != null || removeIfCancel)
				buffer.remove(start,end - start + 1);

			if(newTag != null)
				buffer.insertString(start,newTag,null);
		}
		catch(BadLocationException ble)
		{
			Log.log(Log.ERROR,XmlPlugin.class,ble);
		}
		finally
		{
			buffer.endCompoundEdit();
		}
	}
}
