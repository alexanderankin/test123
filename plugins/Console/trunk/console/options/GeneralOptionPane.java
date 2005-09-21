/*
 * GeneralOptionPane.java - General settings
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999, 2004 Slava Pestov
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

package console.options;

//{{{ Imports
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

import console.ProjectTreeListener;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("console.general");
	} //}}}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{

		prefix = new JComboBox();
		prefix.setEditable(true);
		prefix.addItem(jEdit.getProperty("console.shell.prefix"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.bash"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.cmd"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.tcsh"));		
		prefix.addItem(jEdit.getProperty("console.shell.prefix.command"));
		JLabel prefixLabel = new JLabel(jEdit.getProperty("options.console.general.shellprefix"));
		String toolTip = jEdit.getProperty("options.console.general.shellprefix.tooltip");
		prefixLabel.setToolTipText(toolTip);
		prefix.setToolTipText(toolTip);
		addComponent(prefixLabel, prefix);

		font = new FontSelector(jEdit.getFontProperty("console.font"));
		addComponent(jEdit.getProperty("options.console.general.font"), font);

		String[] encodings = MiscUtilities.getEncodings();
		Arrays.sort(encodings,new MiscUtilities.StringICaseCompare());
		encoding = new JComboBox(encodings);
		encoding.setEditable(true);
		encoding.setSelectedItem(jEdit.getProperty("console.encoding"));
		addComponent(jEdit.getProperty("options.console.general.encoding"),
			encoding);

		addComponent(jEdit.getProperty("options.console.general.bgColor"),
			bgColor = createColorButton("console.bgColor"));
		addComponent(jEdit.getProperty("options.console.general.plainColor"),
			plainColor = createColorButton("console.plainColor"));
		addComponent(jEdit.getProperty("options.console.general.caretColor"),
			caretColor = createColorButton("console.caretColor"));
		addComponent(jEdit.getProperty("options.console.general.infoColor"),
			infoColor = createColorButton("console.infoColor"));
		addComponent(jEdit.getProperty("options.console.general.warningColor"),
			warningColor = createColorButton("console.warningColor"));
		addComponent(jEdit.getProperty("options.console.general.errorColor"),
			errorColor = createColorButton("console.errorColor"));

		
		addComponent(new JSeparator(SwingConstants.HORIZONTAL));
		addComponent(new JLabel(jEdit.getProperty("options.console.general.changedir")));
		
		pvchange = new JCheckBox(jEdit.getProperty("options.console.general.changedir.pvchange"));
		pvselect = new JCheckBox(jEdit.getProperty("options.console.general.changedir.pvselect"));
		
		pvchange.setSelected(jEdit.getBooleanProperty("console.changedir.pvchange"));
		pvselect.setSelected(jEdit.getBooleanProperty("console.changedir.pvselect"));		


		
		addComponent(pvchange);
		addComponent(pvselect);

	} //}}}

	//{{{ _save() method
	protected void _save()
	{

		jEdit.setBooleanProperty("console.changedir.pvchange", pvchange.isSelected());
		jEdit.setBooleanProperty("console.changedir.pvselect", pvselect.isSelected());
		
		jEdit.setFontProperty("console.font",font.getFont());

 		jEdit.setProperty("console.encoding", 
 			(String)encoding.getSelectedItem());

		jEdit.setProperty("console.shell.prefix", prefix.getSelectedItem().toString());
		jEdit.setColorProperty("console.bgColor",
			bgColor.getBackground());
		jEdit.setColorProperty("console.plainColor",
			plainColor.getBackground());
		jEdit.setColorProperty("console.caretColor",
			caretColor.getBackground());
		jEdit.setColorProperty("console.infoColor",
			infoColor.getBackground());
		jEdit.setColorProperty("console.warningColor",
			warningColor.getBackground());
		jEdit.setColorProperty("console.errorColor",
			errorColor.getBackground());
		try {
			ProjectTreeListener.reset();
		}
		catch (Exception e) {}
	}

	//}}}

	//{{{ Private members

	//{{{ Instance variables
	private JCheckBox commandoToolBar;
	private JComboBox prefix;
	private FontSelector font;
	private JComboBox encoding;
	private JButton bgColor;
	private JButton plainColor;
	private JButton caretColor;
	private JButton infoColor;
	private JButton warningColor;
	private JButton errorColor;
	private JCheckBox pvselect;
	private JCheckBox pvchange;
	
	//}}}

	//{{{ createColorButton() method
	private JButton createColorButton(String property)
	{
		final JButton b = new JButton(" ");
		b.setBackground(jEdit.getColorProperty(property));
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
	} //}}}

	//}}}
}
