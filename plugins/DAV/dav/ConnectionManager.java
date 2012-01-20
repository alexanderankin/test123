/**
 * ConnectionManager.java - Manages persistent connections
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 * @author James Glaubiger
 * @version $$
*/

/*
 * TODO:
 *  Code cleanup
 *	- Is forgetPasswords method a useful function for our plugin?
 *	- Can we use HttpURL (or URL) instead of DavAddress?
 */
package dav;

//{{{ imports
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.Component;
import java.io.IOException;
import java.util.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.util.Log;
import org.apache.commons.httpclient.*;
import org.apache.util.HttpURL;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;
import javax.swing.*;
import java.net.MalformedURLException;
import org.gjt.sp.util.ThreadUtilities;
import org.gjt.sp.jedit.jEdit;
//}}}

public class ConnectionManager
{
	//{{{ members 
	private static ConnectionInfo connection = null;
	private static String path = "";
	private static WebdavResources resources = new WebdavResources();
	//}}}

	//{{{ existsResource () method
	public static boolean existsResource( String name ){
		if ( resources.isThereResourceName( name ) ){
			Log.log(Log.DEBUG, ConnectionManager.class, "Resource associated with " + name + " found");
			return true;
		}
		Log.log(Log.DEBUG, ConnectionManager.class, "Resource associated with " + name + " not found");
		return false;
	} //}}}
	
	//{{{ getResource() method
	public static WebdavResource getResource( String name ){
		// goddamn .marks files
		if ( name.endsWith(".marks")) {
			return null;
		}
		// try {
		if (existsResource( name )) {
			WebdavResource resource = resources.getResource( name );
			//resource.close();
			return resource;
		} // } catch(IOException io) { }
		HttpURL url = null;
		WebdavResource resource;
		try {

			url = new HttpURL(name);
			url.setUserInfo(connection.user, connection.password);
			boolean httpEnabled = jEdit.getBooleanProperty("firewall.enabled");
            if (httpEnabled) {
                   Log.log(Log.DEBUG, ConnectionManager.class, "HTTP proxy enabled");
                   int firewallPort = Integer.parseInt(jEdit.getProperty("firewall.port"));
                   resource = new WebdavResource(
                       url, jEdit.getProperty("firewall.host"), firewallPort, new UsernamePasswordCredentials(
                           jEdit.getProperty("firewall.user"),
                           jEdit.getProperty("firewall.password")));
            } else {
                   Log.log(Log.DEBUG, ConnectionManager.class, "HTTP proxy disabled");
                   resource = new WebdavResource(url);
            }
			resource.setUserInfo(connection.user, connection.password);
			Log.log(Log.DEBUG, ConnectionManager.class, "URL for " + name + ": " + url.getEscapedHttpURLExceptForUserInfo() );
			resources.addResource( name, resource );
			return resource;
		} catch (HttpException e) {
			if (e.getReasonCode() == HttpStatus.SC_UNAUTHORIZED)
				JOptionPane.showMessageDialog(null, "Unable to connect1 to server, please make sure that the host, " +
					"username and password are correct.\n" +
					"If problem persists, contact Server Administrator for further assistance." +
					"Name was: " + name +
					" connection user was: " + connection.user + " and password was: "+connection.password);
			
			url = null;
			resource = null;
		} catch (IOException e) {
				Log.log(Log.DEBUG,ConnectionManager.class,"Error: Check! "
			+ e.getMessage());
			url = null;
			resource = null;
		} catch (Throwable e) {
		}
		return null;
	} //}}}
	
	//{{{ forgetPasswords() method
	/** Clears the user information from ConnectionInfo
	 * A relic from when we borrowed this code from FTP
	 *
	 * TODO: Is this really necessary?
	 */
	public static void forgetPasswords()
	{
		JOptionPane.showMessageDialog(null, "User information has been cleared");
		connection.setUser( null );
		connection.setPassword( null );
 	} //}}}

 	//{{{ getConnectionInfo() method
	/** Returns the connectionInfo associated with the given DavAddress
	 * 
	 * TODO:
	 *  Someone complained about having to add http:// to the front of the URL,
	 *	is there a way we can work around this?
	 *  Do we really need a DavAddress, since all the information necessary can be
	 *	contained in a regular HttpURL?
	 */
	public static ConnectionInfo getConnectionInfo(Component comp,
		DavAddress address)
	{
	    try {
		String host, user;
		DavAddress temp;

		if( address != null )
		{
			host = address.host;
			user = connection.getUser();

			if ( host.equals(connection.getHost()) &&
			     user != null &&
			     connection.getPassword() != null )
			{
				return connection;
			}
			host = constructPath(connection.getHost(), connection.getPath());
		}
		else
			host = user = null;

		/* since this can be called at startup time,
		 * we need to hide the splash screen. */
		GUIUtilities.hideSplashScreen();

		final LoginDialog dialog = new LoginDialog(comp, host, user, null);
		ThreadUtilities.runInDispatchThreadAndWait(new Runnable() {
			@Override
			public void run() {
				dialog.setVisible(true);
			}
		});
		
		if(!dialog.isOK())
			return null;

		host = dialog.getHost();
		if( address == null && !host.startsWith(DavVFS.PROTOCOL + "://")) {
			JOptionPane.showMessageDialog(null, "Unable to connect2 to server, please make sure that the host, " +
				"username and password are correct.\n" +
				"If problem persists, contact Server Administrator for further assistance.");
			return null;
		}
		address = new DavAddress(host);
		connection = new ConnectionInfo(address.host,address.path,80,
			dialog.getUser(),dialog.getPassword());
		getResource(DavVFS.PROTOCOL + "://" + connection.getHost() + 
                            connection.getPath());
	    } catch (Throwable t) {
	    }
		return connection;
	} //}}}

	//{{{ constructPath() method
	/** Returns a full path starting with parent and ending with path
	 */
	private static String constructPath(String parent, String path)
	{
		DavAddress address;
		
		if(parent == null && path == null)
			return null;
		
		if(path.startsWith("~"))
			path = "/" + path;

		if(path.startsWith("/"))
		{
			address = new DavAddress(parent);
			address.path = address.path + path;
			return address.toString();
		}
		else if(parent.endsWith("/"))
			return parent + path;
		else
			return parent + '/' + path;
	} //}}}

	//{{{ ConnectionInfo class
	/** Returns a full path starting with parent and ending with path
	*/
public static class ConnectionInfo
{
	private String host;
	private String path;
	private int port = 80;
	private String user;
	private String password;
	// private WebdavResource resource;

	//{{{ ConnectionInfo() constructor
	public ConnectionInfo()
	{
	} //}}}

	//{{{ ConnectionInfo() constructor
	/** Sets all the necessary information for our connectionInfo
	 * Includes check for proxy servers ( Thanks to Torsten )
	 */
	public ConnectionInfo(String host, String path, int port, String user,
		String password)
	{
		this.setHost(host);
		if ( !path.endsWith("/") )
			path = path + "/";
		this.path = path;
		this.port = port;
		this.user = user;
		this.password = password;
		// xxxremoving
		// getResource(DavVFS.PROTOCOL + "://" + this.host + this.path + "foo10");
	} //}}}

        //{{{ equals() method
        /** compares objects of type ConnectionInfo
         */
	public boolean equals(Object o)
	{
		if(!(o instanceof ConnectionInfo))
			return false;

		ConnectionInfo c = (ConnectionInfo)o;
		return c.host.equals(host)
			&& c.port == port
			&& c.user.equals(user)
			&& c.password.equals(password);
	} //}}}

	public String getHost()
	{
		return this.host;
	}

	public String getPath()
	{
		return this.path;
	}

	public String getUser()
	{
		return this.user;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	} //}}}
	
	/** Uhh... do we need this? */
	static
	{
		connection = new ConnectionInfo();
	}
}

