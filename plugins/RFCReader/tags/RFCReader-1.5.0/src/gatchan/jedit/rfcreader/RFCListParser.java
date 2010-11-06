/*
 * RFCListParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Matthieu Casanova
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
package gatchan.jedit.rfcreader;

import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class RFCListParser
{
	public static final String listFile = "rfclist.txt";

	public Map<Integer, RFC> parse()
	{
		Map<Integer, RFC> list = new HashMap<Integer, RFC>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(listFile)));
			String line = reader.readLine();
			while (line != null)
			{
				RFC rfc = parseLine(line);
				if (rfc != null)
					list.put(rfc.getNumber(), rfc);
				line = reader.readLine();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
		return list;
	}

	private RFC parseLine(String line)
	{
		if (line.endsWith("Not Issued."))
			return null;
		try
		{
			RFC rfc = new RFC();
			int rfcNum = Integer.parseInt(line.substring(0,4));
			rfc.setNumber(rfcNum);
			int i = line.indexOf(" (Format:");
			String title;
			if (i == -1)
				title = line.substring(5);
			else
				title = line.substring(5, i);
			rfc.setTitle(title);
			return rfc;
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, this, "unable to parse " + line, e);
		}
		return null;
	}
}
