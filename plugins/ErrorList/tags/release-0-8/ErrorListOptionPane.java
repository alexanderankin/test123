/*
 * ErrorListOptionPane.java - Error list options panel
 * Copyright (C) 1999, 2000 Slava Pestov
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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class ErrorListOptionPane extends AbstractOptionPane
{
	public ErrorListOptionPane()
	{
		super("error-list");

		addComponent(showOnError = new JCheckBox(jEdit.getProperty(
			"options.error-list.showOnError")));
		showOnError.getModel().setSelected("on".equals(jEdit.getProperty(
			"error-list.showOnError")));

		addComponent(jEdit.getProperty("options.error-list.warningColor"),
			warningColor = createColorButton("error-list.warningColor"));
		addComponent(jEdit.getProperty("options.error-list.errorColor"),
			errorColor = createColorButton("error-list.errorColor"));
	}

	public void save()
	{
		jEdit.setProperty("error-list.showOnError",showOnError
			.getModel().isSelected() ? "yes" : "on");
		jEdit.setProperty("error-list.warningColor",GUIUtilities
			.getColorHexString(warningColor.getBackground()));
		jEdit.setProperty("error-list.errorColor",GUIUtilities
			.getColorHexString(errorColor.getBackground()));
	}

	// private members
	private JCheckBox showOnError;
	private JButton warningColor;
	private JButton errorColor;

	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionHandler());
		b.setRequestFocusEnabled(false);
		return b;
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			JButton button = (JButton)evt.getSource();
			Color c = JColorChooser.showDialog(ErrorListOptionPane.this,
				jEdit.getProperty("colorChooser.title"),
				button.getBackground());
			if(c != null)
				button.setBackground(c);
		}
	}
}
