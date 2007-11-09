package console.ssh;


import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import console.Console;

import ftp.ConnectionInfo;
import ftp.ConnectionManager;
import ftp.PasswordDialog;
import ftp.SftpLogger;

/* An ssh remote shell connection - may be shared 
 * by multiple Console instances (?)
 */
public class Connection implements UserInfo {
	// {{{ members
	int id;
	ConnectionInfo info;
	String home;
	boolean inUse = true;
	Timer closeTimer;
	private String passphrase = null;
	Channel channel;
	StreamThread stout;
	Console console;
	private int keyAttempts = 0;
	// }}}
	
	
	public Connection(Console console, ConnectionInfo info) {
		try {
			this.console = console;
			this.info = info;
			Session session=ConnectionManager.client.getSession(info.user, info.host, info.port);
			if (info.privateKey != null) {
				Log.log(Log.DEBUG,this,"Attempting public key authentication");
				Log.log(Log.DEBUG,this,"Using key: "+info.privateKey);
				ConnectionManager.client.addIdentity(info.privateKey);
			}
			keyAttempts = 0;
			session.setUserInfo(this);
			
			// Timeout hardcoded to 60seconds
			session.connect(60000);
			channel=session.openChannel("shell");
			((ChannelShell)channel).setAgentForwarding(true);

			
// XXX: hook up input and output streams
//			channel.setInputStream(System.in);
//			channel.setOutputStream(System.out);
			stout = new StreamThread(
				console, channel.getInputStream(), console.getOutput(), console.getPlainColor());

			channel.connect();
			
		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, "Can't create Connection - did you browse sftp:// first?", e);
		}
		
		
	}
	
	void setConsole(Console c) throws IOException {
		if (c != console) {
			stout.abort();
			console = c;
			stout = new StreamThread (
				console, channel.getInputStream(), console.getOutput(), console.getPlainColor());
		}
	}
	public boolean inUse()
	{
		return inUse;
	}

	boolean checkIfOpen() throws IOException
	{
		return channel.isConnected();
	}
	
	
	void logout() throws IOException
	{
		channel.disconnect();
	}

	
	public String getPassphrase()
	{
		return passphrase;
	}
	
	public String getPassword()
	{
		return info.password;
	}
		
	public boolean promptPassword(String message){ return true;}
	public boolean promptPassphrase(String message)
	{
		Log.log(Log.DEBUG,this,message);
		passphrase = ConnectionManager.getPassphrase(info.privateKey);
		if (passphrase==null || keyAttempts != 0)
		{
			PasswordDialog pd = new PasswordDialog(jEdit.getActiveView(),"Enter Passphrase for key",message);
			if (!pd.isOK())
				return false;
			passphrase = new String(pd.getPassword());
			ftp.ConnectionManager.setPassphrase(info.privateKey,passphrase);
		}
		keyAttempts++;
		return true;
	}
	public boolean promptYesNo(String message)
	{
		Object[] options={ "yes", "no" };
		int foo=JOptionPane.showOptionDialog(null, 
			message,
			"Warning",
			JOptionPane.DEFAULT_OPTION, 
			JOptionPane.WARNING_MESSAGE,
			null, options, options[0]);
		return foo==0;
	}
	public void showMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}
}
