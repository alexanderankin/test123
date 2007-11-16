package console.ssh;

// {{{ imports
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import console.Console;

import ftp.ConnectionInfo;
import ftp.ConnectionManager;
import ftp.PasswordDialog;
// }}}

// {{{ Connection class
/** An ssh remote shell connection 
 */
public class Connection implements UserInfo {
	// {{{ members
	int id;
	ConnectionInfo info;
	String home;
	boolean inUse = false;
	Timer closeTimer;
	private String passphrase = null;
	Channel channel;
	OutputStream ostr;
	StreamThread stout;
	Console console;
	private int keyAttempts = 0;
	// }}}
	
	// {{{ Connection ctor
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
//			channel.setInputStream(System.in);
//			channel.setOutputStream(System.out);
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream(pos);
			channel.setOutputStream(pos);
			stout = new StreamThread(
				console, pis, console.getOutput(), console.getPlainColor());
			stout.start();
			pos = new PipedOutputStream();
			pis = new PipedInputStream(pos);

			channel.setInputStream(pis);
			ostr = pos;
			
			channel.connect(10000);
			
		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, "Can't create Connection - did you browse sftp:// first?", e);
		}
		
		
	}// }}}
	
	// {{{ setConsole() method
    /**
      Not tested yet, but this should make it possible to reuse connections in different Consoles, if the
      need is there */
	void setConsole(Console c) throws IOException {
		if (c != console) {
			stout.abort();
			console = c;
			stout = new StreamThread (
				console, channel.getInputStream(), console.getOutput(), console.getPlainColor());
		}
	}// }}}
	
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
} // }}}
