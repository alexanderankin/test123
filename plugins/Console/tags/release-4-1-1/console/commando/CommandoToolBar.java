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

//{{{ Imports
import console.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
import org.gjt.sp.jedit.*;
//}}}

public class CommandoToolBar extends JToolBar implements EBComponent
{
	//{{{ CommandoToolBar constructor
	public CommandoToolBar(View view)
	{
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setFloatable(false);

		this.view = view;

		updateButtons();
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		if(msg instanceof DynamicMenuChanged
			&& ConsolePlugin.MENU.equals
			(((DynamicMenuChanged)msg).getMenuName()))
		{
			updateButtons();
		}
	} //}}}

	//{{{ Private members
	private View view;

	//{{{ updateButtons() method
	private void updateButtons()
	{
		removeAll();

		ActionListener actionHandler = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				new CommandoDialog(view,evt.getActionCommand());
			}
		};

		EditAction[] commands = ConsolePlugin.getCommandoCommands();
		for(int i = 0; i < commands.length; i++)
		{
			EditAction command = commands[i];
			JButton button = new JButton(command.getLabel());
			button.setActionCommand(command.getName());
			button.addActionListener(actionHandler);
			button.setRequestFocusEnabled(false);
			button.setMargin(new Insets(1,2,1,2));
			add(button);
		}

		add(Box.createGlue());
	} //}}}

	//}}}
}
