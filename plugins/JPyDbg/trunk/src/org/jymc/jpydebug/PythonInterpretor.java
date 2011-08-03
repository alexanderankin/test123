/**
* Copyright (C) 2003,2004 Jean-Yves Mengant
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

package org.jymc.jpydebug;

import java.io.* ; 
import java.util.* ;

import org.jymc.jpydebug.utils.*; 

/**
 * @author jean-yves
 *
 * implement a local python interpretor launcher utility
 *
 */
public class PythonInterpretor
extends Thread
implements ExecTerminationListener
{
  private ProcessLauncher _launcher = new ProcessLauncher() ; 
  private Vector _command = null ; 
  private PythonDebugEventListener _listener = null ; 
  
  public synchronized void addPythonDebugEventListener( PythonDebugEventListener l )
  { _listener = l ; }
  public synchronized void removePythonDebugEventListener()
  { _listener = null ; }
  
    	
  private void populateInterpretorEvent ( int type ,String msg )
  {
  	if ( _listener != null )
  	{
      PythonDebugEvent evt = new PythonDebugEvent( type , msg ) ;
	  _listener.launcherMessage(evt) ; 
  	}  
  }

  public Process getProcess()
  { return _launcher.getProcess() ; }
  
  public PythonInterpretor( String pgm , Vector args , Vector jythonArgs)
  {
    if ( jythonArgs != null )
    {
      // When Jython launch is requested
      // put Jython JVM args before
      _command = jythonArgs ; 
      // and standard launching after
      _command.addAll(args) ; 
    }
    else 
      // For CPYTHON we only need the shell execution path location
      _command = args ;
    
    _command.insertElementAt(pgm,0) ; 
  }
  
  class _DBG_STREAM_READER_
  extends Thread 
  {
	private int _type ; 
	private InputStream _istream	; 
	private StringBuffer _resultBuffer ; 
    	
	public _DBG_STREAM_READER_( int type , InputStream istream )
	{
	  _type = type ; 
	  _istream = istream ;	
	  _resultBuffer = new StringBuffer() ; 
	}	
	public void run()
	{
	int c ;
	  
	  try 
	  {
		while ( ( c = _istream.read() ) != -1 )
		{
		  if ( c == '\n' )
		  {
			populateInterpretorEvent( _type , _resultBuffer.toString() );	
			_resultBuffer = new StringBuffer() ; 
		  }
		  else 
		  _resultBuffer.append((char) c) ;

		}
		_istream.close() ;
	  }  catch( IOException e )
	  { populateInterpretorEvent( PythonDebugEvent.LAUNCHER_ERR , e.getMessage() ); }
	}
  }  
  
  /**
  * Daemon ending 
  */
  public void processHasEnded( ExecTerminationEvent evt ) 
  {
  int code = evt.get_code() ; 	
  String retCode = Integer.toString( code ); 	
	populateInterpretorEvent( PythonDebugEvent.LAUNCHER_ENDING , retCode )  ; 	
  }
  
  public void doTheJob()
  {
	try {	
	  _launcher.setCommand(_command) ; 
	  _launcher.setExecTerminationListener(this) ;  	
	  _DBG_STREAM_READER_ stdoutReader = new _DBG_STREAM_READER_( PythonDebugEvent.LAUNCHER_MSG , _launcher.getStdout() ) ;
	  _DBG_STREAM_READER_ stderrReader = new _DBG_STREAM_READER_( PythonDebugEvent.LAUNCHER_MSG , _launcher.getStderr() ) ;
	  stdoutReader.start() ; 
	  stderrReader.start() ; 
	  _launcher.waitForCompletion();
	} catch (UtilsError e ) 
	{ 
	  populateInterpretorEvent( PythonDebugEvent.LAUNCHER_ERR , e.getMessage() )  ; 	
	}
  }
  
  public void run()
  { doTheJob() ; }

  public static void main(String[] args)
  {}
}
