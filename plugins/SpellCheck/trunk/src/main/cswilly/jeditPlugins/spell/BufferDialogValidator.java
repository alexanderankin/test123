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

package cswilly.jeditPlugins.spell;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

import javax.swing.text.Position;


import cswilly.spell.Result;
import cswilly.spell.Validator;
import cswilly.spell.ValidationDialog;
import cswilly.spell.WordListValidator;


import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;



/**
 * A validator of a spell check results, ensuring that misspelled words are visible and selected
 * Upon prompt of the user
 */
public
class BufferDialogValidator implements Validator
{
  private final HashMap<String,String>        _changeAllMap = new HashMap<String,String>();
  private WordListValidator        ignoreAll = null;
  private TextArea area;
  private JEditBuffer buffer;
  private Position savedPosition;
  private ValidationDialog validationDialog;
  private WordListValidator userDict;
  
  
  /**
   * Validate a line of words that have the <code>results</code> of a spell
   * check.
   *<p>
   * @param line String with a line of words that are to be corrected
   * @param results List of {@link Result} of a spell check
   * @return new line with all corrected words validated
   */
  public
  boolean validate(int lineNum, String line, List<Result> results )
  {
	  //List<Result> checkedLine = new ArrayList<Result>(results.size());
	  if(userDict!=null)userDict.validate(lineNum,line,results);
	  if(ignoreAll!=null)ignoreAll.validate(lineNum,line,results);
    for( int ii=results.size()-1; ii>=0;ii--)
    {
      Result result = results.get( ii );
	  Result newResult = null;
      if( result.getType() != result.OK )
      {

        if( _changeAllMap.containsKey( result.getOriginalWord() ) )
        {
          newResult = new Result(
					result.getOffset(),
					Result.SUGGESTION,
					Arrays.asList(new String[]{_changeAllMap.get( result.getOriginalWord() )}),
					result.getOriginalWord());
        }
        else
        {
          newResult = validate(lineNum, result );
        }

        if( newResult != null )
        {
			if(Result.OK != newResult.getType()){
				results.set(ii,newResult);
			}
			else results.remove(ii);
        }
		else
		{
            return false;
		}
      }else{
		  results.remove(ii); 
	  }
    }

    return true;
  }
  

  /**
   * Validates a single correction
   *<p>
   *
   *<p>
   * @param result A {@link Result} of spell checking one word
   * @return validated correction (this is the replacement word). <i>null</i>
   *         is returned if the operation is cancelled. The replacement word
   *         maybe the same or different from the original word in
   *         <code>result</code>.
   */
  private
  Result validate(int lineNum, Result result )
  {
    String replacementWord = null;
	
    
	// ensures visible and selected
	int offset = buffer.getLineStartOffset(lineNum)+result.getOffset()-1;
	Selection s = new Selection.Range(offset,offset+result.getOriginalWord().length());
	area.setSelection(s);
	/* Waiting for fix to bug [ 1990960 ] "Invalid screen line error" when looping in macro
	area.scrollTo(lineNum,result.getOffset()-1,false);
	*/

    ValidationDialog.UserAction userAction =
		validationDialog.getUserAction(result.getOriginalWord(),
				result.getSuggestions(),
				ignoreAll!=null && ignoreAll.getAllWords().size()>0);
    if( userAction == validationDialog.CANCEL )
    {
      replacementWord = null;
    }
    else if( userAction == validationDialog.CHANGE_ALL )
    {
      if( _changeAllMap.containsKey( result.getOriginalWord() ) )
      {
        System.err.println( "Validator error: Change  all twice same word: " +
                            result.getOriginalWord() );
      }
      _changeAllMap.put( result.getOriginalWord(),
                         validationDialog.getSelectedWord() );
      replacementWord = validationDialog.getSelectedWord();
    }
    else if( userAction == validationDialog.CHANGE )
    {
      replacementWord = validationDialog.getSelectedWord();
    }
    else if( userAction == validationDialog.IGNORE_ALL )
    {
		replacementWord = result.getOriginalWord();
		if(ignoreAll!=null)ignoreAll.addWord(replacementWord);
		//todo : if twice the same word in the line, won't be ignored
    }
    else if( userAction == validationDialog.IGNORE )
    {
      replacementWord = result.getOriginalWord();
    }
    else if( userAction == validationDialog.ADD )
    {
      replacementWord = result.getOriginalWord();
	  if(userDict!=null)userDict.addWord(replacementWord);
    }

	if( replacementWord != null )
	{
		if(replacementWord.equals(result.getOriginalWord()))
			return new Result(
					result.getOffset(),
					Result.OK,
					null,
					result.getOriginalWord());
		else return new Result(
					result.getOffset(),
					Result.SUGGESTION,
					Arrays.asList(new String[]{replacementWord}),
					result.getOriginalWord());
	}
	else
		return null;
  }


 
  public void setTextArea(TextArea ta){
	  this.area = ta;
	  this.buffer = ta.getBuffer();
	  savedPosition = buffer.createPosition(ta.getCaretPosition());
	  validationDialog = new ValidationDialog(((JEditTextArea)area).getView());
  }

  public void setUserDictionary(WordListValidator valid){
	 userDict = valid;
  }
  
  public void setIgnoreAll(WordListValidator ignoreAll){
	 this.ignoreAll = ignoreAll;
  }

  public void start()
  {}
  
  public void done()
  {
	  //TODO can I save a rectangular selection and restore it in a meaningful way ?
	  area.setCaretPosition(savedPosition.getOffset());
	  /*
	  Waiting for fix to bug [ 1990960 ] "Invalid screen line error" when looping in macro
	  area.scrollToCaret(false);
	*/
	  userDict=null;
	  ignoreAll=null;
  }

}
