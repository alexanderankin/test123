/*
 * TagPalette.java
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

import javax.swing.table.*;
import javax.swing.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class TagPalette extends JPanel implements DockableWindow
{
	public TagPalette(View view)
	{
		this.view = view;
	}

	public String getName()
	{
		return "xml-tag-palette";
	}

	public Component getComponent()
	{
		return this;
	}

	// package-private members
	void setDeclaredElements(Hashtable elements)
	{
		this.elements = elements;
	}

	// private members
	private View view;
	private Hashtable elements;
}
