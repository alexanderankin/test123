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
/**
 * This class creates an OptionPane for use with jEdit's "Plugins Options"
 * function.
 */
public class TemplatesOptionPane extends AbstractOptionPane implements ActionListener
{
	protected JTextField dirTextField;
	protected boolean pre_2_4_6 = false;

	//Constructors
	public TemplatesOptionPane() {
		super("Templates");
		try {
			Class superclass = Class.forName("org.gjt.sp.jedit.AbstractOptionPane");
			java.lang.reflect.Method dummy = superclass.getDeclaredMethod("_init",null);
		} catch (NoSuchMethodException nsme) {
			pre_2_4_6 = true;	// Running jEdit 2.4pre5 or earlier ...
			this._init();		// ... so we have to initialize the dialog ourselves.
		} catch (ClassNotFoundException cnfe) {}	// It better be there!
	}

	//Accessors & Mutators

	//Implementors
	public void _init() {
		this.init(jEdit.getProperty("plugin.TemplatesPlugin.templateDir.0",""));
	}
	
	public void _save() {
		jEdit.setProperty("plugin.TemplatesPlugin.templateDir.0",
					dirTextField.getText());
		TemplatesPlugin.refreshTemplates();
	}
	
	/**
	 * Initialize the TemplatesOptionPane.
	 * @param textFieldStr A string containing the current Templates directory.
	 */
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
	
	/**
	 * Save the new Templates directory to the global properties, and refresh 
	 * the Templates menu.
	 */
	public void save() {
		if (pre_2_4_6) {
			this._save();	// save directly
		} else {
			super.save();	// super class ensures TemplatesOptionPane was initialised
		}
	}

	/**
	 * Display the file chooser and respond to the user's selection.
	 * @param evt The ActionEvent corresponding to the user's button press.
	 */
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
	 * Revision 1.4  2002/02/22 02:34:36  sjakob
	 * Updated Templates for jEdit 4.0 actions API changes.
	 * Selection of template menu items can now be recorded in macros.
	 *
	 * Revision 1.3  2001/02/23 19:31:39  sjakob
	 * Added "Edit Template" function to Templates menu.
	 * Some Javadoc cleanup.
	 *
	 * Revision 1.2  2000/04/21 05:32:58  sjakob
	 * Modified TemplatesOptionPane for compliance with new OptionPane interface
	 * introducted in jEdit 2.4pre6 (now supports "lazy" instantiation of dialog).
	 * The class is still compatible with earlier versions, though.
	 *
	 * Revision 1.1  1999/12/21 05:00:52  sjakob
	 * Added options pane for "Plugin options" to allow user to select template directory.
	 * Recursively scan templates directory and subdirectories.
	 * Add subdirectories to "Templates" menu as submenus.
	 * Added online documentation, as well as README.txt and CHANGES.txt.
	 *
	 */


