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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.FontSelector;

import console.ProjectTreeListener;
import console.gui.Label;
import console.ProcessRunner;
//}}}

public class GeneralOptionPane extends AbstractOptionPane
{
	//{{{ GeneralOptionPane constructor
	public GeneralOptionPane()
	{
		super("console.general");
	} //}}}
	//{{{ _init() method
	protected void _init()
	{

		prefix = new JComboBox();
		prefix.setEditable(true);
		
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

		prefix.addItem(jEdit.getProperty("console.shell.prefix"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.bash"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.cmd"));
		prefix.addItem(jEdit.getProperty("console.shell.prefix.tcsh"));		
		prefix.addItem(jEdit.getProperty("console.shell.prefix.command"));

		Label prefixLabel = new Label("options.console.general.shellprefix");
		addComponent(prefixLabel, prefix);
		
		Label pathLabel = new Label("options.console.general.pathdirs");
		pathDirs = new JTextField(jEdit.getProperty("console.shell.pathdirs"));
		addComponent(pathLabel, pathDirs);
		
		mergeError = new JCheckBox();
		mergeError.setText(jEdit.getProperty("options.console.general.mergeError"));
		mergeError.setToolTipText(jEdit.getProperty("options.console.general.mergeError.tooltip"));
		mergeError.setSelected(jEdit.getBooleanProperty("console.processrunner.mergeError", true));
		addComponent(mergeError);
		
		addComponent(new JSeparator(SwingConstants.HORIZONTAL));
//		addComponent(new JLabel(jEdit.getProperty("options.console.general.changedir")));
		addSeparator("options.console.general.changedir");
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
		
		jEdit.setBooleanProperty("console.processrunner.mergeError", mergeError.isSelected());
		jEdit.setProperty("console.shell.pathdirs", pathDirs.getText());
		ProcessRunner runner = ProcessRunner.getProcessRunner();
		runner.prependUserPath();
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
		ProjectTreeListener.reset();
		
	}
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
	
	// {{{ private members
	
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
	private JCheckBox mergeError;
	private JTextField pathDirs ;
	// }}}
}
