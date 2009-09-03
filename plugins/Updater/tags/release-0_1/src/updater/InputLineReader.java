/*
 * InputLineReader - An InputStreamReader with line reading but no buffering
 *
 * Copyright (C) 2009 Shlomy Reinstein
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

package updater;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputLineReader extends InputStreamReader
{

	public InputLineReader(InputStream in)
	{
		super(in);
	}

	public String readLine()
	{
		StringBuilder sb = new StringBuilder();
		int i;
		try
		{
			while ((i = read()) != -1)
			{
				if (i == '\n')
					break;
				sb.append((char) i);
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return (i == -1) ? null : sb.toString();
	}

}
