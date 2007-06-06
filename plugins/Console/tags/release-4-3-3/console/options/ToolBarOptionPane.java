/*
 * ToolBarOptionPane
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 Alan Ezust
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

package console.options;

// {{{ imports
import java.awt.GridLayout;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;

import console.ConsolePlugin;
import console.commando.CommandoCommand;
import console.commando.CommandoToolBar;

// }}}

public class ToolBarOptionPane extends AbstractOptionPane
{

	// {{{ public ToolBarOptionPane()
	public ToolBarOptionPane()
	{
		super("console.toolbar");
	}

	// }}}
	
	// {{{ public void _init()
	protected void _init()
	{

		addComponent(enabledCheckBox = new JCheckBox(jEdit
			.getProperty("options.console.general.commando.toolbar")));

		enabledCheckBox.getModel().setSelected(
			jEdit.getBooleanProperty("commando.toolbar.enabled"));


		createButtons();
	}
	// }}}
	
	// {{{ protected void createButtons()
	protected void createButtons()
	{

		checkBoxes.clear();
		ActionSet allActions = ConsolePlugin.getAllCommands();
		GridLayout glayout = new GridLayout(0, 3);
		addSeparator("options.console.toolbar.buttons");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(glayout);

		for (EditAction ea : allActions.getActions())
		{
			CommandoCommand cc = (CommandoCommand) ea;
			String label = cc.getShortLabel();
			JCheckBox cb = new JCheckBox(label);
			boolean selected = jEdit.getBooleanProperty("commando.visible." + label,
				true);
			cb.setSelected(selected);
			checkBoxes.put(label, cb);
			buttonPanel.add(cb);
		}
		addComponent(buttonPanel);
	} // }}}

	// {{{ protected void _save()
	protected void _save()
	{
		jEdit.setBooleanProperty("commando.toolbar.enabled", enabledCheckBox.isSelected());
		for (JCheckBox cb : checkBoxes.values())
		{
			jEdit.setBooleanProperty("commando.visible." + cb.getText(), cb
				.isSelected());
		}
		jEdit.saveSettings();
		CommandoToolBar.init();
	}
	// }}}

	private JCheckBox enabledCheckBox;

	TreeMap<String, JCheckBox> checkBoxes = new TreeMap<String, JCheckBox>();
}


