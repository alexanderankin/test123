/*
 * SessionsOptionPane.java - plugin options pane for Sessions plugin
 * Copyright (c) 2000,2001 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package sessions;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;


/**
 * This is the option pane that jEdit displays for Session plugin's options.
 */
public class SessionsOptionPane extends AbstractOptionPane implements ActionListener
{

	private JCheckBox bAutoSave;
	private JCheckBox bCloseAll;
	private JCheckBox bShowToolBar;
	private JCheckBox bShowJEditToolBar;
	private JCheckBox bShowTitle;


	public SessionsOptionPane()
	{
		super("sessions");
	}


	public void _init()
	{
		bAutoSave = new JCheckBox(
			jEdit.getProperty("options.sessions.switcher.autoSave"),
			jEdit.getBooleanProperty("sessions.switcher.autoSave", true)
		);

		bCloseAll = new JCheckBox(
			jEdit.getProperty("options.sessions.switcher.closeAll"),
			jEdit.getBooleanProperty("sessions.switcher.closeAll", true)
		);

		bShowToolBar = new JCheckBox(
			jEdit.getProperty("options.sessions.switcher.showToolBar"),
			jEdit.getBooleanProperty("sessions.switcher.showToolBar", false)
		);
		bShowToolBar.addActionListener(this);

		bShowJEditToolBar = new JCheckBox(
			jEdit.getProperty("options.sessions.switcher.showJEditToolBar"),
			jEdit.getBooleanProperty("sessions.switcher.showJEditToolBar", false)
		);

		bShowTitle = new JCheckBox(
			jEdit.getProperty("options.sessions.switcher.showTitle"),
			jEdit.getBooleanProperty("sessions.switcher.showTitle", true)
		);
		bShowTitle.setEnabled(bShowToolBar.isSelected());

		addComponent(bAutoSave);
		addComponent(bCloseAll);
		addComponent(bShowToolBar);
		addComponent("    ", bShowJEditToolBar);
		addComponent("    ", bShowTitle);
	}


	public void _save()
	{
		jEdit.setBooleanProperty("sessions.switcher.autoSave", bAutoSave.isSelected());
		jEdit.setBooleanProperty("sessions.switcher.closeAll", bCloseAll.isSelected());
		jEdit.setBooleanProperty("sessions.switcher.showToolBar", bShowToolBar.isSelected());
		jEdit.setBooleanProperty("sessions.switcher.showJEditToolBar", bShowJEditToolBar.isSelected());
		jEdit.setBooleanProperty("sessions.switcher.showTitle", bShowTitle.isSelected());
	}


	public void actionPerformed(ActionEvent e)
	{
		bShowJEditToolBar.setEnabled(bShowToolBar.isSelected());
		bShowTitle.setEnabled(bShowToolBar.isSelected());
	}

}

