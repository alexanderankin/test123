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
 * PyLint inspector utility launcher is used after the PythonInspector
 * on Save function if the PyLint usage has been toggled
 * Since the sidekickParser is already running its own thread 
 * it is important to override the run parent method of PythonInterpretor
 * class and not to implement a separate thread for running the inspector 
 * else the SideKickParser parse method ends before the inpector and 
 * it's impossible to populate messages to the ErrorList , they simply
 * do not show up
 */
public class PylintInspector
 extends PythonInterpretor
 implements PythonDebugEventListener
 {
    private final static char _ERROR_ = 'E' ; 
    private final static char _FATAL_ = 'F' ; 
    private final static char _WARNING_ = 'W' ; 
    private final static char _REFACTOR_ = 'R' ; 
    private final static char _CONVENTION_ = 'C' ; 
    
    private final static String _PYLINTLAUNCHER_ = "pylintlauncher.py" ; 
    
    private JpyDbgErrorSource _errorSource ; 
    private String _candidate ; 
    private PythonSyntaxTreeNode _result = null ; 
 	
    private JPyDebugXmlParser _parser = new  JPyDebugXmlParser() ; 
    /** filled with parser initialization exception error */
    private String            _initError = null ;    
 	
    /** override the thread start parent proc */
    public void run()
    {}
 	
    public PythonSyntaxTreeNode get_result()
    { return _result ; }
 	
    public void newDebugEvent( PythonDebugEvent e ){}
    
    class _PYLINT_MESSAGE_ 
    {
      char _category ;
      int  _lineNumber  ; 
      String _message ; 
      
      
      public _PYLINT_MESSAGE_( char category , 
                               int lineNumber ,
                               String message 
                             )
      {
        _category = category ; 
        _lineNumber = 0 ; 
        if ( lineNumber > 0 )
          _lineNumber = lineNumber - 1; // relative to 0 
        _message = message ; 
      }
      
      public boolean isSevere()
      {
        if ( _category ==_ERROR_ ||  _category == _FATAL_ ) 
          return true ; 
        return false ; 
      }
      
      public int get_lineNumber()
      { return _lineNumber ; }
      
      public String get_message()
      { return _message ; }
      
    }
	
    private _PYLINT_MESSAGE_ filterPyLintMessage( String message )
    {
       if ( message.length() < 2 ) 
         return null ; 
       if ( message.charAt(0) == ':') 
         return null ; // ::::::::: lines 
       //StefanRank: changed this a bit to do more tolerant parsing. 
       int lineNumberStart = message.indexOf(':') ; 
       if ( lineNumberStart < 1 || lineNumberStart > 5 ) 
         return null ; // not a PyLint Message 
       char category = message.charAt(0) ; 
       String details = "[" + message.substring(0, lineNumberStart) + "] "; 
       int lineNumberEnd = message.indexOf(':', lineNumberStart + 1) ; 
       int lineNumber = 0 ; 
       try { 
         String candidate = message.substring(lineNumberStart+1,lineNumberEnd).trim() ;
         if ( Character.isDigit ( candidate.charAt (0)) )
           lineNumber = Integer.parseInt(candidate) ; 
       } catch ( NumberFormatException e ) 
      { PythonDebugParameters.ideFront.logError ( this , "PyLint CONVERSION FAILURE="+message.substring(2,lineNumberEnd).trim()) ;} 
      details += message.substring(lineNumberEnd+1).trim(); 
      
      switch ( category )
      {
        case _ERROR_ :
          if ( PythonDebugParameters.is_pyLintError() )
            return new _PYLINT_MESSAGE_(category,lineNumber,details) ;
          break ; 
        case _FATAL_ :
          if ( PythonDebugParameters.is_pyLintFatal() )
            return new _PYLINT_MESSAGE_(category,lineNumber,details) ;
          break ; 
        case _WARNING_ :
          if ( PythonDebugParameters.is_pyLintWarning() )
            return new _PYLINT_MESSAGE_(category,lineNumber,details) ;
          break ; 
        case _REFACTOR_ :
          if ( PythonDebugParameters.is_pyLintRefactor() )
            return new _PYLINT_MESSAGE_(category,lineNumber,details) ;
          break ; 
        case _CONVENTION_ :
          if ( PythonDebugParameters.is_pyLintConvention() )
            return new _PYLINT_MESSAGE_(category,lineNumber,details) ;
          break ; 
      }
      return null ; 
    }
    
    public void launcherMessage( PythonDebugEvent e )
    {  
      synchronized(this)
      {
        _result = null ; 
	switch ( e.get_type() )
	{
	  case PythonDebugEvent.LAUNCHER_ENDING :
	    // error running inspector
	    if ( ! e.get_msgContent().equals("0") )
	    {
	      _errorSource.addError( DefaultErrorSource.ERROR ,
			             _candidate , 
			             0,0,0,
			             "Python Source PyLintInspector Abort =" + e.get_msgContent() ) ; 
	    }  
	    break ;   

            case PythonDebugEvent.LAUNCHER_ERR :
	      _errorSource.addError( DefaultErrorSource.ERROR ,
			             _candidate , 
			             0,0,0,
			             "Python Source PyLintInspector launch error = " + e.get_msgContent()) ; 
	      break ;   

	    case PythonDebugEvent.LAUNCHER_MSG :
              String pyLintMsg = e.get_msgContent() ; 
              _PYLINT_MESSAGE_ filtered  = filterPyLintMessage(pyLintMsg) ;  
              if ( filtered != null )
              {
                // candidate just populate accordingly
                if ( filtered.isSevere() )
                  _errorSource.addError( DefaultErrorSource.ERROR ,
                                         _candidate , 
                                         filtered.get_lineNumber(),0,0,
                                         pyLintMsg) ;     
                else
                  _errorSource.addError( DefaultErrorSource.WARNING ,
                                         _candidate , 
                                         filtered.get_lineNumber(),0,0,
                                         pyLintMsg ) ;     
                
              }
	      System.out.println(e.get_msgContent()) ; 
	      break ;   
                
	    default :
	      // we should not be there so just populate error if so 
              _errorSource.addError( DefaultErrorSource.ERROR ,
                                    _candidate , 
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
 	
 	
 	
    public PylintInspector( String pgm , 
                            Vector args , 
                            String candidate , 
                            JpyDbgErrorSource errorSource )
    {
      super( pgm , args , null ) ;
      _errorSource = errorSource ; 
      _candidate = candidate     ;
	  
      super.addPythonDebugEventListener(this) ;  
      try {
        // for performance speed just init once the xml parser	
	_parser.init(null);
      } catch ( PythonDebugException e )
      { _initError = e.getMessage() ; }  
    }
	
    private static void populatePyLintOptions( String args , Vector options )
    {
    StringTokenizer parser = new StringTokenizer(args) ;  
      while ( parser.hasMoreTokens() ) 
        options.add( parser.nextToken() ) ; 
    }

    /**
     * Pratical way of launchin PyLint
     * @param candidate
     * @return the loaded module tree 
     * @throws PythonDebugException
     */	
    public static void launchPyLint( String candidate  )
    throws PythonDebugException
    {
      // if PyLint option is not toggled just exit 
      if (  ! PythonDebugParameters.is_usePyLint () ) 
        return ;   
      // Allways use CPYTHON to irun PyLint even for Jython context for simplicity 
      // reasons ( I am not sure wether or not PyLint code is Jython compliant ....)
      String pythonLoc = PythonDebugParameters.get_pythonShellPath() ; 
      if ( pythonLoc == null )	 
         throw new PythonDebugException("python.exe location not specified => check configuration") ; 
      String pgm = pythonLoc ;  
      Vector args = new Vector() ; 
      String inspectorPath = PythonDebugParameters.get_jpydbgScript () ; 
      if ( inspectorPath != null )
      {
      File tmp = new File(inspectorPath) ;
        inspectorPath = tmp.getParent() + File.separator + _PYLINTLAUNCHER_ ;     
      }
      args.addElement(inspectorPath) ; 
      // Append current pytpath file
      args.addElement(PythonDebugParameters.get_pyPathLocation()  ) ;
     
      String pyLintPath = PythonDebugParameters.get_pyLintLocation ()  ; 
      if ( pyLintPath == null )  
        throw new PythonDebugException("PyLint requested but missing Pylint location => check configuration") ;
      args.addElement(pyLintPath) ; 
	 
      // populate complementay pyLint options if provided
      populatePyLintOptions( PythonDebugParameters.get_pyLintArgs () , args ) ; 

      // finally end with program candidate to Pylint
      args.addElement(candidate) ; 
     
	 
      PylintInspector launcher = new  PylintInspector( pgm , 
	                                               args,
	                                               candidate ,
	                                               // use JEdit Sidekick instance 
	                                               PythonDebugParameters.ideFront.getDefaultErrorSource () ) ; 
	                                                  
      launcher.doTheJob() ;
      /* call thread empty run just to free object instance correctly */                                                        
      launcher.start() ;
   }
   
   public String get_initError()
   { return _initError ; }
 }
