/**
* Copyright (C) 2003 Jean-Yves Mengant
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

import java.awt.*;
import java.util.* ;
import java.io.File ;

/** 
 * @author jean-yves
 *
 * this class is used to store python debuggers parameters
 *
 */
public class PythonDebugParameters
{
  private final static String _JPYTHON_HOME_ = "-DPython.home=";
  private final static String _CLASSPATH_ = "-classpath";
  private final static String _JYTHONCLASS_ = "org.python.util.jython";
  private final static String _JYTHONJAR_ = "jython.jar";
  
  
  /** remote optional connecting port when jpydaemon is in listening mode on dbghost */
  private static int _connectingPort = -1 ; 
  /** default listening port for debugging solicitor to connect UI interface */
  private static int _listeningPort = 29000     ; 
  /** full path to pyhon executable location */
  private static String _pythonShellPath = null      ; 
  /** if not null JpyDbg to connect to */
  private static String _dbgHost = null              ;
  /** python debugger dameon script name */ 
  private static String _jpydbgScript = "jpydbg.py"  ;
  /** python debugger dameon script name */ 
  private static String _jpydbgScriptArgs = ""  ;
  /** temporary work directory */
  private static String _tempDir = "./"  ;  
  /** shell used font */
  private static Font _shellFont = null ; 
  /** shell used background color */
  private static Color _shellBackground = Color.lightGray ; 
  /** shell used message color */
  private static Color _shellMessage = Color.black ; 
  /** shell used warning color */
  private static Color _shellWarning = Color.magenta ; 
  /** shell used Header color */
  private static Color _shellHeader = Color.blue ; 
  /** shell used Error color */
  private static Color _shellError = Color.red ; 
  /** autocompletion code facility */
  private static boolean _autoCompletion = false ; 
  /** autocompletion delay */
  private static int _autoCompletionDelay = 200 ; 
  /** debug tracing in jedit activity log */
  private static boolean _trace = true ; 
  /** debugging expression evaluation from textarea mouse move */
  private static boolean _debugDynamicEvaluation = false  ; 
  /** pyPath file location */
  private static String _pyPathLocation = null ; 
  /** remote codepage to use */
  private static String _codePage = null ; 
  
  /** where Jython is installed */
  private static String _jythonHome = null ;
  /** jython activation is on */
  private static boolean _jythonActivated = false ; 

  /** JVM to use for Jython activation */
  private static String _jythonShellJvm = null ;
  /** args for JVM to use for Jython activation */
  private static String _jythonShellArgs = null ;
  /**  */
  private static String _jyPathLocation = null ;
  
  /** PyLint location */
  private static String _pyLintLocation = null ;
  
  /** PyLint complementary args */
  private static String _pyLintArgs = null ;
  
  /** use PyLint  */
  private static boolean _usePyLint = false ;
  
  /** PyLint  */
  private static boolean _pyLintFatal = false ;
  
  /** PyLint  */
  private static boolean _pyLintError = false ;
  
  /** PyLint  */
  private static boolean _pyLintWarning = false ;
  
  /** PyLint  */
  private static boolean _pyLintConvention = false ;
  
  /** PyLint  */
  private static boolean _pyLintRefactor = false ;
  
  private static String _currentDbgLineColor ; 
  private static String _currentBpLineColor  ; 
  
  /** Front End Multi IDE interface (MUST BE SET AT IDE PLUGIN INIT TIME)  by Ide Pluggin*/
  public static IDEFrontEnd ideFront = null ; 
  
  /* PyLint Static Stuff starts here */
  public static void set_pyLintLocation( String pyLintLocation )
  { _pyLintLocation = pyLintLocation ; }
  public static String get_pyLintLocation()
  { return _pyLintLocation ; }
  public static void set_pyLintArgs( String pyLintArgs )
  { _pyLintArgs = pyLintArgs ; }
  public static String get_pyLintArgs()
  { return _pyLintArgs ; }
  public static void set_usePyLint( boolean usePyLint )
  { _usePyLint = usePyLint ; }
  public static boolean is_usePyLint()
  { return _usePyLint ; }
  public static void set_pyLintFatal( boolean pyLintFatal )
  { _pyLintFatal = pyLintFatal ; }
  public static boolean is_pyLintFatal()
  { return _pyLintFatal ; }
  public static void set_pyLintError( boolean pyLintError )
  { _pyLintError = pyLintError ; }
  public static boolean is_pyLintError()
  { return _pyLintError ; }
  public static void set_pyLintWarning( boolean pyLintWarning )
  { _pyLintWarning = pyLintWarning ; }
  public static boolean is_pyLintWarning()
  { return _pyLintWarning ; }
  public static void set_pyLintConvention( boolean pyLintConvention )
  { _pyLintConvention = pyLintConvention ; }
  public static boolean is_pyLintConvention()
  { return _pyLintConvention ; }
  public static void set_pyLintRefactor( boolean pyLintRefactor )
  { _pyLintRefactor = pyLintRefactor ; }
  public static boolean is_pyLintRefactor()
  { return _pyLintRefactor ; }
  /* PyLint Static Stuff ends here */
  
  
  public static void set_debugDynamicEvaluation( boolean debugDynamicEvaluation )
  { _debugDynamicEvaluation = debugDynamicEvaluation ; }
  public static boolean get_debugDynamicEvaluation()
  { return _debugDynamicEvaluation ; }
  
  public static void set_debugTrace( boolean trace )
  { _trace = trace ; }
  public static boolean get_debugTrace()
  { return _trace ; }
  
  public static void set_codePage( String codePage )
  { if ( (codePage != null) && (codePage.length() == 0 ) )
       _codePage = null ;
    else 
      _codePage = codePage ; 
  }
  public static String get_codePage()
  { return _codePage ; }
  
  
  public static void set_autoCompletionDelay( int autoCompletionDelay )
  { _autoCompletionDelay = autoCompletionDelay ; }
  public static int get_autoCompletionDelay()
  { return _autoCompletionDelay ; }
  
  public static void set_autocompletion( boolean autocompletion )
  { _autoCompletion = autocompletion ; }
  public static boolean get_autocompletion()
  { return _autoCompletion ; }

  public static void set_jythonActivated( boolean jythonActivated )
  { _jythonActivated = jythonActivated ; }
  public static boolean get_jythonActivated()
  { return _jythonActivated ; }

  public static void set_listeningPort( int port ) 
  { _listeningPort = port ; }
  public static int get_listeningPort()
  { return _listeningPort ;  }

  public static void set_pythonShellPath( String path ) 
  { _pythonShellPath = path ; }
  public static String get_pythonShellPath()
  { return _pythonShellPath ; }

  public static void set_jythonHome( String path ) 
  { _jythonHome = path ; }
  public static String get_jythonHome()
  { return _jythonHome ; }
  
  public static String get_currentShellPath()
  {
    if ( _jythonActivated )
      return _jythonShellJvm ; 
    return _pythonShellPath ; 
  }

  public static void set_jythonShellJvm( String jvmPath ) 
  { _jythonShellJvm = jvmPath ; }
  public static String get_jythonShellJvm()
  { return _jythonShellJvm ; }

  public static void set_jythonShellArgs( String jythonArgs ) 
  { _jythonShellArgs = jythonArgs ; }
  public static String get_jythonShellArgs()
  { return _jythonShellArgs ; }

  /*
  public static Vector build_jythonArgsVector()
  {
    if ( _jythonActivated )
    {
    // build a plain argument vector out of jython JVM arguments
    Vector returned = new Vector() ;
      if ( _jythonShellArgs != null )
      {
      StringTokenizer parser = new StringTokenizer(_jythonShellArgs) ;
        while( parser.hasMoreTokens() )
          returned.addElement( parser.nextToken() ) ;
      }
      return returned ;
    }
    // Jython is not used return null
    return null ;
  }
  */
  
  public static void set_dbgHost( String dbgHost )
  { _dbgHost = dbgHost ; }
  public static String get_dbgHost()
  { return _dbgHost ; }

  public static void set_jpydbgScript( String jpydbgScript )
  { _jpydbgScript = jpydbgScript ; }
  public static String get_jpydbgScript()
  { return _jpydbgScript ; }
 
  public static void set_jpydbgScriptArgs( String jpydbgScriptArgs )
  { _jpydbgScriptArgs = jpydbgScriptArgs ; }
  public static String get_jpydbgScriptArgs()
  { return _jpydbgScriptArgs ; }
 
  public static void set_tempDir( String tempDir ) 
  { _tempDir = tempDir ; }
  public static String get_workDir()
  { return _tempDir ; }
  
  public static String get_pyPathLocation()
  { return _pyPathLocation ; }
  public static void set_pyPathLocation( String pyPathLocation )
  { _pyPathLocation = pyPathLocation ; }
  
  public static void set_jyPathLocation( String jyPathLocation )
  { _jyPathLocation = jyPathLocation ; }
  public static String get_jyPathLocation()
  { return _jyPathLocation ; }
  
  public static String get_currentPathLocation( boolean jythonActivated )
  { 
    if ( jythonActivated )
      return _jyPathLocation ;
    return _pyPathLocation ;
  }
  
  public static void set_connectingPort( int connectingPort ) 
  { _connectingPort = connectingPort ; }
  public static int get_connectingPort()
  { return _connectingPort ; }
  
  public static void set_shellBackground( Color background )
  { _shellBackground = background ; }
  public static Color get_shellBackground()
  { return _shellBackground ; } 
  
  public static void set_shellError( Color error )
  { _shellError = error ; }
  public static Color get_shellError()
  { return _shellError ; }
  
  public static void set_shellFont ( Font shellFont )
  { _shellFont = shellFont ; }
  public static Font get_shellFont()
  { 
    return _shellFont ; 
  }
  
  public static void set_shellHeader( Color shellHeader )
  { _shellHeader = shellHeader ; }
  public static Color get_shellHeader()
  { return _shellHeader ; }
  
  public static void set_shellMessage( Color shellMessage )
  { _shellMessage = shellMessage ; }
  public static Color get_shellMessage()
  { return _shellMessage; }
  
  public static void set_shellWarning( Color shellWarning )
  { _shellWarning = shellWarning ; }
  public static Color get_shellWarning()
  { return _shellWarning ; }
  
  /**
   * gracefully build the Jython args vector out of user's properties
   * @return
   */
  public static Vector buildJythonArgs( boolean jythonActivated )
  {
    if ( ! jythonActivated ) 
      return null ;
    
    Vector returned = new Vector() ;   
    PythonPath path = new PythonPath() ;   
    // PythonHome
    StringBuffer buf = new StringBuffer(_JPYTHON_HOME_) ;
    buf.append(_jythonHome) ; 
    returned.add(buf.toString()) ;
    // CLASSPATH from PythonPath.
    returned.add(_CLASSPATH_) ; 
    path.readPath(jythonActivated) ; // read current JYTHON path content
    buf = new StringBuffer() ; 
    // populate from pythonpath
    Enumeration pathList = path.get_Path().elements() ; 
    while ( pathList.hasMoreElements() ) 
    {
      buf.append(pathList.nextElement()) ; 
      buf.append(File.pathSeparator) ;
    }
    // allways append jython.jar for security 
    buf.append(_jythonHome) ; 
    if ( ! _jythonHome.endsWith(File.separator) ) 
      buf.append(File.separatorChar) ;
    buf.append(_JYTHONJAR_) ; 
    System.out.println(buf.toString()) ; 
    returned.add(buf.toString()) ;
    returned.add(_JYTHONCLASS_) ; 
    
    return returned ; 
  }
  
  
  public static void set_currentDbgLineColor( String color )
  { _currentDbgLineColor = color ; }
  public static String get_currentDbgLineColor()
  { return _currentDbgLineColor ; }
  public static void set_currentBpLineColor( String color )
  { _currentBpLineColor = color ; }
  public static String get_currentBpLineColor()
  { return _currentBpLineColor ; }
}
