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
import java.io.File;
import java.io.IOException;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import com.sshtools.j2ssh.transport.publickey.*;
import com.sshtools.common.authentication.PassphraseDialog;


public class LoginDialog extends EnhancedDialog implements ActionListener
{
	//{{{ LoginDialog constructor
	public LoginDialog(Component comp, boolean _secure, String host,
	String user, String password)
	{
		super(JOptionPane.getFrameForComponent(comp),
		jEdit.getProperty(_secure ?
		"login.title-sftp" : "login.title-ftp"),
		true);
		this.secure = _secure;
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
		if (privateKeyField!=null) {
			privateKeyField.addCurrentToHistory();
		}
		
		if(passive != null)
			jEdit.setBooleanProperty("vfs.ftp.passive",passive.isSelected());
		
		if(hostField.hasFocus() && userField.getText().length() == 0)
			userField.requestFocus();
		else if(userField.hasFocus() && passwordField.getPassword().length == 0)
			passwordField.requestFocus();
		else if (privateKeyField == null && passwordField.getPassword().length == 0)
			return;
		else if (passwordField.getPassword().length == 0 && privateKeyField != null && privateKeyField.getText().length() == 0)
			return;
		else
		{
			host = hostField.getText();
			user = userField.getText();
			if (privateKeyField!=null && privateKeyField.getText().length() > 0) {
				try{
					SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(privateKeyField.getText()));
					if (file.isPassphraseProtected()) {
						Log.log(Log.DEBUG, this, "Key File is password protected.");
						PassphraseDialog ppd = new PassphraseDialog(this);
						ppd.setMessage(jEdit.getProperty("login.privatekeypassword"));
						ppd.show();
						if (ppd.isCancelled())
							return;
						privateKey = file.toPrivateKey(new String(ppd.getPassphrase()));
					} else {
						privateKey = file.toPrivateKey(null);
					}
					privateKeyFilename = privateKeyField.getText();
				} catch (InvalidSshKeyException iske) {
					GUIUtilities.error(this,"vfs.sftp.invalid-privatekey",new Object[] {iske.getMessage()});
					return;
				} catch (IOException ioe) {
					GUIUtilities.error(this,"vfs.sftp.invalid-privatekey",new Object[] {ioe.getMessage()});
					return;
				}
				
			}
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
	
	//{{{ getPrivateKey() method
	public SshPrivateKey getPrivateKey()
	{
		return privateKey;
	} //}}}
	
	//{{{ getPrivateKeyFilename() method
	public String getPrivateKeyFilename()
	{
		return privateKeyFilename;
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
		else if(source == privateKeyField)
			ok();
	} //}}}
	
	//{{{ Private members
	private HistoryTextField hostField;
	private HistoryTextField userField;
	private JPasswordField passwordField;
	private HistoryTextField privateKeyField;
	private JButton privateKeySelect;
	private JCheckBox passive;
	private String host;
	private String user;
	private String password;
	private String privateKeyFilename;
	private SshPrivateKey privateKey;
	private boolean isOK;
	private boolean secure;
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
		if (secure)
			hostField.getDocument().addDocumentListener(new FieldCompletionListener());
		hostField.addActionListener(this);
		panel.add(hostField);
		
		label = new JLabel(jEdit.getProperty("login.user"),
		SwingConstants.RIGHT);
		panel.add(label);
		
		userField = new HistoryTextField("ftp.user");
		userField.setText(user);
		userField.setColumns(20);
		if (secure)
			userField.getDocument().addDocumentListener(new FieldCompletionListener());
		userField.addActionListener(this);
		panel.add(userField);
		
		label = new JLabel(jEdit.getProperty("login.password"),
		SwingConstants.RIGHT);
		panel.add(label);
		
		passwordField = new JPasswordField(password,20);
		passwordField.addActionListener(this);
		
		panel.add(passwordField);
		if (secure)
		{
			Box privateKeyBox = Box.createHorizontalBox();
			privateKeyField = new HistoryTextField("sftp.privateKey");
			privateKeyField.addActionListener(this);
			label = new JLabel(jEdit.getProperty("login.privateKey"),
			SwingConstants.RIGHT);
			panel.add(label);
			privateKeyBox.add(privateKeyField);
			privateKeySelect = new JButton("...");
			privateKeySelect.setMargin(new Insets(0,0,0,0));
			privateKeySelect.addActionListener(new PrivateKeySelectActionListener(this));
			privateKeyBox.add(privateKeySelect);
			panel.add(privateKeyBox);
			checkKey();
		}
		
		return panel;
	} //}}}
	
	//{{{ checkKey() method
	public void checkKey()
	{
		String host = hostField.getText();
		String user = userField.getText();
		if(host.indexOf(":") == -1)
				host = host + ":" + FtpVFS.getDefaultPort(secure);
		privateKeyField.setText(jEdit.getProperty("ftp.keys."+host+"."+user));

	}
	//}}}
	
	//}}}
	
	//{{{ class PrivateKeySelectActionListener
	class PrivateKeySelectActionListener implements ActionListener
	{
		private Component parent;
		public PrivateKeySelectActionListener(Component c) {
			super();
			parent = c;
		}
		
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(jEdit.getProperty("login.selectprivatekey"));
			int returnVal = chooser.showOpenDialog(parent);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				try{
					privateKeyField.setText(chooser.getSelectedFile().getCanonicalPath());
				} catch(java.io.IOException err) {
					// Might be nice to pop this up
				}
			}
		}
	} //}}}

	//{{{ class FieldCompletionListener
	class FieldCompletionListener implements DocumentListener
	{
		public void changedUpdate(DocumentEvent e) { checkKey(); }
		public void insertUpdate(DocumentEvent e) { checkKey(); }
		public void removeUpdate(DocumentEvent e) { checkKey(); }
	} //}}}
}
