/*
 * CommandoButton.java - A button for a command.
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
package console.commando;

// {{{ imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import console.ConsolePlugin;
// }}}

/**
 * A view for a CommandoCommand
 * 
 * @author ezust
 * 
 */
public class CommandoButton extends JButton implements ActionListener, MouseListener
{

    // {{{ ctor 
	public CommandoButton(CommandoCommand command)
	{
		String name = command.getLabel();
		setText(name);
		this.command = command;
		contextMenu = new JPopupMenu();
		addMouseListener(this);
		visible = jEdit.getBooleanProperty("commando.visible." + name);
		setVisible(visible);
		hide = new JMenuItem(jEdit.getProperty("commando.hide"));
		hide.addActionListener(this);
		customize = new JMenuItem(jEdit.getProperty("commando.customize"));
		customize.addActionListener(this);
		contextMenu.add(hide);
		contextMenu.add(customize);

	}


    // }}}
    
    // {{{ Customize 
	public void customize()
	{
		String userDir = ConsolePlugin.getUserCommandDirectory();
		try
		{
			String name = command.getShortLabel() + ".xml";
			File f = new File(userDir, name);
			if (!f.exists())
			{
				Reader reader = command.openStream();
				FileWriter writer = new FileWriter(f);
				int bytes;
				char[] buf = new char[200];
				while ((bytes = reader.read(buf)) > 0)
				{
					writer.write(buf, 0, bytes);
				}
				writer.close();
				reader.close();
			}
			View v = jEdit.getActiveView();
			jEdit.openFile(v, f.getAbsolutePath());
		}
		catch (IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}
    // }}}
    
    // {{{ actionPerformed 
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == hide)
		{
			visible = false;
			jEdit.setBooleanProperty("commando.visible." + getText(), visible);
			setVisible(visible);
		}
		if (e.getSource() == customize)
		{
			customize();
		}
	}
    // }}}

    // {{{ mouseClicked()
	public void mouseClicked(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			contextMenu.setVisible(false);
		}

	}
    // }}}
    
    // {{{ mousePressed()
	public void mousePressed(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
			contextMenu.setVisible(true);
		}
	}
    // }}}
    
    // {{{ mouseReleased()
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
			contextMenu.setVisible(true);
		}
	}
    // }}}

    // {{{ mouseEntered ()
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

    // }}}
    
    // {{{ mouseExited()
    public void mouseExited(MouseEvent e)
	{

	}
    // }}}
    
    // {{{ Private data members
	private boolean visible;

	private CommandoCommand command;

	private JPopupMenu contextMenu;

	private JMenuItem hide;

	private JMenuItem customize;
	// }}}

}
