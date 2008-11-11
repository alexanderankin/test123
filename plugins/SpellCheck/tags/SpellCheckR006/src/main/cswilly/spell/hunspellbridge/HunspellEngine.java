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

package cswilly.spell.hunspellbridge;

import com.stibocatalog.hunspell.Hunspell;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import cswilly.spell.Engine;
import cswilly.spell.Result;
import cswilly.spell.SpellException;


/**
 * A spell-checking engine based upon Hunspell.
 * It makes use of hunspell-java bridge by Flemming Frandsen
 * {@link http://dion.swamp.dk/hunspell.html}
 */ 
public
class HunspellEngine implements Engine
{
	private static final Pattern WORDS_PATTERN = Pattern.compile("\\p{L}+");
	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

	private Hunspell.Dictionary dict;
	
	public HunspellEngine(Hunspell.Dictionary dict){
		if(dict == null)throw new IllegalArgumentException("Dictionary can't be null.");
		this.dict=dict;
	}
	
  /**
   * Spell check a list of words
   *<p>
   * Spell checks the list of works in <code>words</code> and returns a list of
   * {@link Result}s. There is one {@link Result} for each mispelled word in
   * <code>words</code> (no more Result.OK.
   *<p>
   * @param words {@link String} with list of works to be spell checked.
   * @return List of {@link Result}
   */
  public List<Result> checkLine( String line )
  throws SpellException{
	  if(dict==null)throw new SpellException("can't check line : engine is stopped");
	  Matcher m = WORDS_PATTERN.matcher(line);
	  List<Result> l = new ArrayList<Result>();
	  while(m.find()){
		  String word  = m.group();
		  
		  if(NUMBER_PATTERN.matcher(word).matches())continue;
		  
		  int offset = m.start()+1;
		  try{
			  if(dict.misspelled(word)){
				  l.add(new Result(offset,Result.NONE,null,word));
			  }
		  }catch(RuntimeException re){
			  throw new SpellException("error from hunspell",re);
		  }
	  }
	  return l;
  }

  public void stop(){
	  dict = null;
  }
	                           
  public boolean isStopped(){
	  return dict==null;
  }
  
  public boolean isContextSensitive(){
	  return false;
  }
  
}
