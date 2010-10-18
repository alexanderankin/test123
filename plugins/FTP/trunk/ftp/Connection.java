/**
 * 
 */
package ftp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Timer;

import org.gjt.sp.util.Log;

/**
 * Abstract base class for (s)ftp connections.
 */
public abstract class Connection
{
	static int COUNTER;

	int id;
	ConnectionInfo info;
	String home;
	Timer closeTimer;
	
	private boolean inUse;

	Connection(ConnectionInfo info)
	{
		id = COUNTER++;
		this.info = info;

		closeTimer = new Timer(0,new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ConnectionManager.closeConnection(Connection.this);
			}
		});
	}

	abstract FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException;
	
	abstract FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException;
	
	abstract boolean removeFile(String path) throws IOException;
	
	abstract boolean removeDirectory(String path) throws IOException;
	
	abstract boolean rename(String from, String to) throws IOException;
	
	abstract boolean makeDirectory(String path) throws IOException;
	
	abstract InputStream retrieve(String path) throws IOException;
	
	abstract OutputStream store(String path) throws IOException;
	
	abstract void chmod(String path, int permissions) throws IOException;
	
	abstract boolean checkIfOpen() throws IOException;
	
	abstract String resolveSymlink(String path, String[] name) throws IOException;
	
	abstract void logout() throws IOException;
	
	public void logoutQuietly() {
		try { logout(); } catch (IOException e) { }
	}

	public boolean inUse() {
		return inUse;
	}

	void lock() {
		if(inUse)
			throw new InternalError("Trying to lock connection twice!");
		
		Log.log(Log.DEBUG,ConnectionManager.class,
				Thread.currentThread() + ": Connection " + this + " locked");
		inUse = true;
		closeTimer.stop();
	}

	void unlock()
	{
		if(!inUse) {
			Log.log(Log.ERROR,ConnectionManager.class,
				new Exception(Thread.currentThread() + ": Trying to release connection twice!"));
		}
		else
		{
			Log.log(Log.DEBUG,ConnectionManager.class,
					Thread.currentThread() + ": Connection " + this + " released");
		}

		inUse = false;
		closeTimer.stop();
		closeTimer.setInitialDelay(ConnectionManager.connectionTimeout);
		closeTimer.setRepeats(false);
		closeTimer.start();
	}

	public String toString()
	{
		return id + ":" + info.host;
	}
}