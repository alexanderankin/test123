/*
 * FtpAddress.java - FTP addressing encapsulator
 * Copyright (C) 2000, 2002 Slava Pestov
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

import org.gjt.sp.jedit.jEdit;

public class FtpAddress
{
	public boolean secure;
	public String host;
	public String user;
	public String path;

	public FtpAddress(String url)
	{
		if(url.startsWith(FtpVFS.FTP_PROTOCOL + ":"))
			secure = false;
		else if(url.startsWith(FtpVFS.SFTP_PROTOCOL + ":"))
			secure = true;
		else
			throw new IllegalArgumentException();

		// remove any leading slashes, and ftp: from URL
		int trimAt = url.indexOf(':') + 1;
		for(int i = trimAt; i < url.length(); i++)
		{
			if(url.charAt(i) != '/')
			{
				trimAt = i;
				break;
			}
		}
		url = url.substring(trimAt);

		// get username
		int index = url.lastIndexOf('@');
		if(index != -1)
		{
			user = url.substring(0,index);
			url = url.substring(index + 1);
		}

		// get host name and path
		index = url.indexOf('/');
		if(index == -1)
			index = url.length();

		host = url.substring(0,index);
		path = url.substring(index);
		if(path.length() == 0)
			path = "/";
	}

	public FtpAddress(boolean secure, String host, String user, String path)
	{
		this.secure = secure;
		this.host = host;
		this.user = user;
		this.path = path;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(secure ? FtpVFS.SFTP_PROTOCOL : FtpVFS.FTP_PROTOCOL);
		buf.append("://");
		if(user != null)
		{
			buf.append(user);
			buf.append('@');
		}
		buf.append(host);
		buf.append(path);

		return buf.toString();
	}
}
