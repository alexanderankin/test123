/**
 * SpecialCommentRemover.java - Sql Plugin
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
package sql.preprocessors;

import java.util.*;

import sql.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    22 Февраль 2002 г.
 */
public class SpecialCommentRemover extends Preprocessor
{

  protected final static String PROP_NAME =
      SpecialCommentRemover.class.getName() + ".tokens";


  /**
   *Description of the Method
   *
   * @param  text  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  public String doProcess( String text )
  {

    ArrayList comments = (ArrayList) this.getAllSpecialComments();
    Iterator iter = comments.iterator();
    while ( iter.hasNext() )
    {
      final String comment = ( (String) iter.next() );

      int curPos = text.indexOf( comment );
      while ( curPos > -1 )
      {
        text = substituteFragment( text, curPos, comment.length() );
        curPos = text.indexOf( comment, curPos + comment.length() );
      }
    }

    return text;
  }


  /**
   *  Description of the Method
   *
   * @param  text              Description of Parameter
   * @param  pos               Description of Parameter
   * @param  substitutionSize  Description of Parameter
   * @return                   Description of the Returned Value
   */
  protected String substituteFragment( String text, int pos, int substitutionSize )
  {
    return text.substring( 0, pos ) +
        text.substring( pos + substitutionSize );
  }


  /**
   *  Gets the AllSpecialComments attribute of the SpecialCommentRemover class
   *
   * @return    The AllSpecialComments value
   */
  public static List getAllSpecialComments()
  {
    final ArrayList lst = new ArrayList();
    try
    {
      final String prop = SqlPlugin.getProperty( PROP_NAME );
      final StringTokenizer strTkn = new StringTokenizer( prop, "?" );

      while ( strTkn.hasMoreTokens() )
      {
        lst.add( strTkn.nextToken() );
      }
    } catch ( NullPointerException ex )
    {
      //!! just ignore missing property
    }
    return lst;
  }


  /**
   *  Description of the Method
   *
   * @param  comments  Description of Parameter
   */
  public static void save( List comments )
  {
    final Iterator iter = comments.iterator();
    final StringBuffer text = new StringBuffer();
    while ( iter.hasNext() )
    {
      text.append( (String) iter.next() + "?" );
    }

    final String stext = text.toString();
    SqlPlugin.setProperty( PROP_NAME,
        stext.length() > 0 ?
        stext.substring( 0,
        stext.length() - 1 ) : "" );
  }

}

