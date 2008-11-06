/*
 * GeneralOptionPane.java
 *
 * Copyright 2004 Ollie Rutherfurd <oliver@jedit.org>
 * Copyright 2007 Shlomy Reinstein <shlomy@users.sourceforge.net>
 *
 * This file is part of TagsPlugin
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA.
 *
 * $Id$
 */

package tags.options;

//{{{ imports
import org.gjt.sp.jedit.*;
import javax.swing.*;
import tags.*;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("tags.general");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		dialogsUnderCursor = new JCheckBox(
			jEdit.getProperty("options.tags.general.open-dialogs-under-cursor.label"),
			jEdit.getBooleanProperty("options.tags.open-dialogs-under-cursor",false));
		addComponent(dialogsUnderCursor);

		extendThroughDot = new JCheckBox(
			jEdit.getProperty("options.tags.general.tag-extends-through-dot.label"),
			jEdit.getBooleanProperty("options.tags.tag-extends-through-dot",false));
		addComponent(extendThroughDot);

		searchAll = new JCheckBox(
			jEdit.getProperty("options.tags.general.tag-search-all-files.label"),
			jEdit.getBooleanProperty("options.tags.tag-search-all-files",false));
		addComponent(searchAll);

		useLineNumbers = new JCheckBox(
			jEdit.getProperty("options.tags.general.tag-use-line-numbers.label"),
			jEdit.getBooleanProperty("options.tags.tag-use-line-numbers",false));
		addComponent(useLineNumbers);
		
		cacheSize = new JTextField("" + 
			jEdit.getIntegerProperty("options.tags.cache-size",TagFileManager.CACHE_SIZE));
		addComponent(jEdit.getProperty("options.tags.general.cache-size.label"),
			cacheSize);

		cacheAll = new JCheckBox(
			jEdit.getProperty("options.tags.general.cache-local.label"),
			jEdit.getBooleanProperty("options.tags.cache-all",false));
		addComponent(cacheAll);
	} //}}}

	protected void _save()
	{
		jEdit.setBooleanProperty("options.tags.open-dialogs-under-cursor",
			dialogsUnderCursor.isSelected());
		jEdit.setBooleanProperty("options.tags.tag-extends-through-dot", 
			extendThroughDot.isSelected());
		jEdit.setBooleanProperty("options.tags.tag-search-all-files", 
			searchAll.isSelected());
		jEdit.setBooleanProperty("options.tags.tag-use-line-numbers", 
			useLineNumbers.isSelected());
		jEdit.setIntegerProperty("options.tags.cache-size", 
			Integer.parseInt(cacheSize.getText()));
		jEdit.setBooleanProperty("options.tags.cache-all", 
			cacheAll.isSelected());
	}

	//{{{ private declarations
	JCheckBox dialogsUnderCursor;
	JCheckBox extendThroughDot;
	JCheckBox searchAll;
	JCheckBox useLineNumbers;
	JTextField cacheSize;
	JCheckBox cacheAll;
	//}}}
}

// :collapseFolds=0:noTabs=false:deepIndent=false:folding=explicit:
