/*
 * ConnectionInfo.java
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002-2013 Slava Pestov, Nicholas O'Leary, Vadim Voituk, Alan Ezust
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

/**
 * Broken out of the ConnectionManager class so that it can be reused from SshPlugin.
*/
public class ConnectionInfo
{
	// {{{ members
	public boolean secure;
	public String host;
	public int port;
	public String user;
	public String password;
	public String privateKey;
	// }}}
	
	public ConnectionInfo(boolean secure, String host, int port,String user, String password, String privateKey)
	{
		this.secure = secure;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.privateKey = privateKey;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof ConnectionInfo))
			return false;

		ConnectionInfo c = (ConnectionInfo)o;
		return c.secure == secure
			&& c.host.equals(host)
			&& c.port == port
			&& c.user.equals(user)
			&& ( (c.password==null && password==null) ||
			     (c.password!=null && password!=null &&
			      c.password.equals(password)))
			&& ( (c.privateKey==null && privateKey==null) ||
			     (c.privateKey!=null && privateKey!=null &&
			      c.privateKey.equals(privateKey)));
	}

	public String toString()
	{
		return (secure ? FtpVFS.SFTP_PROTOCOL : FtpVFS.FTP_PROTOCOL) + "://" + host + ":" + port;
	}

	public int hashCode()
	{
		return host.hashCode();
	}
	
	public String getHost() {
		return host;
	}
	
	public String getPassword() {
		return password;
	}
	public int getPort() {
		return port;
	}
	public String getUser() {
		return user;
	}
	
}
