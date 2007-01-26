/*
 * SideKickModeOptionsPane.java - 
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Alan Ezust
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


package sidekick;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.jEdit;

// {{{ SideKickModeOptionsPane
/**
 * 
 * Mode-Specific options for SideKick - a custom ModeOptionsPane which 
 * includes 3 questions for the user, and provides an example of
 * how to extend ModeOptionsPane and set/reset the mode options.
 * 
 * @author Alan Ezust
 *
 */
public class SideKickModeOptionsPane extends ModeOptionsPane
{
	JCheckBox showStatusWindow;
	JCheckBox treeFollowsCaret;
	JComboBox autoExpandTreeDepth;

	// {{{ SideKickModeOptionsPane ctor
	public SideKickModeOptionsPane() 
	{
		super("sidekick.mode");
	} // }}}
		
	// {{{ init()
	protected void _init() {
			
		showStatusWindow = new JCheckBox(jEdit.getProperty("options." + SideKick.SHOW_STATUS));
		addComponent(showStatusWindow);
		
		treeFollowsCaret = new JCheckBox(jEdit.getProperty("options.sidekick.tree-follows-caret"));
		addComponent(treeFollowsCaret);

		autoExpandTreeDepth = new JComboBox();
		addComponent(jEdit.getProperty("options.sidekick.auto-expand-tree-depth"), autoExpandTreeDepth);
//		autoExpandTreeDepth.addActionListener(new ActionHandler());
		autoExpandTreeDepth.addItem(ModeOptionsDialog.ALL);
		for (int i = 0; i <= 10; i++)
			autoExpandTreeDepth.addItem(String.valueOf(i));
		addComponent(autoExpandTreeDepth);
		_load();
	} // }}}
	
	// {{{ _load()
	protected void _load() 
	{
		
		boolean tfc = getBooleanProperty(SideKick.FOLLOW_CARET);
		treeFollowsCaret.setSelected(tfc);
		showStatusWindow.setSelected(getBooleanProperty(SideKick.SHOW_STATUS));
		int item = getIntegerProperty(SideKick.AUTO_EXPAND_DEPTH, 1) + 1;
		autoExpandTreeDepth.setSelectedIndex(item);
	} // }}}
	
	// {{{ _save()
	protected void _save() 
	{
		setBooleanProperty(SideKick.FOLLOW_CARET, treeFollowsCaret.isSelected());
		setBooleanProperty(SideKick.SHOW_STATUS, showStatusWindow.isSelected());
		String value = (String)autoExpandTreeDepth.getSelectedItem();
		String depth = value.equals(ModeOptionsDialog.ALL) ? "-1" : value;
		setProperty(SideKick.AUTO_EXPAND_DEPTH, depth);
	} // }}}

	// {{{ reset()
	protected void _reset()
	{
		clearModeProperty(SideKick.FOLLOW_CARET);
		clearModeProperty(SideKick.AUTO_EXPAND_DEPTH);
		clearModeProperty(SideKick.SHOW_STATUS);
	} // }}}

} // }}}

