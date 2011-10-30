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
  private String[]       _aspellArgs;
  private AspellEngine _spellEngine          = null;
  private DialogValidator    _spellValidator       = null;
  private String _encoding;
  
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

  public FileSpellChecker( String aspellExeFilename, String[] aspellArgs )
  {
    _aspellExeFilename = aspellExeFilename;
    _aspellArgs = aspellArgs;
	_encoding = System.getProperty("file.encoding");
  }

  public FileSpellChecker()
  {
      this( "/opt/local/bin/aspell", new String[]{"pipe"} );
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
          List<Result> results = _getSpellEngine().checkLine( line );
		  boolean cancel = false;
		  for(Result res : results){
			  cancel = _getSpellValidator().validate(0, line, res );
			  if(cancel)break;
		  }
		  if(!cancel) checkedLine = applyChanges(line, results);
          else
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
  String[] getAspellArgs()
  {
    return _aspellArgs;
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
	  if( _spellEngine == null ){
		  List<String> args = new ArrayList<String>(_aspellArgs.length+1);
		  args.add(_aspellExeFilename);
		  for(int i=0;i<_aspellArgs.length;i++){
			  args.add(_aspellArgs[i]);
		  }
		  args.add("--encoding="+_encoding);
      _spellEngine = new AspellEngine(args,_encoding,false);
	  }
    return _spellEngine;
  }

  private
  DialogValidator _getSpellValidator()
  {
    if( _spellValidator == null )
      _spellValidator = new DialogValidator();

    return _spellValidator;
  }

  /**
   * Helper method to replace the original words with the correction in the line
   * @param	results	list of validated results : only SUGGESTIONS with 1 choice
   */
  public static String applyChanges(String originalLine, List<Result> results){
	  if(results.isEmpty())return originalLine;
	  
	  StringBuilder buf = new StringBuilder(originalLine.length());
	  int currentIndex = 0;
	  for(Result result: results){
		  if(result.getType()!=Result.SUGGESTION)continue;
		  int originalIndex = result.getOffset();
		  buf.append(originalLine,currentIndex,originalIndex-1);
		  buf.append(result.getSuggestions().get(0));
		  currentIndex = originalIndex + result.getOriginalWord().length();
	  }
	  return buf.toString();
  }
}
