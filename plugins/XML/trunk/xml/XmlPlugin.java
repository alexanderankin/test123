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
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
//}}}

public class XmlPlugin extends EBPlugin
{
	public static final String TREE_NAME = "xml-tree";
	public static final String INSERT_NAME = "xml-insert";

	public static final String ELEMENT_TREE_PROPERTY = "xml.element-tree";
	public static final String COMPLETION_INFO_PROPERTY = "xml.completion-info";

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
		grp.addOptionPane(new TagHighlightOptionPane());
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

				TagHighlight tagHighlight =
					(TagHighlight)TagHighlight.addHighlightTo(editPane);

				textAreaPainter.addCustomHighlight(tagHighlight);
			}
			else if(epu.getWhat() == EditPaneUpdate.DESTROYED)
				TagHighlight.removeHighlightFrom(editPane);
			else if(epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
				TagHighlight.bufferChanged(editPane);
		} //}}}
		//{{{ BufferUpdate
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bu = (BufferUpdate)msg;
			Buffer buffer = bu.getBuffer();

			if(bu.getWhat() == BufferUpdate.CREATED)
				TagHighlight.bufferCreated(buffer);
			else if(bu.getWhat() == BufferUpdate.LOADED)
				TagHighlight.bufferLoaded(buffer);
			else if(bu.getWhat() == BufferUpdate.CLOSED)
				TagHighlight.bufferClosed(buffer);
		} //}}}
		//{{{ PropertiesChanged
		else if(msg instanceof PropertiesChanged)
		{
			XmlActions.propertiesChanged();
			CatalogManager.propertiesChanged();
			TagHighlight.propertiesChanged();
		} //}}}
	} //}}}
}
