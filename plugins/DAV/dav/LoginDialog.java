/**
 * LoginDialog.java - WebDav login dialog
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Taken from the FTP plugin code, copyright (c) 2000 Slava Pestov
 * @author Slava Pestov
 * @author James Glaubiger
 * @version $$
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
package dav;

//{{{ imports
import java.awt.Component;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.HistoryTextField;
//}}}

public class LoginDialog extends EnhancedDialog implements ActionListener
{
	//{{{ LoginDialog constructor
	public LoginDialog(Component comp, String host, String user, String password)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("login.title"),true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,0));
		setContentPane(content);

		JPanel panel = createFieldPanel(host,user,password);
		content.add(panel,BorderLayout.NORTH);

		panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(new EmptyBorder(6,0,0,12));

		content.add(panel,BorderLayout.CENTER);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(6,0,6,12));
		panel.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(this);
		getRootPane().setDefaultButton(ok);
		panel.add(ok);
		panel.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(this);
		panel.add(cancel);
		panel.add(Box.createGlue());

		content.add(panel,BorderLayout.SOUTH);

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
		// show();
		setModal(true);
	} //}}}

	//{{{ ok() method
	public void ok()
	{
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
            jEdit.setProperty("davPlugin.host", host);
            jEdit.setProperty("davPlugin.user", user);
            hostField.addCurrentToHistory();
            userField.addCurrentToHistory();
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
	}  //}}}

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

	//{{{ isOK() method
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

	//{{{ private members
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
	//}}}

	//{{{ createFieldPanel() method
	private JPanel createFieldPanel(String host, String user, String password)
	{
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);

		GridBagConstraints cons = new GridBagConstraints();
		cons.insets = new Insets(0,0,6,12);
		cons.gridwidth = cons.gridheight = 1;
		cons.gridx = cons.gridy = 0;
		cons.fill = GridBagConstraints.BOTH;
		JLabel label = new JLabel(jEdit.getProperty("login.host"),
			SwingConstants.RIGHT);
		layout.setConstraints(label,cons);
		panel.add(label);

		hostField = new HistoryTextField("davPlugin.host");
		if(host != null) {
			hostField.setEnabled(false);
		} else {
		    hostField.setText(jEdit.getProperty("davPlugin.host"));
		}
		cons.gridx = 1;
		cons.weightx = 1.0f;
		layout.setConstraints(hostField,cons);
		panel.add(hostField);

		label = new JLabel(jEdit.getProperty("login.user"),
			SwingConstants.RIGHT);
		cons.gridx = 0;
		cons.weightx = 0.0f;
		cons.gridy = 1;
		layout.setConstraints(label,cons);
		panel.add(label);

		userField = new HistoryTextField("davPlugin.user");
	    userField.setText(jEdit.getProperty("davPlugin.user"));
		cons.gridx = 1;
		cons.weightx = 1.0f;
		layout.setConstraints(userField,cons);
		panel.add(userField);

		label = new JLabel(jEdit.getProperty("login.password"),
			SwingConstants.RIGHT);
		cons.gridx = 0;
		cons.weightx = 0.0f;
		cons.gridy = 2;
		layout.setConstraints(label,cons);
		panel.add(label);

		passwordField = new JPasswordField(password,20);
		cons.gridx = 1;
		cons.weightx = 1.0f;
		layout.setConstraints(passwordField,cons);
		panel.add(passwordField);

		return panel;
	} //}}}
	
}
