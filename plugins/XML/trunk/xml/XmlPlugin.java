/*
 * XmlPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
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
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import sidekick.SideKickPlugin;
import xml.options.*;
import xml.parser.*;
//}}}

public class XmlPlugin extends EBPlugin
{
	//{{{ start() method
	public void start()
	{
		
		/* System.setProperty("javax.xml.parsers.SAXParserFactory",
			"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		*/
		CatalogManager.init();

		XmlActions.propertiesChanged();
		CatalogManager.propertiesChanged();

		tagMouseHandler = new TagMouseHandler();

		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				panes[i].getTextArea().getPainter()
					.addMouseListener(
					tagMouseHandler);
			}
			view = view.getNext();
		}
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		View view = jEdit.getFirstView();
		while(view != null)
		{
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++)
			{
				panes[i].getTextArea().getPainter()
					.removeMouseListener(
					tagMouseHandler);
			}
			view = view.getNext();
		}

		CatalogManager.save();

		CatalogManager.uninit();
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ EditPaneUpdate
		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate)msg;
			EditPane editPane = epu.getEditPane();

			if(epu.getWhat() == EditPaneUpdate.CREATED)
			{
				editPane.getTextArea().getPainter().addMouseListener(
					tagMouseHandler);
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				editPane.getTextArea().getPainter().removeMouseListener(
					tagMouseHandler);
			}
		} //}}}
		//{{{ PropertiesChanged
		else if(msg instanceof PropertiesChanged)
		{
			XmlActions.propertiesChanged();
			CatalogManager.propertiesChanged();
		} //}}}
	} //}}}

	//{{{ isDelegated() method
	/**
	 * Returns if the caret is inside a delegated region in the
	 * specified text area. This is used in a few places.
	 */
	public static boolean isDelegated(JEditTextArea textArea)
	{
		JEditBuffer buffer = textArea.getBuffer();
		ParserRuleSet rules = buffer.getRuleSetAtOffset(
			textArea.getCaretPosition());

		String rulesetName = rules.getSetName();
		String modeName = rules.getModeName();

		// Am I an idiot?
		if(rulesetName != null && (rulesetName.startsWith("PHP")
			|| rulesetName.equals("CDATA")))
			return true;

		return jEdit.getProperty("mode." + modeName + "."
			+ SideKickPlugin.PARSER_PROPERTY) == null;
	} //}}}

	//{{{ uriToFile() method
	public static String uriToFile(String uri)
	{
		if(uri.startsWith("file:/"))
		{
			int start;
			if(uri.startsWith("file:///") && OperatingSystem.isDOSDerived())
				start = 8;
			else if(uri.startsWith("file://"))
				start = 7;
			else
				start = 5;

			StringBuffer buf = new StringBuffer();
			for(int i = start; i < uri.length(); i++)
			{
				char ch = uri.charAt(i);
				if(ch == '/')
					buf.append(java.io.File.separatorChar);
				else if(ch == '%')
				{
					String str = uri.substring(i + 1,i + 3);
					buf.append((char)Integer.parseInt(str,16));
					i += 2;
				}
				else
					buf.append(ch);
			}
			uri = buf.toString();
		}
		return uri;
	} //}}}

	private TagMouseHandler tagMouseHandler;

	//{{{ TagMouseHandler class
	/**
	 * Displays the "Edit Tag Dialog" when a tag is selected from the completion
	 * dialog with the mouse.
	 */
	static class TagMouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent evt)
		{
			if(evt.getClickCount() == 2
				&& ((OperatingSystem.isMacOS() && evt.isMetaDown())
				|| (!OperatingSystem.isMacOS() && evt.isControlDown())))
			{
				final View view = GUIUtilities.getView(
					(Component)evt.getSource());
				if(!(SideKickPlugin.getParserForBuffer(view.getBuffer())
					instanceof XmlParser))
					return;

				JEditTextArea textArea = view.getTextArea();
				textArea.setCaretPosition(textArea.xyToOffset(
					evt.getX(),evt.getY()));

				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						XmlActions.showEditTagDialog(view);
					}
				});
			}
		}
	} //}}}
}
