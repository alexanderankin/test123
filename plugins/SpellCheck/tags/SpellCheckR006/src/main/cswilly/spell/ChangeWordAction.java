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

public class ChangeWordAction extends SpellAction{
	public int line;
	public int offset;
	public String originalWord;
	public String newWord;
	
	public ChangeWordAction(Validator source
			,int line
			, int offset
			, String originalWord
			, String newWord)
	
	{
		super(source);
		this.line = line;
		this.offset = offset;
		this.originalWord = originalWord;
		this.newWord = newWord;
	}
	
	public void undo(){}

}
