/*
 * LoginDialog.java - FTP login dialog
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

		content.add(createFieldPanel(host,user,password));

		if(!secure)
		{
			passive = new JCheckBox(jEdit.getProperty("login.passive"),
				jEdit.getBooleanProperty("vfs.ftp.passive"));
			content.add(passive);

			content.add(GUIUtilities.createMultilineLabel(
				jEdit.getProperty("vfs.ftp.warning")));
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
	}

	// EnhancedDialog implementation
	public void ok()
	{
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
	}

	public void cancel()
	{
		dispose();
	}
	// end EnhancedDialog implementation

	public boolean isOK()
	{
		return isOK;
	}

	public String getHost()
	{
		return host;
	}

	public String getUser()
	{
		return user;
	}

	public String getPassword()
	{
		return password;
	}

	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		if(source == ok)
			ok();
		else if(source == cancel)
			cancel();
	}

	// private members
	private JTextField hostField;
	private JTextField userField;
	private JPasswordField passwordField;
	private JCheckBox passive;
	private String host;
	private String user;
	private String password;
	private boolean isOK;
	private JButton ok;
	private JButton cancel;

	private JPanel createFieldPanel(String host, String user, String password)
	{
		JPanel panel = new JPanel(new VariableGridLayout(
			VariableGridLayout.FIXED_NUM_COLUMNS,2,6,6));

		JLabel label = new JLabel(jEdit.getProperty("login.host"),
			SwingConstants.RIGHT);
		panel.add(label);

		hostField = new JTextField(host,20);
		if(host != null)
			hostField.setEnabled(false);
		panel.add(hostField);

		label = new JLabel(jEdit.getProperty("login.user"),
			SwingConstants.RIGHT);
		panel.add(label);

		userField = new JTextField(user,20);
		panel.add(userField);

		label = new JLabel(jEdit.getProperty("login.password"),
			SwingConstants.RIGHT);
		panel.add(label);

		passwordField = new JPasswordField(password,20);
		panel.add(passwordField);

		return panel;
	}
}
