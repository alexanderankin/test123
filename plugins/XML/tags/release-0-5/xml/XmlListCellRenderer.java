/*
 * XmlListCellRenderer.java
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import javax.swing.*;
import java.awt.Component;

class XmlListCellRenderer extends DefaultListCellRenderer
{
	public static final ImageIcon ELEMENT_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/element.gif"));
	public static final ImageIcon EMPTY_ELEMENT_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/empty_element.gif"));
	public static final ImageIcon INTERNAL_ENTITY_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/internal_entity.gif"));
	public static final ImageIcon EXTERNAL_ENTITY_ICON = new ImageIcon(
		XmlListCellRenderer.class.getResource("/xml/external_entity.gif"));

	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list,value,index,
			isSelected,cellHasFocus);

		if(value instanceof ElementDecl)
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
		else
			setIcon(null);

		return this;
	}
}
