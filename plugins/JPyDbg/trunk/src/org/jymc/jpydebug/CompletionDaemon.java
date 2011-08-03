/**
* Copyright (C) 2003,2004,2005 Jean-Yves Mengant
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

import java.io.File;
import java.util.Vector;

import errorlist.DefaultErrorSource;
import java.util.* ; 

/**
 * @author jean-yves
 *
 * Completion daemon utility launcher is used to laucnh a python 
 * IP completion engine used by the editor in the background
 */
public class CompletionDaemon
 extends PythonInterpretor
 implements PythonDebugEventListener
 {
    
    private final static String _COMPLETIONLAUNCHER_ = "completion.py" ; 
    private final static String _COMPLETION_EYECATCHER = "Completion listening on " ; 
    private JpyDbgErrorSource _errorSource ; 
    /** filled with parser initialization exception error */
    private String            _initError = null ;    
    
    private int _listeningPort ;
 	
    /** override the thread start parent proc */
    public void run()
    {
      doTheJob() ;    
    }
 	
 	
    public void newDebugEvent( PythonDebugEvent e ){}
    
    
    public void launcherMessage( PythonDebugEvent e )
    {  
      synchronized(this)
      {
	switch ( e.get_type() )
	{
	  case PythonDebugEvent.LAUNCHER_ENDING :
	    // error running inspector
	    if ( ! e.get_msgContent().equals("0") )
	    {
	      _errorSource.addError( DefaultErrorSource.ERROR ,
			             _COMPLETIONLAUNCHER_ , 
			             0,0,0,
			             "Python Source completion daemon Abort =" + e.get_msgContent() ) ; 
	    }  
	    break ;   

            case PythonDebugEvent.LAUNCHER_ERR :
	      _errorSource.addError( DefaultErrorSource.ERROR ,
			             _COMPLETIONLAUNCHER_ , 
			             0,0,0,
			             "Python Source completion daemon error = " + e.get_msgContent()) ; 
	      break ;   

	    case PythonDebugEvent.LAUNCHER_MSG :
	      System.out.println(e.get_msgContent()) ; 
              
	      break ;   
                
	    default :
	      // we should not be there so just populate error if so 
              _errorSource.addError( DefaultErrorSource.ERROR ,
                                    _COMPLETIONLAUNCHER_ , 
                                    0,0,0,
                                    "unmanaged DebugEvent : " + e.toString()) ;     
	}
      }   
    }
 	
    public void doTheJob()
    {
      // rerun the inspector scanner first 
      super.doTheJob() ; 
    }
 	
    public CompletionDaemon( int connectingPort ,
                             String pgm , 
                             Vector args  ,
                             JpyDbgErrorSource errorSource )
    {
      super( pgm , args , null ) ;
      _errorSource = errorSource ; 
      _listeningPort = connectingPort ;
	  
      super.addPythonDebugEventListener(this) ;  
    }
	
    /**
     * Pratical way of launchin Completion daemon
     * @return the loaded module tree 
     * @throws PythonDebugException
     */	
    public static CompletionDaemon launchCompletion( int connectingPort)
    throws PythonDebugException
    {
      // if autocompletion option is not toggled just exit 
      if (  ! PythonDebugParameters.get_autocompletion() ) 
        return null ;   
      // Allways use CPYTHON to run completion even for Jython context for simplicity 
      // reasons 
      String pythonLoc = PythonDebugParameters.get_pythonShellPath() ; 
      if ( pythonLoc == null )	 
         throw new PythonDebugException("python.exe location not specified => check configuration") ; 
      String pgm = pythonLoc ;  
      Vector args = new Vector() ; 
      // daemon located at the same location as jpydbg
      String completionPath = PythonDebugParameters.get_jpydbgScript () ; 
      if ( completionPath != null )
      {
      File tmp = new File(completionPath) ;
        completionPath = tmp.getParent() + File.separator + _COMPLETIONLAUNCHER_ ;     
      }
      File toLaunch = new File (completionPath) ;
      if ( ! toLaunch.exists() )
          throw new PythonDebugException(completionPath+ " python completion daemon undefined") ; 
      args.addElement(completionPath) ; 
      args.addElement(Integer.toString(connectingPort)) ;
      // the pypath file is appended after
      args.addElement(PythonDebugParameters.get_pyPathLocation()  ) ;
      	 
      CompletionDaemon launcher = new  CompletionDaemon( connectingPort ,
                                                         pgm , 
	                                                 args ,
	                                                 PythonDebugParameters.ideFront.getDefaultErrorSource () ) ; 
	                                                  
      /* call thread empty run just to free object instance correctly */                                                        
      launcher.start() ;
      return launcher ;
   }
   
   public String get_initError()
   { return _initError ; }
 }
