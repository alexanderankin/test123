/*
 * XmlListCellRenderer.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
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
import java.awt.Component;
import org.gjt.sp.jedit.jEdit;
import xml.completion.*;
//}}}

public class XmlListCellRenderer extends DefaultListCellRenderer
{
	public static final XmlListCellRenderer INSTANCE = new XmlListCellRenderer();

	//{{{ Icons
	public static final ImageIcon COMMENT_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/Comment.png"));
	public static final ImageIcon CDATA_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/CDATA.png"));
	public static final ImageIcon ELEMENT_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/Element.png"));
	public static final ImageIcon EMPTY_ELEMENT_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/EmptyElement.png"));
	public static final ImageIcon INTERNAL_ENTITY_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/InternalEntity.png"));
	public static final ImageIcon EXTERNAL_ENTITY_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/ExternalEntity.png"));
	public static final ImageIcon ID_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/ID.png"));
	//}}}

	//{{{ getListCellRendererComponent() method
	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list,null,index,
			isSelected,cellHasFocus);

		if(value instanceof Comment)
		{
			setIcon(COMMENT_ICON);
			setText("!--");
		}
		else if(value instanceof CDATA)
		{
			setIcon(CDATA_ICON);
			setText("![CDATA[");
		}
		else if(value instanceof ClosingTag)
		{
			setIcon(ELEMENT_ICON);
			setText("/" + ((ClosingTag)value).name);
		}
		else if(value instanceof ElementDecl)
		{
			ElementDecl element = (ElementDecl)value;
			setIcon(element.empty ? EMPTY_ELEMENT_ICON : ELEMENT_ICON);
			setText(element.name);
		}
		else if(value instanceof EntityDecl)
		{
			EntityDecl entity = (EntityDecl)value;
			setIcon(entity.type == EntityDecl.INTERNAL
				? INTERNAL_ENTITY_ICON
				: EXTERNAL_ENTITY_ICON);
			setText(entity.name);
		}
		else if(value instanceof IDDecl)
		{
			setIcon(ID_ICON);

			// it's toString() already does this cos I'm too
			// lazy to write a custom renderer for the edit tag
			// dialog box.

			setText(value.toString());
		}
		else
			setIcon(null);

		return this;
	} //}}}

	public static class ClosingTag
	{
		public String name;

		public ClosingTag(String name)
		{
			this.name = name;
		}
	}

	public static class Comment {}

	public static class CDATA {}
}
