/*
 * XmlPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports
import javax.swing.*;
import java.util.HashMap;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import xml.parser.*;
//}}}

public class XmlPlugin extends EBPlugin
{
	public static final String TREE_NAME = "xml-tree";
	public static final String INSERT_NAME = "xml-insert";

	public static final String ELEMENT_TREE_PROPERTY = "xml.element-tree";
	public static final String COMPLETION_INFO_PROPERTY = "xml.completion-info";
	public static final String IDS_PROPERTY = "xml.ids";

	//{{{ start() method
	public void start()
	{
		CatalogManager.propertiesChanged();
		XmlActions.propertiesChanged();
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
				tagHighlights.put(editPane,tagHighlight);
				textAreaPainter.addExtension(tagHighlight);
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
			{
				tagHighlights.remove(editPane);
			}
			else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				TagHighlight highlight = (TagHighlight)
					tagHighlights.get(editPane);
				highlight.bufferChanged(editPane.getBuffer());
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

	//{{{ Private members
	private static HashMap parsers = new HashMap();
	private static HashMap tagHighlights = new HashMap();
	//}}}
}
