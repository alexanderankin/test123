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


import cswilly.spell.Validator;
import cswilly.spell.Result;


/**
 * A spell-checking validator based upon Hunspell.
 * It makes use of hunspell-java bridge by Flemming Frandsen
 * {@link http://dion.swamp.dk/hunspell.html}
 */ 
public
class HunspellSuggestionValidator implements Validator
{
	private Hunspell.Dictionary dict;
	
	public HunspellSuggestionValidator(Hunspell.Dictionary dict){
		if(dict == null)throw new IllegalArgumentException("Dictionary can't be null.");
		this.dict=dict;
	}
	
  /**
   * Validate a result, adding suggestions if hunspell has got some.
   *<p>
   * @param	lineNum	index of the line in the text/buffer/file whatever
   * @param line String with a line of words that are to be corrected
   * @param result result to validate
   * @return	confirm
   */
  public
  boolean validate( int lineNum, String line, Result result ){

	  assert(dict!=null);//simple assertion, as it will be caught during testing
	  if(Result.OK == result.getType())return true;
	  if(Result.ERROR == result.getType())return false;
	  
	  List<String> suggestions = dict.suggest(result.getOriginalWord());
	  
	  if(!suggestions.isEmpty()){
		  if(Result.NONE==result.getType()){
			  result.setType(Result.SUGGESTION);
			  result.setSuggestions(suggestions);
		  }else{
			  result.getSuggestions().addAll(suggestions);
		  }
	  }
	  return true;
  }

  public void done(){
	  dict = null;
  }
	                           
  public void start(){}
}
