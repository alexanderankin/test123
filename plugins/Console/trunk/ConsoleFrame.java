/*
 * ConsoleFrame.java - The console window
 * Copyright (C) 2000 Slava Pestov
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

import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.msg.CreateShell;
import org.gjt.sp.jedit.*;

public class ConsoleFrame extends JFrame
{
	// can't be bothered writing accessors
	public Color bgColor, plainColor, infoColor, warningColor, errorColor;
	public Font defaultFont;

	public ConsoleFrame(View view)
	{
		super(jEdit.getProperty("console.title"));

		this.view = view;

		propertiesChanged();

		tabs = new JTabbedPane();
		Object[] shells = EditBus.getNamedList(Shell.SHELLS_LIST);
		for(int i = 0; i < shells.length; i++)
		{
			String shell = (String)shells[i];
			CreateShell msg = new CreateShell(null,shell);
			EditBus.send(msg);
			tabs.addTab(shell,new ConsoleShellPane(this,
				shell,msg.getShell()));
		}

		tabs.addChangeListener(new ChangeHandler());
		selectShell(jEdit.getProperty("console.shell"));

		getContentPane().add(BorderLayout.CENTER,tabs);

		setIconImage(GUIUtilities.getPluginIcon());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowHandler());

		getRootPane().setPreferredSize(new Dimension(640,300));

		pack();
		GUIUtilities.loadGeometry(this,"console");
		show();
	}

	public void selectShell(String shell)
	{
		for(int i = 0; i < tabs.getTabCount(); i++)
		{
			if(tabs.getTitleAt(i).equals(shell))
			{
				tabs.setSelectedIndex(i);
				break;
			}
		}
	}

	public void run(String shell, String cmd)
	{
		if(shell != null)
			selectShell(shell);

		Component comp = tabs.getSelectedComponent();
		if(!(comp instanceof ConsoleShellPane))
		{
			selectShell(jEdit.getProperty("console.shell"));
			comp = tabs.getSelectedComponent();
		}

		((ConsoleShellPane)comp).run(cmd);
	}

	public void close()
	{
		GUIUtilities.saveGeometry(this,"console");

		// if we have a view and we're closed, we can be bought
		// back up again with Plugins->Console. But if we don't
		// have a view, it is impossible.
		if(view == null)
			dispose();
		else
			setVisible(false);
	}

	// called by ConsoleFramePluginPart when associated view is closed
	public void viewClosed()
	{
		view = null;
	}

	// called by ConsoleShellPanes to get the current view
	public View getView()
	{
		if(view == null)
			return jEdit.getFirstView();
		else
			return view;
	}

	public void propertiesChanged()
	{
		bgColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.bgColor"));
		plainColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.plainColor"));
		infoColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.infoColor"));
		warningColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.warningColor"));
		errorColor = GUIUtilities.parseColor(jEdit.getProperty(
			"console.errorColor"));

		String family = jEdit.getProperty("console.font");
		int size;
		try
		{
			size = Integer.parseInt(jEdit.getProperty(
				"console.fontsize"));
		}
		catch(NumberFormatException nf)
		{
			size = 14;
		}
		int style;
		try
		{
			style = Integer.parseInt(jEdit.getProperty(
				"console.fontstyle"));
		}
		catch(NumberFormatException nf)
		{
			style = Font.PLAIN;
		}
		defaultFont = new Font(family,style,size);

		if(tabs != null)
		{
			Component[] components = tabs.getComponents();
			for(int i = 0; i < components.length; i++)
			{
				((ConsoleOutputPane)components[i])
					.propertiesChanged();
			}
		}
	}

	// private members
	private View view;
	private JTabbedPane tabs;

	class ChangeHandler implements ChangeListener
	{
		public void stateChanged(ChangeEvent evt)
		{
			Component comp = tabs.getSelectedComponent();
			if(comp instanceof ConsoleShellPane)
			{
				((ConsoleShellPane)comp).getCommandField()
					.requestFocus();
			}
		}
	}

	class WindowHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent evt)
		{
			close();
		}
	}
}
