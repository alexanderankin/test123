// $Id$
/*
 * TemplatesOptionPane.java - A concrete implementation of
 * org.gjt.sp.jedit.AbstractOptionPane which allows modification
 * of user-configurable options for the Templates plugin.
 * Copyright (C) 1999 Steve Jakob
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.gjt.sp.jedit.*;
/** This class creates an OptionPane for use with jEdit's "Plugins Options"
	function. */
public class TemplatesOptionPane extends AbstractOptionPane implements ActionListener
{
	JTextField dirTextField;
	TemplatesAction myAction;

	//Constructors
	public TemplatesOptionPane(TemplatesAction ta) {
		super("Templates");
		myAction = ta;
		init(jEdit.getProperty("plugin.TemplatesPlugin.templateDir.0",""));
	}

	//Accessors & Mutators

	//Implementors
	public void init(String textFieldStr) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5,5,5,5));
		JLabel chooseDirLabel = new JLabel(jEdit.getProperty("options.Templates.choose-dir-msg"));
		add(chooseDirLabel, BorderLayout.NORTH);
		JPanel p = new JPanel();
		BorderLayout bl = new BorderLayout();
		bl.setHgap(5);
		p.setLayout(bl);
		dirTextField = new JTextField(textFieldStr);
		p.add(dirTextField, BorderLayout.CENTER);
		JButton chooseBtn = new JButton(jEdit.getProperty("options.Templates.choose-btn-msg"));
		chooseBtn.addActionListener(this);
		p.add(chooseBtn, BorderLayout.EAST);
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.add(p, BorderLayout.NORTH);
		add(p2, BorderLayout.CENTER);
	}
	
	public void save() {
		jEdit.setProperty("plugin.TemplatesPlugin.templateDir.0",
						dirTextField.getText());
		myAction.refreshTemplates();
	}

	public void actionPerformed(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser(dirTextField.getText());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retVal = chooser.showDialog(this,jEdit.getProperty(
					"options.Templates.choose-btn-msg"));
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			if(file != null)
			{
				try
				{
					String dirName = file.getCanonicalPath();
					dirTextField.setText(dirName);
				}
				catch(IOException e)
				{
					// shouldn't happen
				}
			}
		}
	}
	
}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2000/04/21 05:05:51  sjakob
	 * Initial revision
	 *
	 * Revision 1.1  1999/12/21 05:00:52  sjakob
	 * Added options pane for "Plugin options" to allow user to select template directory.
	 * Recursively scan templates directory and subdirectories.
	 * Add subdirectories to "Templates" menu as submenus.
	 * Added online documentation, as well as README.txt and CHANGES.txt.
	 *
	 */


