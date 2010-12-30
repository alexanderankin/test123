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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.io.VFSFile;

import console.ConsolePlugin;
// }}}

/**
 * A view for a CommandoCommand
 * 
 * @author ezust
 * 
 */
public class CommandoButton extends JButton implements ActionListener 
{
    // {{{ Private data members
	private boolean visible;

	private CommandoCommand command;

	private JPopupMenu contextMenu;

	private JMenuItem hide;

	private JMenuItem customize;
	
	private JMenuItem unCustomize;
	
	// }}}

    // {{{ ctor 
	public CommandoButton(CommandoCommand command)
	{
		this.command = command;
		init();
	}
	
	void init() {
		
		setToolTipText(jEdit.getProperty("console.commando.button.tooltiptext"));
		String name = command.getLabel();
		setText(name);
		contextMenu = new JPopupMenu();
		visible = jEdit.getBooleanProperty("commando.visible." + name);
		setVisible(visible);
		if (!command.isUser()) {
			hide = new JMenuItem(jEdit.getProperty("commando.hide"));
			hide.addActionListener(this);
			contextMenu.add(hide);
		}
		customize = new JMenuItem(jEdit.getProperty("commando.customize"));
		customize.addActionListener(this);
		contextMenu.add(customize);
		if (command.isUser()) {
			setBackground(Color.GREEN);
			setToolTipText(jEdit.getProperty("console.commando.button.customized.tooltiptext"));
			if (command.isOverriding()) {
				unCustomize = new JMenuItem(jEdit.getProperty("commando.uncustomize"));
				unCustomize.setToolTipText(jEdit.getProperty("commando.uncustomize.tooltiptext", new Object[] {name}));
				unCustomize.addActionListener(this);
				contextMenu.add(unCustomize);
			}
		}
		setComponentPopupMenu(contextMenu);
		
	}
    // }}}
    
	// {{{ unCustomize
	public void unCustomizeButton() 
	{
		String userDir = ConsolePlugin.getUserCommandDirectory();
		String name = command.getShortLabel() + ".xml";
		File f = new File(userDir, name);
		String path = f.getPath();
		VFSFile vf = new VFSFile(path, path, path, VFSFile.FILE, 0, false);
		VFSFile[] vfl = new VFSFile[1];
		vfl[0]=vf;
		DockableWindowManager dwm = jEdit.getActiveView().getDockableWindowManager();
		Component comp = dwm.getDockable("vfs.browser");
		VFSBrowser browser = (VFSBrowser) comp;
		if (browser == null) {
			dwm.showDockableWindow("vfs.browser");
			comp = dwm.getDockable("vfs.browser");
			browser = (VFSBrowser) comp;
		}
		if (browser == null) {
			return;
		}
		browser.delete(vfl);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {	ConsolePlugin.rescanCommands();	}});
	}
	
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
				ConsolePlugin.rescanCommands();
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
		if (e.getSource() == unCustomize) {
			unCustomizeButton();
		}
	}
    // }}}

}
