/*
 *  AntPrintStream.java - Plugin for running Ant builds from jEdit.
 *  Copyright (C) 2001 Brian Knowles
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package antfarm;
import console.*;
import errorlist.*;

import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

public class AntPrintStream extends PrintStream
{
	View _view;
    Output _output;
	String _cache = "";

	public AntPrintStream( PrintStream stream, View view, Output output )
	{
		super( stream );
		_view = view;
        _output = output;
	}


	public void println( String msg )
	{
		parseLine( msg );
	}


	public void println( char[] msg )
	{
		parseLine( new String( msg ) );
	}


	public void print( String msg )
	{
		// Ant 1.4 and 1.5 org.apache.tools.ant.taskdefs.compilers.Javac13 sends output
		// incorrectly. It must be tokenized and sent to println().
		if ( msg.equals(" ")) {
			_cache += msg;
			return;
		}
		if ( msg.indexOf( "\n" ) > 0 ) {
			StringTokenizer tokenizer = new StringTokenizer( msg, "\n", false );
			while ( tokenizer.hasMoreElements() )
				println( _cache + tokenizer.nextToken() );
			resetCache();
		}
		else {
			println( _cache + msg );
			resetCache();
		}
	}
	
	private void resetCache() {
		_cache = "";
	}
	
	public void write(byte b[], int i, int j)
	{
		  String msg = new String(b,i,j);
		  print(msg);
	}

	private void parseLine( String line )
	{
		line = chomp(line);
	
		Log.log( Log.DEBUG, null, line + ":" + line.length() );
		if (line == null || line.trim().equals("")) return;
		
		Console console =
			(Console) _view.getDockableWindowManager().getDockable( "console" );

		// should be project dir
		String dir = System.getProperty( "user.dir" );

		if ( console == null )
			return;

		int type = ConsolePlugin.parseLine( _view, line, dir, AntFarmPlugin.getErrorSource() );

		switch ( type ) {
			case ErrorSource.ERROR:
				_output.print( console.getErrorColor(), line );
				break;
			case ErrorSource.WARNING:
				_output.print( console.getWarningColor(), line );
				break;
			default:
				if ( line.trim().equals( "BUILD SUCCESSFUL" ) )
					_output.print( console.getInfoColor(), line );
				else if ( line.trim().equals( "BUILD FAILED" ) )
					_output.print( console.getErrorColor(), line );
				else
					_output.print( null, line );
				break;
		}
	}
	
	private String chomp(String str) {
		if (str.endsWith("\n")) {
			return str.substring(0, str.length() - 1);
		}
		else {
			return str;
		}
	}

}

