/*
 * SessionFileConverter.java - convert session files from old format to XML
 * Copyright (c) 2001 Dirk Moebius
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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


package sessions;


import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;


public class SessionFileConverter implements Runnable
{

	private String sessionsDir;


	SessionFileConverter()
	{
		sessionsDir = SessionManager.getSessionsDir();
	}


	public void run()
	{
		String[] files = getOldSessionFiles();
		if(files.length == 0)
			return;

		for(int i = 0; i < files.length; ++i)
			convert(files[i]);
	}


	private String[] getOldSessionFiles()
	{
		return new File(sessionsDir).list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".session");
			}
		});
	}


	private void convert(String filename)
	{
		Log.log(Log.DEBUG, this, "converting to new session file format: " + filename);

		String absoluteFilename = MiscUtilities.constructPath(sessionsDir, filename);
		String name = filename.substring(0, filename.length() - 8);
		Session session = new Session(name);
		String line;

		try
		{
			File absoluteFile = new File(absoluteFilename);
			BufferedReader in = new BufferedReader(new FileReader(absoluteFile));
			while ((line = in.readLine()) != null)
				readOldSessionLine(line, session);
			in.close();
			session.saveXML();
			absoluteFile.delete();
		}
		catch (IOException io)
		{
			Log.log(Log.ERROR, this, "IOException during session convert: " + io);
		}
	}


	/**
	 * Parse one line from an old-format session file.
	 * @param line  the line.
	 * @param session  where to add the line info to.
	 */
	private void readOldSessionLine(String line, Session session)
	{
		// handle path:XXX for backwards compatibility
		// with jEdit 2.2 sessions
		if (line.startsWith("path:"))
			line = line.substring(5);

		boolean isCurrent = false;
		StringTokenizer st = new StringTokenizer(line, "\t");
		String path = st.nextToken();

		// ignore all tokens except for 'current' to maintain
		// compatibility with jEdit 2.2 sessions
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (token.equals("current"))
				isCurrent = true;
		}

		if (path != null)
		{
			session.addFile(path);
			if(isCurrent)
				session.setCurrentFile(path);
		}
	}


}

