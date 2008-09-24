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

import cswilly.spell.Validator;
import cswilly.spell.Result;


import java.util.List;


import errorlist.*;
import org.gjt.sp.util.Log;

/**
* A validator of a spell check results,
*  - it doesn't correct anything
*  - it outputs misspelled words to ErrorList
*/
public
class ErrorListValidator extends DefaultErrorSource implements Validator
{
	private String path;
	private int lineNum;
	public ErrorListValidator(String name){
		super(name);
		path = "";
		lineNum = 0;
	}
	
	/**
	* Validate a line of words that have the <code>results</code> of a spell
	* check.
	*<p>
	* @param line String with a line of words that are to be corrected
	* @param results List of {@link Result} of a spell check
	* @return new List of results, with choices made.
	*/
	public
	boolean validate(int lineNum, String line, Result result ){
		if(result.getType()==Result.OK)return true;
		if(result.getType()==Result.ERROR){
			addError(new DefaultError(this,
				ErrorSource.ERROR,path,lineNum,0,0,
				"There was an error with aspell !"));
			return false;
		}
		int startOffset = result.getOffset()-1;
		int endOffset = startOffset+result.getOriginalWord().length();
		String errorMessage = "Misspelled word: "+result.getOriginalWord();
		if(result.getType()!=Result.NONE){
			List<String> suggestions = result.getSuggestions();
			errorMessage+= " (is it '"+suggestions.get(0);
			for(int i=1;i<suggestions.size()&&i<4;i++){
				errorMessage+="' or '"+suggestions.get(i);
			}
			errorMessage+="' ?)";
		}
		DefaultError error = new DefaultError(this,
			ErrorSource.WARNING,path,lineNum,startOffset,endOffset,
			errorMessage);
		// if(result.getType()==Result.NONE){
		// 	error.addExtraMessage("No suggestion.");
		// }else{
		// 	for(String suggestion:result.getSuggestions()){
		// 		error.addExtraMessage(suggestion);
		// 	}
		// }
		addError(error);
		result.setType(Result.OK);
		return true;//we don't modify anything
	}
	
	public void setPath(String path){
		this.path = path;
	}
	public void start(){
		ErrorSource.unregisterErrorSource(this);
		removeFileErrors(path);
	}

	public void done(){
		ErrorSource.registerErrorSource(this);
	}
	
}
