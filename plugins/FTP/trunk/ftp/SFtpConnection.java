/*
 * SFtpConnection.java - A connection to an SSH FTP server
 * Copyright (C) 2002 Slava Pestov
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

import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.search.RESearchMatcher;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;

class SFtpConnection extends ConnectionManager.Connection
{
	SFtpConnection(ConnectionManager.ConnectionInfo info) throws IOException
	{
		super(info);
	}

	FtpVFS.FtpDirectoryEntry[] listDirectory(String path) throws IOException
	{
		return null;
	}

	FtpVFS.FtpDirectoryEntry getDirectoryEntry(String path) throws IOException
	{
		return null;
	}

	boolean delete(String path) throws IOException
	{
		return false;
	}

	boolean removeDirectory(String path) throws IOException
	{
		return false;
	}

	boolean rename(String from, String to) throws IOException
	{
		return false;
	}

	boolean makeDirectory(String path) throws IOException
	{
		return false;
	}

	InputStream retrieve(String path) throws IOException
	{
		return null;
	}

	OutputStream store(String path) throws IOException
	{
		return null;
	}

	void chmod(String path, int permissions) throws IOException
	{
	}

	boolean checkIfOpen() throws IOException
	{
		return false;
	}

	void logout() throws IOException
	{
	}
}
