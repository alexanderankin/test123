/*
 * LoginDialog.java - FTP login dialog
 * :tabSize=4:indentSize=4:noTabs=false:
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

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.HistoryTextField;
import org.gjt.sp.jedit.gui.VariableGridLayout;
import org.gjt.sp.util.GenericGUIUtilities;

@SuppressWarnings("serial")
public class LoginDialog extends EnhancedDialog
{
	//{{{ LoginDialog constructor
	public LoginDialog(Component comp, boolean _secure, String host, String user, String password)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty(_secure ? "login.title-sftp" : "login.title-ftp"), true);
		this.secure = _secure;

		JPanel content = new JPanel(new VariableGridLayout(VariableGridLayout.FIXED_NUM_COLUMNS, 1, 6, 6));
		content.setBorder(new EmptyBorder(12, 12, 12, 12));
		setContentPane(content);

		content.add(createFieldPanel(secure, host, user, password));

		useProxy = new JCheckBox(
			jEdit.getProperty(secure ? "login.useProxy" : "login.useProxyHttp"),
			jEdit.getBooleanProperty("vfs.ftp.useProxy", false)
		);

		if (!secure)
		{
			passive = new JCheckBox(jEdit.getProperty("login.passive"),
				jEdit.getBooleanProperty("vfs.ftp.passive"));

			passive.addItemListener(e -> useProxy.setEnabled(passive.isSelected()));

			useProxy.setEnabled(passive.isSelected());
			content.add(passive);
		}


		content.add(useProxy);
		storePassword = new JCheckBox(jEdit.getProperty("options.ftp.savePasswords"),
			jEdit.getBooleanProperty("vfs.ftp.storePassword"));
		content.add(storePassword);

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

		JTextField focus;
		if (host == null)
			focus = hostField;
		else if (user == null)
			focus = userField;
		else
			focus = passwordField;
		GenericGUIUtilities.requestFocus(this, focus);

		pack();
		setLocationRelativeTo(comp);
		setModal(true);

	} //}}}

	//{{{ ok() method
	@Override
	public void ok()
	{
		hostField.addCurrentToHistory();
		userField.addCurrentToHistory();
		if (privateKeyField != null)
		{
			privateKeyField.addCurrentToHistory();
		}

		if (passive != null)
			jEdit.setBooleanProperty("vfs.ftp.passive", passive.isSelected());
		if (storePassword != null)
			jEdit.setBooleanProperty("vfs.ftp.storePassword", storePassword.isSelected());
		if (useProxy != null)
			jEdit.setBooleanProperty("vfs.ftp.useProxy", useProxy.isSelected());

		if (hostField.hasFocus() && userField.getText().isEmpty())
			userField.requestFocusInWindow();
		else if (userField.hasFocus() && passwordField.getPassword().length == 0)
			passwordField.requestFocusInWindow();
			// Allow empty passwords (Bug #1802173)
			//else if (privateKeyField == null && passwordField.getPassword().length == 0)
			//	return;
			// else if (passwordField.getPassword().length == 0 && privateKeyField != null && privateKeyField.getText().length() == 0)
			// 	return;
		else
		{
			host = hostField.getText();
			user = userField.getText();
			if (privateKeyField != null && !privateKeyField.getText().isEmpty())
			{
				//try{
				//SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(privateKeyField.getText()));
				//if (file.isPassphraseProtected()) {
				//	Log.log(Log.DEBUG, this, "Key File is password protected.");
				//	PassphraseDialog ppd = new PassphraseDialog(jEdit.getActiveView());
				//	Point p  = this.getLocation();
				//	Dimension s = this.getSize();
				//	Dimension ppds = ppd.getSize();
				//	ppd.setLocation((int)(p.x + s.width/2 - ppds.width/2), (int)(p.y + s.height/2 - ppds.height/2));
				//	ppd.setMessage(jEdit.getProperty("login.privatekeypassword"));
				//	ppd.setVisible(true);
				//	if (ppd.isCancelled())
				//		return;
				//	privateKey = file.toPrivateKey(new String(ppd.getPassphrase()));
				//} else {
				//	privateKey = file.toPrivateKey(null);
				//}
				privateKeyFilename = privateKeyField.getText();

				//} catch (InvalidSshKeyException iske) {
				//	GUIUtilities.error(this,"vfs.sftp.invalid-privatekey",new Object[] {iske.getMessage()});
				//	return;
				//} catch (IOException ioe) {
				//	GUIUtilities.error(this,"vfs.sftp.invalid-privatekey",new Object[] {ioe.getMessage()});
				//	return;
				//}

			}
			if (host.isEmpty())
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

	//{{{ getPrivateKeyFilename() method
	public String getPrivateKeyFilename()
	{
		return privateKeyFilename;
	} //}}}

	//{{{ Private members
	private HistoryTextField hostField;
	private HistoryTextField userField;
	private JPasswordField passwordField;
	private HistoryTextField privateKeyField;
	private JCheckBox passive;
	private final JCheckBox storePassword;
	private final JCheckBox useProxy;
	private String host;
	private String user;
	private String password;
	private String privateKeyFilename;
	private boolean isOK;
	private final boolean secure;

	//{{{ createFieldPanel() method
	private JPanel createFieldPanel(boolean secure, String host, String user, String password)
	{
		JPanel panel = new JPanel(new VariableGridLayout(VariableGridLayout.FIXED_NUM_COLUMNS, 2, 6, 6));
		panel.add(new JLabel(jEdit.getProperty("login.host"), SwingConstants.RIGHT));

		hostField = new HistoryTextField(secure ? "sftp.host" : "ftp.host");
		hostField.setText(host);
		hostField.setColumns(20);
		if (host != null)
			hostField.setEnabled(false);
		hostField.getDocument().addDocumentListener(new FieldCompletionListener());
		hostField.addActionListener(e -> userField.requestFocusInWindow());
		panel.add(hostField);
		panel.add(new JLabel(jEdit.getProperty("login.user"), SwingConstants.RIGHT));

		userField = new HistoryTextField("ftp.user");
		userField.setText(user);
		userField.setColumns(20);
		userField.getDocument().addDocumentListener(new FieldCompletionListener());
		userField.addActionListener(e -> passwordField.requestFocusInWindow());
		panel.add(userField);

		panel.add(new JLabel(jEdit.getProperty("login.password"), SwingConstants.RIGHT));

		passwordField = new JPasswordField(password, 20);
		passwordField.addActionListener(e -> ok());

		panel.add(passwordField);
		if (secure)
		{
			Box privateKeyBox = Box.createHorizontalBox();
			privateKeyField = new HistoryTextField("sftp.privateKey");
			//privateKeyField.setText("");
			privateKeyField.addActionListener(e -> ok());
			panel.add(new JLabel(jEdit.getProperty("login.privateKey"), SwingConstants.RIGHT));
			privateKeyBox.add(privateKeyField);
			JButton privateKeySelect = new JButton("...");
			privateKeySelect.setMargin(new Insets(0, 0, 0, 0));
			privateKeySelect.addActionListener(new PrivateKeySelectActionListener(this));
			privateKeyBox.add(privateKeySelect);
			panel.add(privateKeyBox);
		}
		checkKey();

		return panel;
	} //}}}

	//{{{ checkKey() method
	public void checkKey()
	{
		String host = hostField.getText();
		String user = userField.getText();
		if (!host.contains(":"))
			host = host + ":" + FtpVFS.getDefaultPort(secure);
		String key = secure ? ConnectionManager.getStoredFtpKey(host, user) : null;
		String pass = ConnectionManager.getPassword(host + "." + user);
		if (secure)
		{
			if (key != null)
				privateKeyField.setText(key);
			else
				privateKeyField.setText("");
		}

		if (pass != null)
			passwordField.setText(pass);

	}
	//}}}

	//}}}

	//{{{ class PrivateKeySelectActionListener
	class PrivateKeySelectActionListener implements ActionListener
	{
		private final Component parent;

		protected PrivateKeySelectActionListener(Component c)
		{
			parent = c;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(jEdit.getProperty("login.selectprivatekey"));
			chooser.setFileHidingEnabled(false);
			int returnVal = chooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					privateKeyField.setText(chooser.getSelectedFile().getCanonicalPath());
				}
				catch (java.io.IOException err)
				{
					// Might be nice to pop this up
				}
			}
		}
	} //}}}

	//{{{ class FieldCompletionListener
	class FieldCompletionListener implements DocumentListener
	{
		@Override
		public void changedUpdate(DocumentEvent e)
		{
			checkKey();
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			checkKey();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			checkKey();
		}
	} //}}}
}
