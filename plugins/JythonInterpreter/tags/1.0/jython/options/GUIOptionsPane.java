/*
 * GUIOptionsPane.java - GUI Options for JythonInterpreter plugin.
 *
 * Copyright (C) 2003 Ollie Rutherfurd
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
 *
 * $Id: GUIOptionsPane.java,v 1.2 2003/03/11 23:29:49 fruhstuck Exp $
 */

package jython.options;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

public class GUIOptionsPane extends AbstractOptionPane
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//{{{ GUIOptionsPane constructor
	public GUIOptionsPane()
	{
		super("jython.gui");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		font = new FontSelector(jEdit.getFontProperty("jython.font"));
		addComponent(jEdit.getProperty("options.jython.gui.font"), font);

		addComponent(jEdit.getProperty("options.jython.gui.bgColor"),
			bgColor = new ColorWellButton(
				jEdit.getColorProperty("jython.bgColor")
			)
		);
		addComponent(jEdit.getProperty("options.jython.gui.resultColor"),
			resultColor = new ColorWellButton(
				jEdit.getColorProperty("jython.resultColor")
			)
		);
		addComponent(jEdit.getProperty("options.jython.gui.textColor"),
			textColor = new ColorWellButton(
				jEdit.getColorProperty("jython.textColor")
			)
		);
		addComponent(jEdit.getProperty("options.jython.gui.errorColor"),
			errorColor = new ColorWellButton(
				jEdit.getColorProperty("jython.errorColor")
			)
		);
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		jEdit.getFontProperty("jython.font", font.getFont());

		jEdit.setColorProperty("jython.bgColor",
			bgColor.getSelectedColor());
		jEdit.setColorProperty("jython.resultColor",
			resultColor.getSelectedColor());
		jEdit.setColorProperty("jython.textColor",
			textColor.getSelectedColor());
		jEdit.setColorProperty("jython.errorColor",
			errorColor.getSelectedColor());
	} //}}}

	//{{{ Instance variables
	private FontSelector font;
	private ColorWellButton bgColor;
	private ColorWellButton resultColor;
	private ColorWellButton textColor;
	private ColorWellButton errorColor;
	//}}}
}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=1:
