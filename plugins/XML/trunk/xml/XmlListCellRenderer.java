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
			setText("<" + element.name
				+ (element.empty ? " /" : "")
				+ ">");
		}
		else if(value instanceof EntityDecl)
		{
			EntityDecl entity = (EntityDecl)value;
			setText("&" + entity.name + "; ("
				+ (entity.type == EntityDecl.INTERNAL
				? entity.value : "external") + ")");
		}

		return this;
	}
}
