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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.jymc.jpydebug.jedit.JPYJeditOptionPane;
import org.jymc.jpydebug.jedit.JPYPythonParser;


/**
 * @author jean-yves
 *
 * Python inspector utility launcher used by the JEdit sidekcik and
 * other jedit and non jedit tools
 * Since the sidekickParser is already running its own thread 
 * it is important to override the run parent method of PythonInterpretor
 * class and not to implement a separate thread for running the inspector 
 * else the SideKickParser parse method ends before the inpector and 
 * it's impossible to populate messages to the ErrorList , they simply
 * do not show up
 */
public class PythonInspector
 extends PythonInterpretor
 implements PythonDebugEventListener
 {
   private final static String _INSPECTOR_ = "inspector.py" ; 

    public  final static String JPYDBGXML = "jpydbgxml"    ; 
    private final static String _INSPECTOR_SUFFIX_ = ".xml" ;
    private final static String _H_ = "H"    ;
    
 	private StringBuffer _stdout = new StringBuffer() ; 
 	private JpyDbgErrorSource _errorSource ; 
 	private String _candidate ; 
	private PythonSyntaxTreeNode _result = null ; 
 	
	private JPyDebugXmlParser _parser = new  JPyDebugXmlParser() ; 
	/** filled with parser initialization exception error */
	private String            _initError = null ;    
	
	private static void checkDirectory( String dir )
	{
	File toCheck = new File(dir) ; 
	  // create jpydbgxml directory once
	  if ( ! toCheck.exists() )
	    if ( ! toCheck.mkdirs() )
	      PythonDebugParameters.ideFront.logError( PythonInspector.class , "jpydbg fails to create wmlwk dir :" + dir);    	
	}
	
	public static String getJpyDbgXmlDirectory()
	{
	StringBuffer wk = new StringBuffer(PythonDebugParameters.ideFront.getSettingsDirectory()) ;  
	  wk.append( File.separatorChar ) ; 
	  wk.append( JPYDBGXML) ;
	  checkDirectory( wk.toString() ) ;
	  wk.append( File.separatorChar ) ; 
	  return wk.toString() ; 
	}
	
	public static String hashDestFile( String pyName )
	{
	StringBuffer wk = new StringBuffer(pyName) ; 
	File pyF   = new File (pyName) ; 
	  	  
	  wk.append(_INSPECTOR_SUFFIX_) ;	
	  int hash = wk.toString().hashCode() ;
	  // try to build an acceptable xml unique filename in temporary work directory
	  wk = new StringBuffer( getJpyDbgXmlDirectory() ) ;
	  wk.append( _H_) ;
	  wk.append(Integer.toString(hash)) ; 
	  wk.append( pyF.getName() ) ; 
	  wk.append(_INSPECTOR_SUFFIX_) ;  
	  return wk.toString()  ; 
	}


 	/** 
 	 * 
 	 * @return true when .py has been updated 
 	 */
 	private boolean updated()
 	{
 	File pyF   = new File (_candidate) ; 
 	File pyXml = new File ( hashDestFile(_candidate ) ) ;	
 	
 	  if ( pyXml.lastModified() < pyF.lastModified() ) 
 	    return true ; 
 	  return false ;   
 	}
 	
 	private PythonSyntaxTreeNode parseXmlResult()
 	{
	String toParse = hashDestFile(_candidate) ; 
    StringBuffer xmlStr = new StringBuffer() ; 
 	  try {
 	  	String str = null ; 
 	    BufferedReader f = new BufferedReader( new FileReader(toParse)) ; 
 	    while ( ( str = f.readLine() ) != null )
 	      xmlStr.append(str) ; 
 	  } catch ( IOException e )
	  { 
        _errorSource.addError( JpyDbgErrorSource.ERROR ,
		                   toParse , 
				   0,0,0,
				   toParse + " ReadError:" + e.getMessage()) ; 
	  }
 	  try {
		PythonDebugEvent evt = new PythonDebugEvent( _parser , xmlStr.toString() ) ; 
 	    // check inspector production and populate it
 	    if ( evt.get_msgContent() != null )
 	    {
 	    int line = Integer.parseInt(evt.get_lineSource()) ; 
 	      // Python parsing error occured 
	      _errorSource.addError( JpyDbgErrorSource.ERROR ,
		                     evt.get_fName() , 
				     line-1 ,
				     0,0,
				     evt.get_msgContent() ) ; 
 	    }
 	    else
 	    {
 	      // populate python tree
 	      _result = _parser.get_pythonTree() ; 
 	      return _result ; 
 	    }   	
 	  } catch( PythonDebugException e )
	  { _errorSource.addError( JpyDbgErrorSource.ERROR ,
				   _candidate , 
				   0,0,0,
				   toParse + " Xml parse error :" + e.getMessage()) ; 
	  }
	  return null ; 
 	}
 	
 	/** override the thread start parent proc */
 	public void run()
 	{}
 	
 	public PythonSyntaxTreeNode get_result()
 	{ return _result ; }
 	
	public void newDebugEvent( PythonDebugEvent e ){}
	
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
			  _errorSource.addError( JpyDbgErrorSource.ERROR ,
			                         _candidate , 
			                         0,0,0,
			                         "Python Source Inspector Abort =" + e.get_msgContent() ) ; 
			}  
			else 
			  // parse xml inspector production 
			  _result = parseXmlResult() ; 
			break ;   

		  case PythonDebugEvent.LAUNCHER_ERR :
		     _errorSource.addError( JpyDbgErrorSource.ERROR ,
			                    _candidate , 
			                    0,0,0,
			                    "Python Source Inspector launch error = " + e.get_msgContent()) ; 
			break ;   

		  case PythonDebugEvent.LAUNCHER_MSG :
			_stdout.append( e.get_msgContent() ) ; 
			System.out.println(e.get_msgContent()) ; 
			break ;   
                
		  default :
		    // we should not be there so just populate error if so 
			_errorSource.addError( JpyDbgErrorSource.ERROR ,
			                       _candidate , 
			                       0,0,0,
			                       "unmanaged DebugEvent : " + e.toString()) ;     
		  }	
		}
	}
 	
 	public void doTheJob(boolean force)
 	{
 	  if ( force || updated() )
 	    // rerun the inspector scanner first 
 	    super.doTheJob() ; 
 	  else 
	    // .py not updated parse xml inspector production 
	    _result = parseXmlResult() ; 
 	}
 	
 	
 	
   public PythonInspector( String pgm , 
                           Vector args , 
                           Vector jythonArgs ,
                           String candidate , 
                           JpyDbgErrorSource errorSource )
   {
     super( pgm , args , jythonArgs ) ;
     _errorSource = errorSource ; 
     _candidate = candidate     ;
	  
     super.addPythonDebugEventListener(this) ;  
     try {
	// for performance speed just init once the xml parser	
	_parser.init(null);
     } catch ( PythonDebugException e )
     { _initError = e.getMessage() ; }  
   }
	

   /**
    * Pratical way of loading a module tree 
    * @param candidate
    * @return the loaded module tree 
    * @throws PythonDebugException
    */	
   public static PythonSyntaxTreeNode launchInspector( String candidate , boolean force )
   throws PythonDebugException
   {
   // Allways use CPYTHON to inspect PYTHON code even for Jython context
   // The reason is that inspector using the CPYTHON AST syntax tree to analyze the 
   // PYTHON source structure and Jython does not generate .pyc compiled PYTHON object it 
   // produces   
     String pythonLoc = PythonDebugParameters.get_pythonShellPath() ; 
     if ( pythonLoc == null )	 
       throw new PythonDebugException("python.exe location not specified => check configuration") ;
     String pgm = pythonLoc ; 
     Vector args = new Vector() ; 
     String inspectorPath = PythonDebugParameters.get_jpydbgScript() ; 
     if ( inspectorPath != null )
     {
	 File tmp = new File(inspectorPath) ;
	   inspectorPath = tmp.getParent() + File.separator + _INSPECTOR_ ; 	
	 }
	 args.addElement(inspectorPath) ; 
	 
	 args.addElement(candidate) ; 
	 args.addElement( hashDestFile(candidate) ) ; 

     // since jpydbg V0.0.9 the pypath file is appended here
     args.addElement(PythonDebugParameters.get_pyPathLocation()  ) ;
     
	 PythonInspector launcher = new  PythonInspector( pgm , 
	                                                  args,
	                                                  // Allways use CPYTHON to inspect python sources
	                                                  // Event if we're in a Jython context
	                                                  null ,
	                                                  candidate ,
	                                                  // use JEdit Sidekick instance 
	                                                  // or the Netbeans instance
	                                                  PythonDebugParameters.ideFront.getDefaultErrorSource() ) ; 
	                                                  
	 launcher.doTheJob(force) ;
	/* call thread empty run just to free object instance correctly */                                                        
	 launcher.start() ;
	 
	 return launcher.get_result() ;
   }
   
   public String get_initError()
   { return _initError ; }
 }
