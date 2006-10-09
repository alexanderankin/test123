/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Note: The ctags invocation code was taken from the CodeBrowser
plugin by Gerd Knops.
*/

package ctags.sidekick;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;


import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import errorlist.DefaultErrorSource;


public class Parser extends SideKickParser {

	public Parser(String serviceName)
	{
		super(serviceName);
	}

	@Override
	public SideKickParsedData parse(Buffer buffer,
									DefaultErrorSource errorSource)
	{		
		ParsedData data =
			new ParsedData(buffer.getName(),
					buffer.getMode().getName());
		runctags(buffer, errorSource, data);
		return data;
	}

	private void runctags(Buffer buffer, DefaultErrorSource errorSource,
						  ParsedData data)
	{
		String ctagsExe = jEdit.getProperty("options.CtagsSideKick.ctags_path");
		String path = buffer.getPath();
		String [] args;
		if (! path.endsWith("build.xml"))
		{
			args = new String[] {
				ctagsExe,
				"--fields=KsSz",
				"--excmd=pattern",
				"--sort=no",
				"--fields=+n",
				"--extra=-q",
				"-f",
				"-",
				path
			};
		}
		else
		{
			args = new String[] {
				ctagsExe,
				"--fields=KsSz",
				"--excmd=pattern",
				"--sort=no",
				"--language-force=ant",
				"--fields=+n",
				"--extra=-q",
				"-f",
				"-",
				path
			};
		}
		Process p;
		try {
			p = Runtime.getRuntime().exec(args);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String line;
			Tag prevTag = null;
			while ((line=in.readLine()) != null)
			{
				Hashtable<String, String> info =
					new Hashtable<String, String>();
				if (line.endsWith("\n") || line.endsWith("\r"))
					line = line.substring(0, line.length() - 1);
				String fields[] = line.split("\t");
				if (fields.length < 3)
					continue;
				info.put("k_tag", fields[0]);
				info.put("k_pat", fields[2]);
				// extensions
				for (int i = 3; i < fields.length; i++)
				{
					String pair[] = fields[i].split(":", 2);
					if (pair.length != 2)
						continue;
					info.put(pair[0], pair[1]);
				}
				Tag curTag = new Tag(buffer, info);
				if (prevTag != null)
				{
					prevTag.setEnd(new LinePosition(
							buffer, curTag.getLine() - 1, false));
					data.add(prevTag);
				}
				prevTag = curTag;
			}
			if (prevTag != null)
				data.add(prevTag);
			data.done();
		} catch (IOException e) {
			System.err.println(e);
		}	
	}
}
