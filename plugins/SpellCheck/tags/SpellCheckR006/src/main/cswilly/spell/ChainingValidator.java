/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2001 C. Scott Willy
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A validator implementation which encapsulate a list of validators
 *
 */ 
public class ChainingValidator implements Validator
{
	private Validator mine;
	private Validator next;
	
	public ChainingValidator(Validator mine, Validator next){
		this.mine = mine;
		this.next = next;
	}
  	
	public Validator getMyValidator(){
		return mine;
	}
	
	public Validator getNextValidator(){
		return next;
	}
	
  /**
   * Validate a line of words that have the <code>results</code> of a spell
   * check.
   *<p>
   * @param	lineNum	index of the line in the text/buffer/file whatever
   * @param line String with a line of words that are to be corrected
   * @param results List of {@link Result} of a spell check
   * @return valid (false to cancel)
   */
  public boolean validate( int lineNum, String line, Result result ){
	  boolean valid = true;
	  valid=mine.validate(lineNum, line, result);
	  if(valid && Result.OK != result.getType())valid = next.validate(lineNum, line, result);
	  return valid;
  }
  
  /**
   * Call this upon new spell-checking
   */
   public void start(){
	   mine.start();
	   next.start();
   }

  /**
   * Call this at the end of spell-checking
   */
   public void done(){
	   mine.done();
	   next.done();
   }
}
