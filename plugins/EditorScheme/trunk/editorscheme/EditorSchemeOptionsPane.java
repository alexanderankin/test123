/*
 * EditorSchemeOptionsPane.java - EditorScheme pugin.
 * Copyright (C) 2002 Ollie Rutherfurd
 *
 * :folding=explicit:collapseFolds=1:
 *
 * {{{This program is free software; you can redistribute it and/or
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.}}}
 *
 * $Id: EditorSchemeOptionsPane.java,v 1.4 2003/11/10 14:23:18 orutherfurd Exp $
 */

package editorscheme;

//{{{ Imports
import java.util.ArrayList;
import javax.swing.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
//}}}


public class EditorSchemeOptionsPane extends AbstractOptionPane
{

	public EditorSchemeOptionsPane()
	{
		super("editor-scheme");
	}

	//{{{ _init()
	protected void _init()
	{
		addComponent(Box.createVerticalStrut(6));

		addComponent(new JLabel(jEdit.getProperty(
			"options.editor-scheme.groups.label")));

		addComponent(Box.createVerticalStrut(3));

		ArrayList groups = EditorScheme.getPropertyGroups();
		JCheckBoxList.Entry[] entries = new JCheckBoxList.Entry[groups.size()];
		for(int i=0; i < groups.size(); i++)
		{
			EditorScheme.PropertyGroup group = (EditorScheme.PropertyGroup)groups.get(i);
			entries[i] = new JCheckBoxList.Entry(group.apply,group);
		}

		this.groups = new JCheckBoxList(entries);
		addComponent(new JScrollPane(this.groups));
		addComponent(Box.createVerticalStrut(6));

		addComponent(autoApply = new JCheckBox(
			jEdit.getProperty("options.editor-scheme.autoapply.label"),
			jEdit.getBooleanProperty("editor-scheme.autoapply",true)));

	}//}}}

	//{{{ _save()
	public void _save()
	{
		JCheckBoxList.Entry[] entries = groups.getValues();
		for(int i=0; i < entries.length; i++)
		{
			EditorScheme.PropertyGroup group = 
				(EditorScheme.PropertyGroup)entries[i].getValue();
			group.apply = entries[i].isChecked();
			jEdit.setBooleanProperty(
				"editor-scheme." + group.name + ".apply", group.apply);
		}

		jEdit.setBooleanProperty(
			"editor-scheme.autoapply",autoApply.isSelected());
			
	}//}}}

	private JCheckBoxList groups;
	private JCheckBox autoApply;

}
