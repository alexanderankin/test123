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

  
  /**
   * Validate a line of words that have the <code>results</code> of a spell
   * check.
   *<p>
   * @param line String with a line of words that are to be corrected
   * @param results List of {@link Result} of a spell check
   * @return new line with all corrected words validated
   */
  public
  List<Result> validate(int lineNum, String line, List<Result> results )
  {
	  List<Result> checkedLine = new ArrayList<Result>(results.size());
    for( int ii=results.size()-1; ii>=0; ii-- )
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
        else if( _ignoreAllSet.contains( result.getOriginalWord() ) )
        {
          newResult = new Result(
					result.getOffset(),
					Result.OK,
					null,
					result.getOriginalWord());
        }
        else
        {
          newResult = validate( result );
        }

        if( newResult != null )
        {
			if(Result.OK != newResult.getType())checkedLine.add(newResult);
        }
		else
		{
            checkedLine = null;
            break;
		}
      }
    }

    return checkedLine;
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
  public
  Result validate( Result result )
  {
    String replacementWord = null;
	
    ValidationDialog validationDialog;
    validationDialog = new ValidationDialog( result.getOriginalWord(),
                                             result.getSuggestions() );
    validationDialog.show();

    ValidationDialog.UserAction userAction = validationDialog.getUserAction();
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
      if( _ignoreAllSet.contains( result.getOriginalWord() ) )
      {
        System.err.println( "Validator error: Ignore all twice same word: " +
                            result.getOriginalWord() );
      }
      _ignoreAllSet.add( result.getOriginalWord() );
      replacementWord = result.getOriginalWord();
    }
    else if( userAction == validationDialog.IGNORE )
    {
      replacementWord = result.getOriginalWord();
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
}
