/*
 * $Revision: 1.4 $
 * $Date: 2002-07-26 15:36:20 $
 * $Author: lio-sand $
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

import java.util.ArrayList;
import java.util.List;

/**
 * Models the result of a spell check of a single word.
 *<p>
 */
public
class FileSpellChecker
{
  private String       _aspellExeFilename;
  private String       _aspellCommandLine;
  private AspellEngine _spellEngine          = null;
  private Validator    _spellValidator       = null;

  public static
  void main( String[] args )
  {
    int exitStatus;

    String inputFilename = "spellTest.txt";
    try
    {
      BufferedReader input  = new BufferedReader( new FileReader( inputFilename ) );
      BufferedWriter output = new BufferedWriter( new OutputStreamWriter( System.out ) );

      FileSpellChecker checker = new FileSpellChecker();

      checker.checkFile( input, output );

      input.close();
      output.close();

      exitStatus  = 0;
    }
    catch( Exception e )
    {
      e.printStackTrace( System.err );
      exitStatus = 1;
    }

    System.exit( exitStatus );
  }

  public FileSpellChecker( String aspellExeFilename, String aspellCommandLine )
  {
    _aspellExeFilename = aspellExeFilename;
    _aspellCommandLine = aspellCommandLine;
  }

  public FileSpellChecker()
  {
      this( "O:\\local\\aspell\\aspell.exe", "" );
  }

  /**
   * @return <i>true</i> if file completely checked and <i>false</i> if the user
   * interupted the checking.
   */
  public
  boolean checkFile( BufferedReader input, BufferedWriter output )
    throws SpellException
  {
    try
    {
      String line = input.readLine();
      while( line != null )
      {
        String checkedLine;
        if( line.trim().equals( "" ) )
        {
          checkedLine = line;
        }
        else
        {
          List results = _getSpellEngine().checkLine( line );

          checkedLine = _getSpellValidator().validate( line, results );
          if( checkedLine == null )
            return false;
        }

        output.write( checkedLine );

        line = input.readLine();

        // Restore each line separator except for the last one (we don't know
        // here if selected text to check ends with such a line separator)
        if( line != null )
          output.write( '\n' );
      }
    }
    catch( Exception e )
    {
      stop();
      if( e instanceof SpellException )
        throw (SpellException)e;
      else
        throw new SpellException( "Error communicating with the aspell subprocess", e );
    }

    return true;
  }

  public
  String getAspellExeFilename()
  {
    return _aspellExeFilename;
  }

  public
  String getAspellCommandLine()
  {
    return _aspellCommandLine;
  }

  public
  void stop()
  {
    if( _spellEngine != null )
    {
      _spellEngine.stop();
      _spellEngine = null;
    }
  }

  private
  Engine _getSpellEngine()
    throws SpellException
  {
    if( _spellEngine == null )
      _spellEngine = new AspellEngine( _aspellExeFilename + _aspellCommandLine );

    return _spellEngine;
  }

  private
  Validator _getSpellValidator()
  {
    if( _spellValidator == null )
      _spellValidator = new Validator();

    return _spellValidator;
  }

}
