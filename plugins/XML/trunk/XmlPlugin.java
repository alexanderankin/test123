/*
 * XmlPlugin.java
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

import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Component;
import java.util.Vector;
import org.gjt.sp.jedit.gui.DockableWindow;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class XmlPlugin extends EBPlugin
{
	public static final String NAME = "xml-tree";

	public void start()
	{
		documentHandler = new DocumentHandler();
		EditBus.addToNamedList(DockableWindow.DOCKABLE_WINDOW_LIST,NAME);
		errorSource = new DefaultErrorSource("XML");
		EditBus.addToNamedList(ErrorSource.ERROR_SOURCES_LIST,errorSource);
	}

	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenuItem("xml-tree"));
	}

	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof CreateDockableWindow)
		{
			CreateDockableWindow cmsg = (CreateDockableWindow)msg;
			if(cmsg.getDockableWindowName().equals(NAME))
				cmsg.setDockableWindow(new XmlTree(cmsg.getView()));
		}
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate)msg;
			Buffer buffer = bu.getBuffer();
			if(bu.getWhat() == BufferUpdate.MODE_CHANGED
				|| bu.getWhat() == BufferUpdate.LOADED)
			{
				if(buffer.getBooleanProperty("useXmlPlugin"))
					buffer.addDocumentListener(documentHandler);
				else
					buffer.removeDocumentListener(documentHandler);

				/* // reparse buffer if it is open in at least one view
				View view = jEdit.getFirstView();
loop:				while(view != null)
				{
					EditPane[] panes = view.getEditPanes();
					for(int i = 0; i < panes.length; i++)
					{
						if(panes[i].getBuffer() == buffer)
						{
							parse(buffer);
							break loop;
						}
					}
				} */
			}
			else if(bu.getWhat() == BufferUpdate.CLOSED)
				buffer.removeDocumentListener(documentHandler);
		}
	}

	// package-private members
	static void parse(Buffer buffer)
	{
		errorSource.clear();

		// check for non-XML file
		if(!buffer.getBooleanProperty("useXmlPlugin"))
		{
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(buffer.getName());
			DefaultTreeModel model = new DefaultTreeModel(root);

			root.insert(new DefaultMutableTreeNode(
				jEdit.getProperty("xml-tree.not-xml-file")),0);

			model.reload(root);
			EditBus.send(new XmlTreeParsed(buffer,model));
			return;
		}

		if(daemon != null)
		{
			daemon.stop();
			daemon = null;
		}

		daemon = new XmlDaemon(buffer);
		daemon.start();
	}

	static void daemonFinished()
	{
		daemon = null;
	}

	static void addError(String file, int line, String message)
	{
		errorSource.addError(ErrorSource.ERROR,file,
			line,0,0,message);
	}

	// private members
	private static DocumentHandler documentHandler;
	private static Timer timer;
	private static XmlDaemon daemon;
	private static DefaultErrorSource errorSource;

	private static void parseWithDelay(Buffer buffer)
	{
		if(timer != null)
			timer.stop();

		timer = new Timer(0,new XmlTimer(buffer));
		timer.setInitialDelay(500);
		timer.setRepeats(false);
		timer.start();
	}

	static class DocumentHandler implements DocumentListener
	{
		public void insertUpdate(DocumentEvent evt)
		{
			parseWithDelay((Buffer)evt.getDocument());
		}

		public void removeUpdate(DocumentEvent evt)
		{
			parseWithDelay((Buffer)evt.getDocument());
		}

		public void changedUpdate(DocumentEvent evt)
		{
		}
	}

	static class XmlTimer implements ActionListener
	{
		Buffer buffer;

		XmlTimer(Buffer buffer)
		{
			this.buffer = buffer;
		}

		public void actionPerformed(ActionEvent evt)
		{
			parse(buffer);
		}
	}
}
