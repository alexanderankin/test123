/*
 * CommandoToolBar.java - Commando tool bar
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
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

package console.commando;

// {{{ Imports
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;

import console.ConsolePlugin;

// }}}

public class CommandoToolBar extends JToolBar implements EBComponent
{

	// {{{ init()
	public static void init()
	{
		remove();
		if (!jEdit.getBooleanProperty("commando.toolbar.enabled"))
			return;
		View[] view = jEdit.getViews();
		for (int i = 0; i < view.length; ++i)
		{
			if (view[i].isPlainView())
				continue;
			CommandoToolBar tb = new CommandoToolBar(view[i]);
			view[i].addToolBar(tb);
			smToolBarMap.put(view[i], tb);
		}
	}

	// }}}
	
	// {{{ remove()
	/** Remove the instance from the view */
	public static void remove()
	{
		Iterator itr = smToolBarMap.keySet().iterator();
		while (itr.hasNext()) 
		{
			View v = (View) itr.next();
			if (v == null) continue;
			CommandoToolBar tb =(CommandoToolBar) smToolBarMap.get(v);
			if (tb != null) {
				v.removeToolBar(tb);
			}
		}
		smToolBarMap.clear();
	}
	// }}}

	// {{{ CommandoToolBar constructor
	private CommandoToolBar(View dockable)
	{
		view = dockable;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setFloatable(true);
		updateButtons();

	} 
	// }}}

	// {{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} 
	// }}}

	// {{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	}
	// }}}

	// {{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof DynamicMenuChanged
				&& ConsolePlugin.MENU.equals(((DynamicMenuChanged) msg)
						.getMenuName()))
		{
			updateButtons();
		}
	} // }}}

	// {{{ updateButtons() method
	private void updateButtons()
	{
		removeAll();

		ActionListener actionHandler = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				new CommandoDialog(view, evt.getActionCommand());
			}
		};
		ConsolePlugin.rescanCommands();
		EditAction[] commands = ConsolePlugin.getCommandoCommands();
		for (int i = 0; i < commands.length; i++)
		{
			CommandoCommand command = (CommandoCommand) commands[i];
			CommandoButton button = new CommandoButton(command);
			button.setActionCommand(command.getName());
			button.addActionListener(actionHandler);
			button.setRequestFocusEnabled(false);
			button.setMargin(new Insets(1, 2, 1, 2));
			add(button);
		}

		add(Box.createGlue());
	} 
	// }}}

	private View view;
	
	/**
	 * For each view, we might add a toolbar.
	 * This map keeps track of what
	 * views had toolbars added to them.
	 */
	static HashMap smToolBarMap = new HashMap();
	
}
