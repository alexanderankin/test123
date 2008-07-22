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


/**
 * This interface is for getting text lines
 */
public interface SpellSource{
	
	/**
	 * initiate iteration
	 */
	public void start();
	
	/**
	 * retrieve next line
	 * @return	null if no next line
	 */
	public String getNextLine();
	
	/**
	 * retrieve previous line
	 * @return	null if no previous line
	 */
	public String getPreviousLine();
	
	
	/**
	 * retrieve last line number
	 * @return	line number, starting at 0
	 */
	public int getLineNumber();

	/**
	 * end of iteration
	 */
	public void done();
}
