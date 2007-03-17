/*
* PasswordDialog.java - FTP login dialog
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2007 Nicholas O'Leary
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
package ftp;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class PasswordDialog extends EnhancedDialog implements ActionListener
{
	public PasswordDialog(Component comp,String title, String message)
	{
		super(JOptionPane.getFrameForComponent(comp),
			title,
			true);
		JPanel content = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,1,6,6));
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);
		
		content.add(new JLabel(message));
		password = new JPasswordField();
		content.add(password);
		
				Box buttons = new Box(BoxLayout.X_AXIS);
		buttons.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(this);
		getRootPane().setDefaultButton(ok);
		buttons.add(ok);
		buttons.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(this);
		buttons.add(cancel);
		buttons.add(Box.createGlue());
		
		content.add(buttons);

		GUIUtilities.requestFocus(this,password);
		pack();
		setLocationRelativeTo(comp);
		setModal(true);
		setVisible(true);
	}
	
	private JPasswordField password;
	private boolean isOK = false;
	private JButton ok;
	private JButton cancel;
	
	//{{{ ok() method
	public void ok()
	{
		isOK = true;
		dispose();
	} //}}}
	
	//{{{ cancel() method
	public void cancel()
	{
		dispose();
	} //}}}
	
	//{{{ isOK() method
	public boolean isOK()
	{
		return isOK;
	} //}}}

	//{{{ actionPerformed() method
	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		if(source == ok)
			ok();
		else if(source == cancel)
			cancel();
	} //}}}

	public char[] getPassword()
	{
		return password.getPassword();
	}
}
