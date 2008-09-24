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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Parse the output of `aspell dump dicts` to get the list of supported modes
 * On my computer using aspell 0.60.5, it outputs a long list starting with:
 * en
 * en-variant_0
 * en-variant_1
 * en-variant_2
 * en-w_accents
 * en-wo_accents
 * en_CA
 *
 * @author $Author$
 * @version $Revision$
 */
public class FutureListDicts extends FutureAspell<Vector<Dictionary>>{
	
	public FutureListDicts(String aspellExeFilename){
		super(Arrays.asList(new String[]{aspellExeFilename,"dump","dicts"}),new MyProcessor());
	}
	
	
	private static class MyProcessor implements Processor<Vector<Dictionary>>{
		private Vector<Dictionary> dicts;
		private Pattern pattern;
		
		MyProcessor(){
			// each line is a dictionnary
			//at least 2 letters language code, then anything
			pattern = Pattern.compile("^[a-z]{2}[-\\w]*$");
			dicts = new Vector<Dictionary>();
		}
		
		public void accumulate(String line) throws SpellException{
			if(!pattern.matcher(line).matches())
				throw new SpellException("Suspect dictionnary name ("+line+")");
			Log.log(Log.DEBUG,FutureListDicts.class, "dict:"+line);
			dicts.add(new Dictionary(line,line));
		}
		
		public Vector<Dictionary> done(){
			return dicts;
		}
	}
}
