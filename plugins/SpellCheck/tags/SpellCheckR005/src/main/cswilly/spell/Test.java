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

import java.io.*;

public
class Test
{
  BufferedReader _aSpellReader;
  BufferedWriter _aSpellWriter;

  public static
  void main( String[] args )
    throws IOException
  {
    new Test();
  }

  private Test()
    throws IOException
  {
    System.err.println( "TestTest" );

    String aSpellCommandLine = "O:\\local\\aspell\\aspell.exe pipe";

    Runtime runtime = Runtime.getRuntime();
    Process aSpellProcess = runtime.exec( aSpellCommandLine );

    _aSpellReader =
      new BufferedReader( new InputStreamReader( aSpellProcess.getInputStream() ) );

    _aSpellWriter =
      new BufferedWriter( new OutputStreamWriter( aSpellProcess.getOutputStream() ) );

    String aSpellWelcomeMsg;
    aSpellWelcomeMsg = _aSpellReader.readLine();
    System.err.println( "aSpellWelcomeMsg: " + aSpellWelcomeMsg );

    checkWords( "expetr nobody noboyd response" );
    checkWords( "table lable llll" );
  }

  private
  void checkWords( String words )
    throws IOException
  {
    System.err.println( "words: " + words );

    final String spellCheckLinePrefix = "^";
    _aSpellWriter.write( spellCheckLinePrefix + words );
    _aSpellWriter.newLine();
    _aSpellWriter.flush();

    String response = _aSpellReader.readLine();
    while( response != null &&
           !response.equals( "" ) )
    {
      System.err.println( "response: " + response );
      Result result = new Result( response );
      System.err.println( "result: " + result );

      response = _aSpellReader.readLine();
    }
  }

}
