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
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import xml.options.*;
import xml.parser.*;
//}}}

public class XmlPlugin extends EBPlugin
{
	//{{{ Some constants
	public static final String TREE_NAME = "xml-tree";
	public static final String INSERT_NAME = "xml-insert";

	public static final String ELEMENT_TREE_PROPERTY = "xml.element-tree";
	public static final String PARSER_PROPERTY = "xml.parser";
	public static final String COMPLETION_INFO_PROPERTY = "xml.completion-info";
	public static final String IDS_PROPERTY = "xml.ids";
	//}}}

	//{{{ start() method
	public void start()
	{
		System.setProperty("javax.xml.parsers.SAXParserFactory",
			"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		XmlActions.propertiesChanged();
	} //}}}

	//{{{ stop() method
	public void stop()
	{
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
		grp.addOptionPane(new CompletionOptionPane());
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
				EditBus.addToBus(tagHighlight);
				tagHighlights.put(editPane,tagHighlight);

				textAreaPainter.addMouseListener(new TagMouseHandler());
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				TagHighlight highlight = (TagHighlight)tagHighlights
					.remove(editPane);
				EditBus.removeFromBus(highlight);
			}
		} //}}}
		//{{{ PropertiesChanged
		else if(msg instanceof PropertiesChanged)
		{
			XmlActions.propertiesChanged();
			CatalogManager.propertiesChanged();
			TagHighlight.propertiesChanged();
		} //}}}
		//{{{ ViewUpdate
		else if(msg instanceof ViewUpdate)
		{
			ViewUpdate vu = (ViewUpdate)msg;
			View view = vu.getView();

			if(vu.getWhat() == ViewUpdate.CREATED)
				parsers.put(view,new XmlParser(view));
			else if(vu.getWhat() == ViewUpdate.CLOSED)
			{
				XmlParser parser = (XmlParser)parsers.get(view);
				parser.dispose();
				parsers.remove(view);
			}
		} //}}}
	} //}}}

	//{{{ getParser() method
	public static XmlParser getParser(View view)
	{
		return (XmlParser)parsers.get(view);
	} //}}}

	//{{{ getParserType() method
	public static String getParserType(Buffer buffer)
	{
		String prop = buffer.getStringProperty(XmlPlugin.PARSER_PROPERTY);
		if(prop == null || prop.equals("xml"))
			return prop;
		else if(prop.equals("html-really"))
			return "html";
		else if(prop.equals("html"))
		{
			if(buffer.getLineText(0).toLowerCase().startsWith("<?xml"))
			{
				buffer.setProperty(XmlPlugin.PARSER_PROPERTY,"xml");
				return "xml";
			}
			else if(buffer.getName().toLowerCase().endsWith(".dtd"))
			{
				buffer.setProperty(XmlPlugin.PARSER_PROPERTY,null);
				return null;
			}
			else
			{
				buffer.setProperty(XmlPlugin.PARSER_PROPERTY,"html-really");
				return "html";
			}
		}
		else
		{
			return null;
		}
	} //}}}

	//{{{ Private members
	private static HashMap parsers = new HashMap();
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
				if(XmlPlugin.getParserType(view.getBuffer()) == null)
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
