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
import org.gjt.sp.jedit.gui.*;
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
		addComponent(new JLabel(jEdit.getProperty("options.console.errors")));

		JPanel errors = new JPanel(new BorderLayout());
		errorListModel = new DefaultListModel();
		errors.add(BorderLayout.CENTER,new JScrollPane(errorList = new JList()));
		errorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		errorList.addMouseListener(new MouseHandler());
		ErrorMatcher[] matchers = ConsoleShellPluginPart.loadMatchers();
		for(int i = 0; i < matchers.length; i++)
		{
			errorListModel.addElement(matchers[i]);
		}
		errorList.setModel(errorListModel);

		JPanel buttons = new JPanel();
		buttons.add(edit = new JButton(jEdit.getProperty("options.console.errors.edit")));
		edit.addActionListener(new ActionHandler());
		buttons.add(add = new JButton(jEdit.getProperty("options.console.errors.add")));
		add.addActionListener(new ActionHandler());
		buttons.add(del = new JButton(jEdit.getProperty("options.console.errors.del")));
		del.addActionListener(new ActionHandler());
		buttons.add(up = new JButton(jEdit.getProperty("options.console.errors.up")));
		up.addActionListener(new ActionHandler());
		buttons.add(down = new JButton(jEdit.getProperty("options.console.errors.down")));
		down.addActionListener(new ActionHandler());
		errors.add(BorderLayout.SOUTH,buttons);

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = y++;
		cons.gridheight = cons.REMAINDER;
		cons.gridwidth = cons.REMAINDER;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = 1.0f;

		gridBag.setConstraints(errors,cons);
		add(errors);
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
	private JList errorList;
	private DefaultListModel errorListModel;
	private JButton edit;
	private JButton add;
	private JButton del;
	private JButton up;
	private JButton down;

	private JButton createColorButton(String property)
	{
		JButton b = new JButton(" ");
		b.setBackground(GUIUtilities.parseColor(jEdit.getProperty(property)));
		b.addActionListener(new ActionHandler());
		b.setRequestFocusEnabled(false);
		return b;
	}

	private void edit()
	{
		ErrorMatcher error = (ErrorMatcher)errorList.getSelectedValue();
		if(error == null)
		{
			getToolkit().beep();
			return;
		}
		new ErrorMatcherDialog(this,error);
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == edit)
			{
				edit();
			}
			else
			{
				JButton button = (JButton)source;
				Color c = JColorChooser.showDialog(ConsoleOptionPane.this,
					jEdit.getProperty("colorChooser.title"),
					button.getBackground());
				if(c != null)
					button.setBackground(c);
			}
		}
	}

	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() == 2)
			{
				edit();
			}
		}
	}
}

class ErrorMatcherDialog extends EnhancedDialog
{
	public ErrorMatcherDialog(Component comp, ErrorMatcher matcher)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("options.console.errors.title"),true);
		this.matcher = matcher;

		JPanel panel = new JPanel();
		getContentPane().add(BorderLayout.CENTER,panel);

		panel = new JPanel();
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(new ActionHandler());
		getRootPane().setDefaultButton(ok);
		panel.add(ok);
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new ActionHandler());
		panel.add(cancel);
		getContentPane().add(BorderLayout.SOUTH,panel);

		Dimension screen = getToolkit().getScreenSize();
		pack();
		setLocation((screen.width - getSize().width) / 2,
			(screen.height - getSize().height) / 2);
		show();
	}

	public void ok()
	{
		isOK = true;
		dispose();
	}

	public void cancel()
	{
		dispose();
	}

	public ErrorMatcher getMatcher()
	{
		return (isOK ? matcher : null);
	}

	// private members
	private ErrorMatcher matcher;
	private JButton ok;
	private JButton cancel;
	private boolean isOK;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == ok)
				ok();
			else
				cancel();
		}
	}
}
