/*
 * LogViewer.java
 * Copyright (C) 1999, 2000, 2001 Slava Pestov
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

package org.gjt.sp.jedit.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class LogViewer extends JPanel implements DockableWindow
{
	public LogViewer()
	{
		super(new BorderLayout());

		String settingsDirectory = jEdit.getSettingsDirectory();
		if(settingsDirectory != null)
		{
			String[] args = { MiscUtilities.constructPath(
				settingsDirectory, "activity.log") };
			JLabel label = new JLabel(jEdit.getProperty(
				"log-viewer.caption",args));
			add(BorderLayout.NORTH,label);
		}

		JTextArea textArea = new JTextArea(24,80);
		textArea.setDocument(Log.getLogDocument());
		//textArea.setEditable(false);

		add(BorderLayout.CENTER,new JScrollPane(textArea));
	}

	public String getName()
	{
		return "log-viewer";
	}

	public Component getComponent()
	{
		return this;
	}
}
