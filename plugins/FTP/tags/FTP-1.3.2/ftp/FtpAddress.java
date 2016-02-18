/*
 * FtpAddress.java - FTP URI parser class
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2008 Vadim Voituk
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
 * @author Vadim Voituk
 */
public class FtpAddress {

	private String scheme;
	private String host;
	private int port;
	private String path;
	private String user;
	private String password;

	/**
	 * FtpAddress constructor
	 * @param url
	 * @throws IllegalArgumentException - on invalid FTP address format
	 */
	public FtpAddress(String url) {

		if (url.startsWith(FtpVFS.FTP_PROTOCOL + "://"))
			this.scheme = FtpVFS.FTP_PROTOCOL;
		else if (url.startsWith(FtpVFS.SFTP_PROTOCOL + "://"))
			this.scheme = FtpVFS.SFTP_PROTOCOL;
		else
			throw new IllegalArgumentException("Unsupported URI scheme");

		// Parse path
		String domainPart;
		int pos = url.indexOf('/', this.scheme.length()+3);
		if (pos == -1) {
			this.setPath("");
			domainPart = url.substring(this.scheme.length()+3, url.length());
		} else {
			this.setPath( url.substring(pos) );
			domainPart = url.substring(this.scheme.length()+3, pos);
		}


		// Parse auth+domain part
		pos = domainPart.lastIndexOf('@');
		String authPart;
		if (pos == -1)
			authPart = null;
		else {
			authPart = domainPart.substring(0, pos);
			domainPart = domainPart.substring(pos+1);

			// parse auth part
			pos = authPart.indexOf(':');
			if (pos == -1) {
				this.user     = authPart;
				this.password = null;
			} else {
				this.user = authPart.substring(0, pos);
				this.password = authPart.substring(pos+1);
			}
		}

		// parse domain part
		pos = domainPart.lastIndexOf(':');
		if (pos == -1) {
			this.host = domainPart;
			this.port = this.getDefaultPort();
		} else {
			this.host = domainPart.substring(0, pos);
			try {
				this.port = Integer.parseInt(domainPart.substring(pos+1));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid connection port: '"+domainPart.substring(pos+1)+"'", e);
			}
		}

		this.host = this.host.replace(" ", "");
	}

	/**
	 * FtpAddress constructor
	 */
	public FtpAddress(boolean secure, String host, String user, String path) {
		this.host = host.replace(" ", "");
		this.user = user;
		this.setPath(path);
	}

	/**
	 * toString() method
	 */
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(this.scheme);
		buf.append("://");
		if(user != null && !user.isEmpty())
		{
			buf.append(this.user);
			buf.append('@');
		}
		buf.append(this.host);
		buf.append(":");
		buf.append(this.port);
		buf.append(this.path);

		return buf.toString();
	}

	public String getScheme() {
		return scheme;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	private int getDefaultPort() {
		return this.scheme == FtpVFS.SFTP_PROTOCOL ? 22 : 21;
	}

	public boolean isSecure() {
		return this.scheme == FtpVFS.SFTP_PROTOCOL;
	}

	/**
	 * Never returns null.
	 */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path==null ? "" : path.trim();
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
