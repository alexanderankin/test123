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

//import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;


/**
 * A validator of a spell check results
 *<p>
 * After a spell check engine runs, its results must be validated (normally by
 * a user). The {@link Validator} class provides this service.
 */
public
class DialogValidator implements Validator
{
  private final HashMap<String,String>        _changeAllMap = new HashMap<String,String>();
  private final HashSet<String>        _ignoreAllSet = new HashSet<String>();

  
  private ValidationDialog vd = new ValidationDialog(null);
  private MyCallback mcb = new MyCallback();

  /**
   * Validate a line of words that have the <code>results</code> of a spell
   * check.
   *<p>
   * @param line String with a line of words that are to be corrected
   * @param results List of {@link Result} of a spell check
   * @return new line with all corrected words validated
   */
  public
  boolean validate(int lineNum, String line, Result result )
  {
	  boolean cancelled = false;
      if( result.getType() != result.OK )
      {
		  
		  if( _changeAllMap.containsKey( result.getOriginalWord() ) )
		  {
			  result.setType(Result.SUGGESTION);
			  result.setSuggestions(Arrays.asList(new String[]{_changeAllMap.get( result.getOriginalWord() )}));
		  }
		  else if( _ignoreAllSet.contains( result.getOriginalWord() ) )
		  {
			  result.setType(Result.OK);
		  }
		  else
		  {
			  try{
				  cancelled = validate(result);
			  }catch(SpellException spe){
				  System.err.println("Error validating "+result);
				  cancelled = true;
			  }
		  }
	  }
	  return !cancelled;
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
  boolean validate( Result result ) throws SpellException
  {
    String replacementWord = null;
	boolean confirm = vd.showAndGo(result,mcb);
	

    if(!confirm )
    {
      replacementWord = null;
    }
    else if(mcb.action == UserAction.CHANGE_ALL )
    {
      if( _changeAllMap.containsKey( result.getOriginalWord() ) )
      {
        System.err.println( "Validator error: Change  all twice same word: " +
                            result.getOriginalWord() );
      }
      _changeAllMap.put( result.getOriginalWord(),
                         mcb.newword );
      replacementWord = mcb.newword;
    }
    else if(mcb.action == UserAction.CHANGE )
    {
      replacementWord = mcb.newword;
    }
    else if(mcb.action == UserAction.IGNORE_ALL )
    {
      if( _ignoreAllSet.contains( result.getOriginalWord() ) )
      {
        System.err.println( "Validator error: Ignore all twice same word: " +
                            result.getOriginalWord() );
      }
      _ignoreAllSet.add( result.getOriginalWord() );
      replacementWord = result.getOriginalWord();
    }
    else if(mcb.action == UserAction.IGNORE )
    {
      replacementWord = result.getOriginalWord();
    }

	if( replacementWord != null )
	{
		if(replacementWord.equals(result.getOriginalWord()))
			result.setType(Result.OK);
		else
		{
			result.setType(Result.SUGGESTION);
			result.setSuggestions(Arrays.asList(new String[]{replacementWord}));
		}
		return false;
	}
	else
		return true;
  }

  /**
   * Helper method to replace the original word with the correction in the line
   */
  protected
  String replaceWord( String originalLine,
                      String originalWord,
                      int    originalIndex,
                      String replacementWord )
  {
    String leftText = originalLine.substring( 0, originalIndex - 1 );
    String rightText = originalLine.substring( originalIndex + originalWord.length() - 1 );

    StringBuffer buf = new StringBuffer();
    buf.append( leftText );
    buf.append( replacementWord );
    buf.append( rightText );

    return buf.toString();
  }

  public void start()
  {
	  //do nothing...
  }
  public void done()
  {
	  //do nothing
  }
  
  static enum UserAction {ADD,CHANGE,CHANGE_ALL,IGNORE,IGNORE_ALL,SUGGEST}
  
  class MyCallback implements ValidationDialog.Callback{
	  String newword;
	  UserAction action;
	  
	  MyCallback(){
		  newword = null;
		  action = null;
	  }
	  
	  public Result add()throws SpellException{
		  action = UserAction.ADD;
		  return null;
	  }
	  
	  public Result change(String newWord)throws SpellException{
		  action = UserAction.CHANGE;
		  return null;
	  }
	  
	  public Result changeAll(String newWord)throws SpellException{
		  action = UserAction.CHANGE_ALL;
		  return null;
	  }
	  
	  public Result ignore()throws SpellException{
		  action = UserAction.IGNORE;
		  return null;
	  }
	  public Result ignoreAll()throws SpellException{
		  action = UserAction.IGNORE_ALL;
		  return null;
	  }
	  
	  public Result suggest(String newWord)throws SpellException{
		  action = UserAction.SUGGEST;
		  return null;
	  }
	  
	  public Result previous()throws SpellException{
		  throw new SpellException("DialogValidator has no previous");
	  }
	  public boolean cancel(){ return true;}
	  public void done(){}
	  
	  public boolean hasPrevious(){return false;}
	  public boolean hasIgnored(){return !_ignoreAllSet.isEmpty();}
  }
}
