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

import cswilly.spell.Validator;
import cswilly.spell.Result;

import java.util.Vector;

import voxspellcheck.SuggestionTree;


public class VoxSpellSuggestionValidator implements Validator{
	private SuggestionTree suggestions;
	
	public VoxSpellSuggestionValidator(SuggestionTree suggestions){
		if(suggestions == null)throw new IllegalArgumentException("suggestions shouldn't be null");
		this.suggestions = suggestions;
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
  public boolean validate(int lineNum, String line, Result r ){
	  
	  Vector<String> suggs = suggestions.getSuggestions(r.getOriginalWord());

	  if(!suggs.isEmpty()){
		  r.setType(Result.SUGGESTION);

		  if(r.getSuggestions()!=null)r.getSuggestions().addAll(suggs);
		  else r.setSuggestions(suggs);
	  }
	  return true;
  }


  public void done(){}
  public void start(){}
  
}
