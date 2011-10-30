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

package cswilly.jeditPlugins.spell;

import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.Log;


import cswilly.spell.Result;
import cswilly.spell.Validator;


public class ModeSensitiveValidator implements Validator{
	private JEditBuffer buffer;
	private Token token;
	private int lineNum;
	
	public ModeSensitiveValidator(JEditBuffer buffer){
		if(buffer == null)throw new IllegalArgumentException("buffer can't be null when initializing ModeSensitiveValidator");
		this.buffer = buffer;
	}
	
	public boolean validate(int lineNum, String line, Result result){
		if(this.lineNum != lineNum){
			DefaultTokenHandler handler = new DefaultTokenHandler();
			buffer.markTokens(lineNum,handler);
			token = handler.getTokens();
			this.lineNum = lineNum;
		}
		
		int startOffset = result.getOffset()-1;
		while(token!=null && token.offset<startOffset){
			//Log.log(Log.DEBUG,ModeSensitiveValidator.class,"token="+token);
			token = token.next;
		}
		if(token !=null){
			switch(token.id){
				case Token.COMMENT1 :
				case Token.COMMENT2 :
				case Token.COMMENT3 :
				case Token.COMMENT4 :
				case Token.LITERAL1 :
				case Token.LITERAL2 :
				case Token.LITERAL3 :
				case Token.LITERAL4 :
					break;
				default:
					result.setType(Result.OK);
			}
		}else{
			Log.log(Log.ERROR,ModeSensitiveValidator.class,"Token not found for result :"+result);
			return false;
		}
		return true;
	}
	
	public void start(){}
	public void done(){
		token = null;
	}
}
