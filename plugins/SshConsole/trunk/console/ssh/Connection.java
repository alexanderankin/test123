/*          DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE (WTFPL)
                    Version 4, October 2012
	    Based on the wtfpl: http://sam.zoy.org/wtfpl/

 Copyright © 2012 Alan Ezust

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FRAK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FRAK YOU WANT TO.
  1. It is provided "as is" without any warranty whatsoever.
*/



package console.ssh;

// {{{ imports
import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;

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
/** An ssh remote shell connection.
 *
 *  This class is based on the SFtpConnection.java class in the FTP plugin.
 *  @author ezust
 *  @version $Id$
 */
public class Connection implements UserInfo {
	// {{{ members
	int id;
	ConnectionInfo info;
	Session session;
	String home;
	boolean inUse = false;
	Timer closeTimer;
	private String passphrase = null;
	Channel channel;
	OutputStream ostr;
	StreamThread stout;
	Console console;
	private int keyAttempts = 0;

	private int SESSION_TIMEOUT = 60000;
	private int CHANNEL_TIMEOUT = 10000;
	// }}}

	// {{{ Connection ctor
	public Connection(Console console, ConnectionInfo info) {
		try {
			this.console = console;
			this.info = info;
			session=ConnectionManager.client.getSession(info.user, info.host, info.port);
			if (info.privateKey != null) {
				Log.log(Log.DEBUG,this,"Attempting public key authentication");
				Log.log(Log.DEBUG,this,"Using key: "+info.privateKey);
				ConnectionManager.client.addIdentity(info.privateKey);
			}
			keyAttempts = 0;
			session.setUserInfo(this);
			// Timeout hardcoded to 60seconds
			session.connect(SESSION_TIMEOUT);
			channel=session.openChannel("shell");
			ChannelShell channelShell = (ChannelShell) channel;
			channelShell.setAgentForwarding(true);
			channelShell.setXForwarding(jEdit.getBooleanProperty("sshconsole.xforward"));
			channelShell.setPtyType("dumb");
//			channel.setInputStream(System.in);
//			channel.setOutputStream(System.out);
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream(pos);
			channel.setOutputStream(pos);

			stout = new StreamThread(console,
									 pis,
									 console.getOutput(),
									 console.getPlainColor()
			);
			stout.setStatus("ssh " + info.toString());
			console.startAnimation();
			inUse = true;
			if (jEdit.getBooleanProperty("sshconsole.showtasks")) {
				ThreadUtilities.runInBackground(stout);
			}
			else {
				stout.start();
			}

			pos = new PipedOutputStream();
			pis = new PipedInputStream(pos);

			channel.setInputStream(pis);
			ostr = pos;

			channel.connect(CHANNEL_TIMEOUT);

		}
		catch (Exception e) {
			Log.log(Log.ERROR, this, "Can't create Connection - did you browse sftp:// first?", e);
		}


	}// }}}

	// {{{ setConsole() method
	void setConsole(Console c) throws IOException {
		if (c != console) {
			stout.abort();
			console = c;
			stout = new StreamThread(console,
									 channel.getInputStream(),
									 console.getOutput(),
									 console.getPlainColor()
			);
			console.startAnimation();
			inUse = true;
			stout.setStatus("ssh " + info.toString());
			ThreadUtilities.runInBackground(stout);
		}
	}// }}}

	// {{{ inUse() method
	public boolean inUse()
	{
		return inUse;
	} // }}}

	// {{{ checkIfOpen() method
	boolean checkIfOpen() throws IOException
	{
		return channel.isConnected();
	} // }}}

	// {{{ logout() method
	void logout() throws IOException
	{
		if(console.getView() != null)
			console.stopAnimation();
		stout.abort();
		channel.disconnect();
		session.disconnect();
		inUse = false;
	} // }}}

	// {{{ getPassphrase() method
	public String getPassphrase()
	{
		return passphrase;
	} // }}}

	// {{{ getPassword() method
	public String getPassword()
	{
		return info.password;
	} // }}}

	// {{{ promptPassword() method
	public boolean promptPassword(String message)
	{
		return true;
	} // }}}

	// {{{ promptPassphrase() method
	public boolean promptPassphrase(String message)
	{
		Log.log(Log.DEBUG,this,message);
		passphrase = ConnectionManager.getPassphrase(info.privateKey);
		if (passphrase==null || keyAttempts != 0)
		{
			PasswordDialog pd = new PasswordDialog(console.getView(),
				jEdit.getProperty("login.privatekeypassword"), message);
			if (!pd.isOK())
				return false;
			passphrase = new String(pd.getPassword());
			ftp.ConnectionManager.setPassphrase(info.privateKey,passphrase);
		}
		keyAttempts++;
		return true;
	} // }}}

	// {{{ promptYesNo()
	public boolean promptYesNo(final String message)
	{
		final int ret[] = new int[1];
		try
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{

					Object[] options={ "yes", "no" };
					ret[0]=JOptionPane.showOptionDialog(console.getView(),
						message, "Warning", JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				}
			};
			if (EventQueue.isDispatchThread())
			{
				runnable.run();
			}
			else
			{
				EventQueue.invokeAndWait(runnable);
			}
		}
		catch (InterruptedException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (InvocationTargetException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		return ret[0]==0;

	} // }}}

	// {{{ showMessage() method
	public void showMessage(String message)
	{
		JOptionPane.showMessageDialog(console.getView(), message);
	} // }}}

} // }}}
