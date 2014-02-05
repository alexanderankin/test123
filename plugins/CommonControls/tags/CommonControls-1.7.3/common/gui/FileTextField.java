/*
 * FileTextField.java - an input field with a button that opens a file chooser.
 * Copyright (c) 2005 Marcelo Vanzin
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package common.gui;

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;
//}}}

/** A widget that shows a text field with a button that opens up a file chooser.
 *	By default the chooser will only allow files to be selected.
 *
 *  @author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		CC 0.9.4
 */
public class FileTextField extends JPanel
							 implements ActionListener
{

	private JTextField	text;
	private JButton 	chooser;
	private boolean		forceExists;
	private int			selectionMode;

	public FileTextField()
	{
		this(false);
	}

	public FileTextField(boolean forceExists)
	{
		this(null, forceExists);
	}

	/**
	 *	Constructs a new FileTextField.
	 *
	 *	@param	text	Default contents of the text field.
	 *	@param	forceExists	Don't change the text field if the selected
	 *						file cannot be read.
	 */
	public FileTextField(String text, boolean forceExists)
	{
		super(new BorderLayout());
		this.forceExists = forceExists;
		this.selectionMode = JFileChooser.FILES_ONLY;

		this.text = new JTextField(text);
		add(BorderLayout.CENTER, this.text);

		chooser = new JButton(jEdit.getProperty("common.gui.filetextfield.choose"));
		chooser.addActionListener(this);
		add(BorderLayout.EAST, chooser);
	}

	/**
	 *	Returns the text field containing the user input.
	 */
	public JTextField getTextField()
	{
		return text;
	}

	public void setEnabled(boolean e) 
	{
		super.setEnabled(e);
		text.setEnabled(e);
		chooser.setEnabled(e);
	}
	
	/** Sets the tooltip text on the child components. */
	public void setToolTipText(String ttt) {
		super.setToolTipText(ttt);	
		text.setToolTipText(ttt);
		chooser.setToolTipText(ttt);
	}
	/**
	 *	Sets the file selection mode.
	 * @param mode can be 
	   {@link org.gjt.sp.jedit.browser.VFSBrowser#OPEN_DIALOG},
	 * {@link org.gjt.sp.jedit.browser.VFSBrowser#SAVE_DIALOG}, or
	 * {@link org.gjt.sp.jedit.browser.VFSBrowser#CHOOSE_DIRECTORY_DIALOG}.
	 */
	public void setFileSelectionMode(int mode)
	{
		if (mode == JFileChooser.DIRECTORIES_ONLY) 
			this.selectionMode = VFSBrowser.CHOOSE_DIRECTORY_DIALOG;
		else this.selectionMode = mode;
	}

	public void actionPerformed(ActionEvent ae)
	{
		// Used for selected and executable file
		String[] results = GUIUtilities.showVFSFileDialog(null, text.getText(), this.selectionMode, false);
		if (results == null || results.length < 1) return;
		File f = new File(results[0]);
		
		if (forceExists && (!f.exists() || !f.canRead())) {
			Component parent = SwingUtilities.getAncestorOfClass(javax.swing.JDialog.class, this);
			if (parent == null) {
				parent = SwingUtilities.getAncestorOfClass(javax.swing.JFrame.class, this);
			}
			if (parent == null) parent = this;
			JOptionPane.showMessageDialog(parent,
				jEdit.getProperty("common.gui.filetextfield.file_not_found"),
				jEdit.getProperty("common.gui.error"),
				JOptionPane.ERROR_MESSAGE);
		} else {
			getTextField().setText(f.getPath());
		}
	}

}

