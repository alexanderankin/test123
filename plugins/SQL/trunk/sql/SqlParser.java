/**
 * SqlParser.java - Sql Plugin
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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
package sql;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    6 0@B 2001 3.
 */
public class SqlParser
{

  /**
   *  Description of the Field
   *
   * @since
   */
  protected String text;
  /**
   *  Description of the Field
   *
   * @since
   */
  protected char curChar;
  /**
   *  Description of the Field
   *
   * @since
   */
  protected int nextPos;
  /**
   *  Description of the Field
   *
   * @since
   */
  protected int textLength;

  protected final static String VAR_SUBST_PATTERN = "=?";


  /**
   *  Constructor for the SqlParser object
   *
   * @param  sqlText  Description of Parameter
   * @param  pos      Description of Parameter
   * @since
   */
  public SqlParser( String sqlText, int pos )
  {
    text = sqlText;
    textLength = text.length();

    setPos( pos );
    logState( "initParser" );
  }

  
  /**
   * Finds out whether we need the variable substitution
   */
  public boolean isPrepared( String sqlText )
  {
    return sqlText.indexOf ( VAR_SUBST_PATTERN ) > 0;
  }


  /**
   *  Sets the Pos attribute of the SqlParser object
   *
   * @param  pos  The new Pos value
   * @since
   */
  public void setPos( int pos )
  {
    nextPos = pos;
    doStep();
    logState( "setPos to " + pos );
  }


  /**
   *  Gets the NextPos attribute of the SqlParser object
   *
   * @return    The NextPos value
   * @since
   */
  public int getNextPos()
  {
    return nextPos;
  }


  /**
   *  Gets the EolComment attribute of the SqlParser object
   *
   * @return    The EolComment value
   * @since
   */
  public boolean isEolComment()
  {
    logState( "isEolComment" );
    return
        nextPos != textLength &&
        curChar == '-' &&
        text.charAt( nextPos ) == '-';
  }


  /**
   *  Description of the Method
   *
   * @param  point  Description of Parameter
   * @since
   */
  public final void logState( String point )
  {
//    System.out.println( "At " + point + ":" );
//    System.out.println( "  curChar: [" + curChar + "], " + ( int ) curChar );
//    System.out.println( "  pos: " + nextPos + "/" + textLength );
  }


  /**
   *  Description of the Method
   *
   * @exception  SqlEotException  Description of Exception
   * @since
   */
  public void skipWhiteSpace()
       throws SqlEotException
  {
    while ( true )
    {
      // skip whitespace
      if ( skipEolComment() )
      {
        continue;
      }
      if ( skipComment() )
      {
        continue;
      }

      if ( !Character.isWhitespace( curChar ) )
      {
        break;
      }
      if ( nextPos == textLength )
      {
        break;
      }

      doStep();
    }
    logState( "skipWhiteSpace" );
  }


  /**
   *  Description of the Method
   *
   * @exception  SqlEotException  Description of Exception
   * @since
   */
  public void findRealEndOfStatement()
       throws SqlEotException
  {
    nextPos = text.lastIndexOf( '/' );
    if ( nextPos != -1 )
    {
      if ( nextPos == textLength - 1 )
      {
        return;
      }

      final int lastDelim = nextPos;
      nextPos++; doStep();
      skipWhiteSpace();
      // skip the text after last '/'
      if ( nextPos == textLength )
      {
        nextPos = lastDelim;
        return;
      }
    }
    nextPos = textLength;
  }


  /**
   *  Gets the CommentStart attribute of the SqlParser object
   *
   * @return    The CommentStart value
   * @since
   */
  protected boolean isCommentStart()
  {
    logState( "isCommentStart" );
    return
        nextPos != textLength &&
        curChar == '/' &&
        text.charAt( nextPos ) == '*';
  }


  /**
   *  Gets the CommentEnd attribute of the SqlParser object
   *
   * @return    The CommentEnd value
   * @since
   */
  protected boolean isCommentEnd()
  {
    logState( "isCommentEnd" );
    return
        nextPos != textLength &&
        curChar == '*' &&
        text.charAt( nextPos ) == '/';
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  protected void doStep()
  {
    curChar = text.charAt( nextPos++ );
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  protected void doStepBack()
  {
    nextPos -= 2;
    doStep();
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  protected boolean skipEolComment()
  {
    if ( isEolComment() )
    {
      logState( "skipEolComment: comment detected" );
      nextPos++;
      doStep();
      while ( true )
      {
        doStep();
        if ( curChar == '\n' ||
            curChar == '\r' )
        {
          logState( "skipEolComment: Eol detected" );
          if ( nextPos != textLength )
          {
            doStep();
            if ( curChar != '\n' &&
                curChar != '\r' )
            {
              logState( "skipEolComment: One more eol detected" );
              doStepBack();
            }
          }
          break;
        }
        if ( nextPos == textLength )
        {
          break;
        }
      }
      logState( "skipEolComment" );
      return true;
    }
    return false;
  }


  /**
   *  Description of the Method
   *
   * @return                      Description of the Returned Value
   * @exception  SqlEotException  Description of Exception
   * @since
   */
  protected boolean skipComment()
       throws SqlEotException
  {
    if ( isCommentStart() )
    {
      logState( "skipComment: comment detected" );
      nextPos++;
      doStep();
      while ( true )
      {
        doStep();
        if ( isCommentEnd() )
        {
          logState( "skipComment: comment end detected" );
          nextPos++;
          break;
        }
        if ( nextPos == textLength )
        {
          throw new SqlEotException();
        }
      }
      logState( "skipComment" );
      return true;
    }
    return false;
  }


  /**
   *  Description of the Class
   *
   * @author     svu
   * @created    6 0@B 2001 3.
   */
  public static class SqlException extends Exception
  {
  }


  /**
   *  Description of the Class
   *
   * @author     svu
   * @created    6 0@B 2001 3.
   */
  public static class SqlEotException extends SqlException
  {
  }


  /**
   *  Substitutes the variable with user input
   */
  public String substituteVariable( String sqlText, SubstitutionHandler handler )
    throws SqlEotException
  {
    int curPos = sqlText.indexOf( VAR_SUBST_PATTERN );

    while ( curPos > -1 )
    {
      sqlText = handler.substituteFragment( sqlText, curPos + 1, 1 );
      curPos = sqlText.indexOf( VAR_SUBST_PATTERN, curPos + VAR_SUBST_PATTERN.length() );
    }

    return sqlText;
  }


  public static interface SubstitutionHandler
  {
    public String substituteFragment( String text, int pos, int length );
  }
}

