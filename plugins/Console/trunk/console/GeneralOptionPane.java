/*
 * GeneralOptionPane.java - General settings
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

package console;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class GeneralOptionPane extends AbstractOptionPane
{
	public GeneralOptionPane()
	{
		super("console.general");
	}

	// protected members

	protected void _init()
	{
		addComponent(consoleToolBar = new JCheckBox(jEdit.getProperty(
			"options.console.general.toolbar")));
		consoleToolBar.getModel().setSelected(jEdit.getBooleanProperty(
			"console.toolbar.enabled"));

		addComponent(commandoToolBar = new JCheckBox(jEdit.getProperty(
			"options.console.general.commando.toolbar")));
		commandoToolBar.getModel().setSelected(jEdit.getBooleanProperty(
			"commando.toolbar.enabled"));

		addComponent(jEdit.getProperty("options.console.general.bgColor"),
			bgColor = createColorButton("console.bgColor"));
		addComponent(jEdit.getProperty("options.console.general.plainColor"),
			plainColor = createColorButton("console.plainColor"));
		addComponent(jEdit.getProperty("options.console.general.infoColor"),
			infoColor = createColorButton("console.infoColor"));
		addComponent(jEdit.getProperty("options.console.general.warningColor"),
			warningColor = createColorButton("console.warningColor"));
		addComponent(jEdit.getProperty("options.console.general.errorColor"),
			errorColor = createColorButton("console.errorColor"));
	}

	protected void _save()
	{
		jEdit.setBooleanProperty("console.toolbar.enabled",
			consoleToolBar.getModel().isSelected());
		jEdit.setBooleanProperty("commando.toolbar.enabled",
			commandoToolBar.getModel().isSelected());
		jEdit.setProperty("console.bgColor",GUIUtilities
			.getColorHexString(bgColor.getBackground()));
		jEdit.setProperty("console.plainColor",GUIUtilities
			.getColorHexString(plainColor.getBackground()));
		jEdit.setProperty("console.infoColor",GUIUtilities
			.getColorHexString(infoColor.getBackground()));
		jEdit.setProperty("console.warningColor",GUIUtilities
			.getColorHexString(warningColor.getBackground()));
		jEdit.setProperty("console.errorColor",GUIUtilities
			.getColorHexString(errorColor.getBackground()));
	}

	// private members
	private JCheckBox consoleToolBar;
	private JCheckBox commandoToolBar;
	private JButton bgColor;
	private JButton plainColor;
	private JButton infoColor;
	private JButton warningColor;
	private JButton errorColor;

	private JButton createColorButton(String property)
	{
		final JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Color c = JColorChooser.showDialog(
					GeneralOptionPane.this,
					jEdit.getProperty("colorChooser.title"),
					b.getBackground());
				if(c != null)
					b.setBackground(c);
			}
		});

		b.setRequestFocusEnabled(false);
		return b;
	}
}
