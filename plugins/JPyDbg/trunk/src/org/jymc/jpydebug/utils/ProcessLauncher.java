/**
* Copyright (C) 1998,1999,200,2001,2002,2003 Jean-Yves Mengant
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

package org.jymc.jpydebug.utils;

import java.util.* ;
import java.io.* ;

/**

  isolate process launching 

  @author Jean-Yves MENGANT

*/

public class ProcessLauncher
extends Thread
{
  public final static int PROCESS_EXCEPTION         = 99 ;
  private final static String _NO_SYSTEM_ERROR_ = "SYSTEM EXCEPTION=none" ;

  // the launched process component
  private Process _executable  ;
  private String  _env[]    = null    ;
  private String  _args[]      ;
  private int     _retCode     ;
  private ExecTerminationListener _listener ;
  private InputStream  _stdout ;
  private InputStream  _stderr ;

  public Process getProcess()
  { return _executable ; }
  
  public void setEnv( String env )
  {
    if ( ( env == null ) || ( env.length() == 0 ) )
      return ;

    StringTokenizer parser = new StringTokenizer(env) ;

    _env = new String[parser.countTokens()] ;

    for ( int ii = 0 ; ii < _env.length ; ii++ )
    {
      _env[ii] = parser.nextToken() ;
    }
  }

  public InputStream getStdout()
  {
    return  _stdout  ;
  }

  public InputStream getStderr()
  {
    return  _stderr   ;
  }

  /**
    @param command a sting containing the program name followed
    by an optional argument list
  */
  public void setCommand( Vector command )
  throws UtilsError
  {
    _args = new String[command.size()] ;

    for ( int ii = 0 ; ii < _args.length ; ii++ )
    {
      _args[ii] = (String) command.elementAt(ii) ; 
      System.out.println("_args["+ii+"]="+_args[ii]) ;
    }
    try {

      _executable = Runtime.getRuntime().exec(_args,_env) ;
      _stdout = _executable.getInputStream() ;
      _stderr = _executable.getErrorStream() ;
    } catch ( IOException e )
    {
      throw new UtilsError( "IOException when building process : " +
                            e.getMessage()
                          ) ;
    }
  }

  public void setExecTerminationListener ( ExecTerminationListener listener )
  {
    _listener = listener ;
  }

  public void waitForCompletion()
  {
    try {
      _retCode = _executable.waitFor() ;
    } catch ( InterruptedException e )
    {}

    /* broadcast termination event to any subscribed listener */
    if ( _listener !=  null )
      _listener.processHasEnded( new ExecTerminationEvent( _retCode ,
                                                           _NO_SYSTEM_ERROR_
                                                         )
                               ) ;
  }

  public void run()
  {
    waitForCompletion() ;
  }

  public ProcessLauncher()
  {}

  /**
    test process launching
  */
  public static void main ( String args[] )
  {

    ProcessLauncher launcher = new ProcessLauncher()  ;
    try {
    Vector argList = new Vector() ;
    int ii  = 0      ;
      while ( ii < args.length )
        argList.addElement( args[ii++] ) ;

      launcher.setCommand(argList);
      InputStream stdOut = launcher.getStdout() ;
      InputStream stdErr = launcher.getStderr() ;
      WL reader = new WL(stdOut, stdErr) ;
      reader.start() ;
      launcher.waitForCompletion();
    } catch ( UtilsError e )
    { System.out.println("ERROR :" + e.getMessage() ) ; }
  }
}

/**
  local non public classes used by main
*/
class WL
extends Thread
{
  InputStream _out ;
  InputStream _err ;
  public WL( InputStream out , InputStream err )
  {
    _out = out ;
    _err = err ;
  }

  public void run()
  {
  int c ;
    try {
      while ( ( c = _out.read() ) != -1 )
        System.out.print((char) c) ;

    System.out.println("end of print" ) ;
    _out.close() ;

    while ( ( c = _err.read() ) != -1 )
      System.out.print((char) c) ;
    _err.close() ;
    }  catch( IOException e )
    { e.printStackTrace() ; }
  }
}


