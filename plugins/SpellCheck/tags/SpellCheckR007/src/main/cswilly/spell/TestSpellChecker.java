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

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public
class TestSpellChecker
{
  private static final DialogValidator _validator = new DialogValidator();
  public static
  void main( String[] args )
    throws SpellException
  {
    new TestSpellChecker();
  }

  private TestSpellChecker()
    throws SpellException
  {
    System.err.println( "TestSpellChecker()" );

    List<String> l = new ArrayList<String>(2);
	l.add("O:\\local\\aspell\\aspell.exe");
  	l.add("pipe");

    AspellEngine spellChecker = new AspellEngine( l , "ISO-8859-1", false);

    String words;
    List results;

    words = "expetr nobody noboyd response";
    System.err.println( "\nwords: " + words );
    results = spellChecker.checkLine( words );
    //printResults( results );
    checkResults( words, results );

    words = "table lable llll";
    System.err.println( "\nwords: " + words );
    results = spellChecker.checkLine( words );
    //printResults( results );
    checkResults( words, results );

    System.exit( 0 );
  }

  /**
  * @param results List of  {@link Result}
   */
  private
  void printResults( List results )
  {
    for( int ii=1; ii<results.size(); ii++ )
    {
      Result result = (Result)results.get( ii );
      System.err.println( "result(" + ii + "): " + result );
    }
  }

  private
  void checkResults( String line, List results )
  {
    for( int ii=1; ii<results.size(); ii++ )
    {
      Result result = (Result)results.get( ii );
      if( result.getType() != result.OK )
      {
		  Result oldR = null;
		  try{
			  oldR = (Result)result.clone();
		  }catch(CloneNotSupportedException cnse){}

		  boolean confirmed = _validator.validate(1,line,result);
		  
		  if(!confirmed){
			  System.err.println("User cancelled");
			  return;
		  }
		  
		  System.err.println("oldresult is : "+oldR);
		  System.err.println("result is : "+result);
      }
    }
  }
}
