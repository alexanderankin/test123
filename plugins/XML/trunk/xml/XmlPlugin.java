/*
 * XmlPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2001, 2002 Slava Pestov
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
import java.util.HashMap;
import java.util.Vector;
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
	public static final XmlParser XML_PARSER_INSTANCE = new SAXParserImpl();
	public static final XmlParser HTML_PARSER_INSTANCE = new SwingHTMLParserImpl();

	//{{{ start() method
	public void start()
	{
		System.setProperty("javax.xml.parsers.SAXParserFactory",
			"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		SideKickPlugin.registerParser(XML_PARSER_INSTANCE);
		SideKickPlugin.registerParser(HTML_PARSER_INSTANCE);

		XmlActions.propertiesChanged();
	} //}}}

	//{{{ stop() method
	public void stop()
	{
		SideKickPlugin.unregisterParser(XML_PARSER_INSTANCE);
		SideKickPlugin.unregisterParser(HTML_PARSER_INSTANCE);

		CatalogManager.save();
	} //}}}

	//{{{ createMenuItems() method
	public void createMenuItems(Vector menuItems)
	{
		menuItems.addElement(GUIUtilities.loadMenu("xml-menu"));
	} //}}}

	//{{{ createOptionPanes() method
	public void createOptionPanes(OptionsDialog dialog)
	{
		OptionGroup grp = new OptionGroup("xml");
		grp.addOptionPane(new GeneralOptionPane());
		grp.addOptionPane(new CatalogsOptionPane());
		dialog.addOptionGroup(grp);
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
				JEditTextArea textArea = editPane.getTextArea();
				TextAreaPainter textAreaPainter = textArea.getPainter();

				TagHighlight tagHighlight = new TagHighlight(
					editPane.getView(),textArea);
				tagHighlights.put(editPane,tagHighlight);

				textAreaPainter.addMouseListener(new TagMouseHandler());
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				TagHighlight tagHighlight = (TagHighlight)tagHighlights
					.remove(editPane);
				tagHighlight.dispose();
			}
		} //}}}
		//{{{ PropertiesChanged
		else if(msg instanceof PropertiesChanged)
		{
			XmlActions.propertiesChanged();
			CatalogManager.propertiesChanged();
			TagHighlight.propertiesChanged();
		} //}}}
	} //}}}

	//{{{ isDelegated() method
	/**
	 * Returns if the caret is inside a delegated region in the
	 * specified text area. This is used in a few places.
	 */
	public static boolean isDelegated(JEditTextArea textArea)
	{
		Buffer buffer = textArea.getBuffer();
		ParserRuleSet rules = buffer.getRuleSetAtOffset(
			textArea.getCaretPosition());

		String rulesetName = rules.getName();
		String modeName = rules.getMode().getName();

		// Am I an idiot?
		if(rulesetName != null && (rulesetName.startsWith("PHP")
			|| rulesetName.equals("CDATA")))
			return true;

		return jEdit.getProperty("mode." + modeName + "."
			+ SideKickPlugin.PARSER_PROPERTY) == null;
	} //}}}

	//{{{ Private members
	private static HashMap tagHighlights = new HashMap();
	//}}}

	//{{{ TagMouseHandler class
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
