/*
 * ReferenceOptionPane.java - Reference options panel
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Anthony Roy
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
 */
//TODO:Doesn't compile
package uk.co.antroy.latextools; 
//{{{ Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import javax.swing.text.*;
//}}}

public class ReferenceOptionPane extends AbstractOptionPane
{
	public ReferenceOptionPane()
	{
		super("reference");
	} 

	protected void _init()
	{
		addComponent(inserttags = new JCheckBox(jEdit.getProperty(
			"options.reference.inserttags")));
		inserttags.getModel().setSelected(jEdit.getBooleanProperty(
			"reference.inserttags"));
	} 

	protected void _save()
	{
		jEdit.setBooleanProperty("reference.inserttags",inserttags
			.getModel().isSelected());

	} 

	private JCheckBox inserttags;

}
