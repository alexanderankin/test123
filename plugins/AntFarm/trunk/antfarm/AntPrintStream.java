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

import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

public class AntPrintStream extends PrintStream
{

	String cache = "";
	View _view;


	public AntPrintStream( PrintStream stream, View view )
	{
		super( stream );
		_view = view;
	}


	public void println( String msg )
	{
// 		Log.log( Log.DEBUG, this, cache + msg );
		parseLine( cache + msg );
		resetCache();
	}


	public void println( char[] msg )
	{
		parseLine( cache + new String( msg ) );
		resetCache();
	}


	public void print( String msg )
	{
		if ( msg.indexOf( "\n" ) > 0 ) {
			StringTokenizer tokenizer = new StringTokenizer( msg, "\n" );
			while ( tokenizer.hasMoreElements() )
				println( tokenizer.nextToken() );
		}
		else {
			cache += msg;
		}
	}


	public void flush()
	{
		super.flush();
		parseLine( cache );
	}


	private void resetCache()
	{
		cache = "";
	}


	private void parseLine( String line )
	{
		Console console =
			(Console) _view.getDockableWindowManager().getDockableWindow( "console" );

		// should be project dir
		String dir = System.getProperty( "user.dir" );

		if ( console == null )
			return;

		int type = ConsolePlugin.parseLine( line, dir, AntFarmPlugin.getErrorSource() );

		switch ( type ) {
			case ErrorSource.ERROR:
				console.print( console.getErrorColor(), line );
				break;
			case ErrorSource.WARNING:
				console.print( console.getWarningColor(), line );
				break;
			default:
				if ( line.trim().equals( "BUILD SUCCESSFUL" ) )
					console.print( console.getInfoColor(), line );
				else if ( line.trim().equals( "BUILD FAILED" ) )
					console.print( console.getErrorColor(), line );
				else
					console.print( null, line );
				break;
		}
	}

}

