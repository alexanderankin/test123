/*
 * LoginDialog.java - FTP login dialog
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000 Slava Pestov
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
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class LoginDialog extends EnhancedDialog implements ActionListener
{
	//{{{ LoginDialog constructor
	public LoginDialog(Component comp, boolean secure, String host,
		String user, String password)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty(secure ?
			"login.title-sftp" : "login.title-ftp"),
			true);

		JPanel content = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,1,6,6));
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		content.add(createFieldPanel(secure,host,user,password));

		if(!secure)
		{
			passive = new JCheckBox(jEdit.getProperty("login.passive"),
				jEdit.getBooleanProperty("vfs.ftp.passive"));
			content.add(passive);
		}

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

		JTextField focus;
		if(host == null)
			focus = hostField;
		else if(user == null)
			focus = userField;
		else
			focus = passwordField;
		GUIUtilities.requestFocus(this,focus);

		pack();
		setLocationRelativeTo(comp);
		show();
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		hostField.addCurrentToHistory();
		userField.addCurrentToHistory();

		if(passive != null)
			jEdit.setBooleanProperty("vfs.ftp.passive",passive.isSelected());

		if(hostField.hasFocus() && userField.getText().length() == 0)
			userField.requestFocus();
		else if(userField.hasFocus() && passwordField.getPassword().length == 0)
			passwordField.requestFocus();
		else
		{
			host = hostField.getText();
			user = userField.getText();
			if(host.length() == 0 || user.length() == 0)
			{
				getToolkit().beep();
				return;
			}
			password = new String(passwordField.getPassword());
			isOK = true;
			dispose();
		}
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

	//{{{ getHost() method
	public String getHost()
	{
		return host;
	} //}}}

	//{{{ getUser() method
	public String getUser()
	{
		return user;
	} //}}}

	//{{{ getPassword() method
	public String getPassword()
	{
		return password;
	} //}}}

	//{{{ actionPerformed() method
	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		if(source == ok)
			ok();
		else if(source == cancel)
			cancel();
		else if(source == hostField)
			userField.requestFocus();
		else if(source == userField)
			passwordField.requestFocus();
		else if(source == passwordField)
			ok();
	} //}}}

	//{{{ Private members
	private HistoryTextField hostField;
	private HistoryTextField userField;
	private JPasswordField passwordField;
	private JCheckBox passive;
	private String host;
	private String user;
	private String password;
	private boolean isOK;
	private JButton ok;
	private JButton cancel;

	//{{{ createFieldPanel() method
	private JPanel createFieldPanel(boolean secure, String host, String user,
		String password)
	{
		JPanel panel = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,2,6,6));

		JLabel label = new JLabel(jEdit.getProperty("login.host"),
			SwingConstants.RIGHT);
		panel.add(label);

		hostField = new HistoryTextField(secure ? "sftp.host" : "ftp.host");
		hostField.setText(host);
		hostField.setColumns(20);
		if(host != null)
			hostField.setEnabled(false);
		hostField.addActionListener(this);
		panel.add(hostField);

		label = new JLabel(jEdit.getProperty("login.user"),
			SwingConstants.RIGHT);
		panel.add(label);

		userField = new HistoryTextField("ftp.user");
		userField.setText(user);
		userField.setColumns(20);
		userField.addActionListener(this);
		panel.add(userField);

		label = new JLabel(jEdit.getProperty("login.password"),
			SwingConstants.RIGHT);
		panel.add(label);

		passwordField = new JPasswordField(password,20);
		passwordField.addActionListener(this);
		panel.add(passwordField);

		return panel;
	} //}}}

	//}}}
}
