package org.jymc.jpydebug.jedit;

import sidekick.* ; 
import org.gjt.sp.jedit.* ; 
import org.gjt.sp.util.*  ;

import errorlist.* ; 
import org.jymc.jpydebug.* ;

import java.util.* ;

/* 
 * we heavily depends on sidekick to parse python source and provide
 * Python tree structure
 */

public class JPYPythonParser
extends SideKickParser
 
{
  private final static String _PYTHON_ = "JPyDebug" ; 

  private static Hashtable _syntaxTrees = new Hashtable() ; 
  private static PythonTreeNodeEventListener _listener = null ; 
  private static DefaultErrorSource _defaultErrorSource = null ; 
  
  public static PythonSyntaxTreeNode getPythonSyntaxTree( Buffer buffer )
  { 
    return (PythonSyntaxTreeNode)_syntaxTrees.get(buffer) ; 
  }
  
  public synchronized static void  addPythonTreeNodeEventListener( PythonTreeNodeEventListener lstnr )
  { _listener = lstnr ; }
  public synchronized static void  removePythonTreeNodeEventListener( PythonTreeNodeEventListener lstnr )
  { 
    if ( _listener == lstnr )
      _listener = null ; 
  }
  
  public static JpyDbgErrorSource get_defaultErrorSource()
  { 
    return new JEditDefaultErrorSource( _defaultErrorSource ) ; 
  }
  
  public JPYPythonParser()
  { super(_PYTHON_) ; }
  
  
  /**
   * check for local launch of Python Inspector stuff
   * @param pythonLoc
   * @param inspectorPyLoc
   * @param buffer
   */
   private PythonParsedData localPythonLaunch( Buffer buffer )
   throws PythonDebugException
   {
   String  inspectorFName = FtpBuffers.checkBufferPath( buffer ) ; 
     // store python syntax goodies in global hash 
     PythonSyntaxTreeNode parsedNode = PythonInspector.launchInspector( inspectorFName ,false ) ;
     // process pyLint if requested 
     PylintInspector.launchPyLint(inspectorFName) ; 
	 if ( parsedNode != null )
	 {  
	   _syntaxTrees.put( buffer , parsedNode ) ; 
	   if ( _listener != null )
	     _listener.newTreeNodeEvent( new PythonTreeNodeEvent( buffer , parsedNode)) ;
	      
	   return new PythonParsedData( buffer , parsedNode ) ; 	
	 }
	 return null ; 
   }                                     


   /**
   *  Parse A python source providing either a Python tree 
   *  or a Syntax Error 
   */
   public SideKickParsedData parse( Buffer buffer , 
                                    DefaultErrorSource errorSource
                                  )
   {
     Log.log(Log.DEBUG,this,"entering parser");   
     // keep track of Sidekick Error source instance for later usage
     _defaultErrorSource = errorSource ; 
     String pPath = PythonDebugParameters.get_currentShellPath() ; 
	   
     // when entering the Sidekick thread , the PythonPanel may not
     // have loaded the property file , this will be done here then	
     // in order to avoid later PythonInspector failures
     if ( pPath == null )
     {  
       PythonJeditPanel.loadProperties() ; 
     }
   
    try {
      SideKickParsedData parsed = localPythonLaunch( buffer ) ; 
      // in case of previous error it's important to populate
      // JpyDbg back to front
      // JPYJeditPlugin.showJpyDbg() ;
      return parsed ; 
    } catch( PythonDebugException e )
    { 
	errorSource.addError( DefaultErrorSource.ERROR ,
	                      buffer.getPath() , 
			      0,0,0,
			      e.getMessage() ) ; 
     }                   
     return null ; 	
   }                               
}
