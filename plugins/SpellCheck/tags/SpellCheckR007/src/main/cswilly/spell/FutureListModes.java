/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
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

package cswilly.spell;

import org.gjt.sp.util.Log;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Parse the output of `aspell dump modes` to get the list of supported modes
 * On my computer using aspell 0.60.5, it outputs :
 * ccpp           mode for checking C++ comments and string literals
 * comment        mode to check any lines starting with a #
 * email          mode for skipping quoted text in email messages
 * html           mode for checking HTML documents
 * none           mode to disable all filters
 * nroff          mode for checking Nroff documents
 * perl           mode for checking Perl comments and string literals
 * sgml           mode for checking generic SGML/XML documents
 * tex            mode for checking TeX/LaTeX documents
 * texinfo        mode for checking Texinfo documents
 * url            mode to skip URL like constructs (default mode)
 *
 * @author $Author$
 * @version $Revision$
 */
public class FutureListModes extends FutureAspell<Map<String,String>>{
	private static final String[] DEFAULT_MODES = new String[]{"none","url","email","sgml","tex"};
	
	public FutureListModes(String aspellExeFilename){
		super(Arrays.asList(new String[]{aspellExeFilename,"dump","modes"}),new MyProcessor());
	}
	
	
	private static class MyProcessor implements Processor<Map<String,String>>{
		private Map<String,String> modes;
		private Pattern pattern;
		
		MyProcessor(){
			// each line is mode <SOME SPACE> mode description
			// for now, modes are lowercase letters, so is the pattern
			pattern = Pattern.compile("^([a-z]+)\\s+(.*)$");
			modes = new HashMap<String,String>();
		}
		
		public void accumulate(String line) throws SpellException{
			Matcher m = pattern.matcher(line);
			if(!m.matches()){
				// silently fall back to defaults, since windows port of Aspell
				// doesn't support 'dump modes' command
				if("Error: Unknown Action: dump modes".equals(line)){
						Log.log(Log.DEBUG,FutureListModes.class,
							"Aspell doesn't use 'aspell dump modes' => using fallback");
						for(int i=0;i<DEFAULT_MODES.length;i++)
							modes.put(DEFAULT_MODES[i],"");
						return;
				}
				else throw new SpellException("Suspect mode ("+line+")");
			}
			Log.log(Log.DEBUG,FutureListModes.class, "mode:"+line);
			modes.put(m.group(1),m.group(2));
		}
		
		public Map<String,String> done(){
			return modes;
		}
	}
}
