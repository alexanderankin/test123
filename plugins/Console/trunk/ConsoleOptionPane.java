/*
 * ConsoleOptionPane.java - Console options panel
 * Copyright (C) 1999 Slava Pestov
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
import org.gjt.sp.jedit.gui.FontComboBox;
import org.gjt.sp.jedit.*;

public class ConsoleOptionPane extends AbstractOptionPane
{
	public ConsoleOptionPane()
	{
		super("console");

		addComponent(toolBarEnabled = new JCheckBox(jEdit.getProperty(
			"options.console.toolbar")));
		toolBarEnabled.getModel().setSelected("on".equals(jEdit.getProperty(
			"console.toolbar.enabled")));

		add(shells = new JComboBox(EditBus.getNamedList(
			Shell.SHELLS_LIST)));
		shells.setSelectedItem(jEdit.getProperty("console.shell"));

		addComponent(jEdit.getProperty("options.console.shell"),
			shells);

		/* Font */
		font = new FontComboBox();
		font.setSelectedItem(jEdit.getProperty("console.font"));
		addComponent(jEdit.getProperty("options.console.font"),font);

		/* Font style */
		String[] styles = { jEdit.getProperty("options.console.plain"),
			jEdit.getProperty("options.console.bold"),
			jEdit.getProperty("options.console.italic"),
			jEdit.getProperty("options.console.boldItalic") };
		style = new JComboBox(styles);
		try
		{
			style.setSelectedIndex(Integer.parseInt(jEdit
				.getProperty("console.fontstyle")));
		}
		catch(NumberFormatException nf)
		{
		}
		addComponent(jEdit.getProperty("options.console.fontstyle"),
			style);

		/* Font size */
		String[] sizes = { "9", "10", "12", "14", "18", "24" };
		size = new JComboBox(sizes);
		size.setEditable(true);
		size.setSelectedItem(jEdit.getProperty("console.fontsize"));
		addComponent(jEdit.getProperty("options.console.fontsize"),size);

		addComponent(jEdit.getProperty("options.console.bgColor"),
			bgColor = createColorButton("console.bgColor"));
		addComponent(jEdit.getProperty("options.console.plainColor"),
			plainColor = createColorButton("console.plainColor"));
		addComponent(jEdit.getProperty("options.console.infoColor"),
			infoColor = createColorButton("console.infoColor"));
		addComponent(jEdit.getProperty("options.console.warningColor"),
			warningColor = createColorButton("console.warningColor"));
		addComponent(jEdit.getProperty("options.console.errorColor"),
			errorColor = createColorButton("console.errorColor"));
	}

	public void save()
	{
		jEdit.setProperty("console.toolbar.enabled",toolBarEnabled
			.getModel().isSelected() ? "on" : "off");
		jEdit.setProperty("console.shell",(String)shells.getSelectedItem());
		jEdit.setProperty("console.font",(String)font.getSelectedItem());
		jEdit.setProperty("console.fontsize",(String)size.getSelectedItem());
		jEdit.setProperty("console.fontstyle",String.valueOf(
			style.getSelectedIndex()));
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
	private JCheckBox toolBarEnabled;
	private JComboBox shells;
	private JComboBox font;
	private JComboBox size;
	private JComboBox style;
	private JButton bgColor;
	private JButton plainColor;
	private JButton infoColor;
	private JButton warningColor;
	private JButton errorColor;

	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionHandler(b));
		b.setRequestFocusEnabled(false);
		return b;
	}

	class ActionHandler implements ActionListener
	{
		ActionHandler(JButton button)
		{
			this.button = button;
		}

		public void actionPerformed(ActionEvent evt)
		{
			JButton button = (JButton)evt.getSource();
			Color c = JColorChooser.showDialog(ConsoleOptionPane.this,
				jEdit.getProperty("colorChooser.title"),
				button.getBackground());
			if(c != null)
				button.setBackground(c);
		}

		private JButton button;
	}
}
