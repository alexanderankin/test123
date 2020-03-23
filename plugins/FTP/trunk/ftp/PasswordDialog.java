/*
* PasswordDialog.java - FTP login dialog
* :tabSize=4:indentSize=4:noTabs=false:
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

//{{{ imports
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.VariableGridLayout;
import org.gjt.sp.util.GenericGUIUtilities;
//}}}

@SuppressWarnings("serial")
public class PasswordDialog extends EnhancedDialog
{
	// {{{ PasswordDialog ctor
	public PasswordDialog(JFrame comp, String title, String message)
	{
		super(comp,title,true);
		JPanel content = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,1,6,6));
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);
		
		content.add(new JLabel(message));
		password = new JPasswordField();
		content.add(password);
		
		Box buttons = new Box(BoxLayout.X_AXIS);
		buttons.add(Box.createGlue());
		JButton ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(e -> ok());
		getRootPane().setDefaultButton(ok);
		buttons.add(ok);
		buttons.add(Box.createHorizontalStrut(6));
		JButton cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(e -> cancel());
		buttons.add(cancel);
		buttons.add(Box.createGlue());
		
		content.add(buttons);

		GenericGUIUtilities.requestFocus(this,password);
		pack();
		setLocationRelativeTo(comp);
		setModal(true);
		setVisible(true);
	} // }}}
	
	private final JPasswordField password;
	private boolean isOK;

	//{{{ ok() method
	@Override
	public void ok()
	{
		isOK = true;
		dispose();
	} //}}}
	
	//{{{ cancel() method
	@Override
	public void cancel()
	{
		dispose();
	} //}}}
	
	//{{{ isOK() method
	public boolean isOK()
	{
		return isOK;
	} //}}}

	public char[] getPassword()
	{
		return password.getPassword();
	}
}
