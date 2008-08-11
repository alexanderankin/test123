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

package cswilly.jeditPlugins.spell.voxspellbridge;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import cswilly.spell.Engine;
import cswilly.spell.Result;
import cswilly.spell.SpellException;

import voxspellcheck.OffsetTrie;


public class VoxSpellEngine implements Engine{
	private OffsetTrie checker;
	
	private static final Pattern WORDS_PATTERN = Pattern.compile("\\w+");
	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
	public VoxSpellEngine(OffsetTrie checker){
		if(checker == null)throw new IllegalArgumentException("checker shouldn't be null");
		this.checker = checker;
	}
	
  /**
   * Spell check a list of words
   *<p>
   * Spell checks the list of works in <code>words</code> and returns a list of
   * {@link Result}s. There is one {@link Result} for each word in
   * <code>words</code>.
   *<p>
   * @param words {@link String} with list of works to be spell checked.
   * @return List of {@link Result}
   */
  public List<Result> checkLine( String line )
  throws SpellException{
	  if(checker==null)throw new IllegalStateException("this spell engine is stopped");
	  
	  Matcher m = WORDS_PATTERN.matcher(line);
	  List<Result> l = new ArrayList<Result>();
	  while(m.find()){
		  String word  = m.group();
		  
		  if(NUMBER_PATTERN.matcher(word).matches())continue;
		  
		  int offset = m.start()+1;
		  if(checker.find(word.toLowerCase())){
			  //simply don't do this, as I throw it away later...
			  //l.add(new Result(offset,Result.OK,null,word));
		  }else{
				  l.add(new Result(offset,Result.NONE,null,word));
		  }
	  }
	  return l;
  }

  public void stop(){
  	checker = null;
  }
	
  public boolean isStopped(){
	  return checker==null;
  }
  
  public boolean isContextSensitive(){
	  return false;
  }
}
