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

/**
 * A validator of a spell check results
 *<p>
 * After a spell check engine runs, its results must be validated (normally by
 * a user). The {@link Validator} class provides this service.
 */ 
public
interface Validator
{
  /**
   * Validate a line of words that have the <code>results</code> of a spell
   * check.
   *<p>
   * @param	lineNum	index of the line in the text/buffer/file whatever
   * @param line String with a line of words that are to be corrected
   * @param result result to validate
   * @return	confirm
   */
  public
  boolean validate( int lineNum, String line, Result result );
  
  /**
   * Call this upon new spell-checking
   */
  public void start();
  /**
   * Call this at the end of spell-checking
   * @return	a list of pending replacements
   */
  public void done();
  
}
