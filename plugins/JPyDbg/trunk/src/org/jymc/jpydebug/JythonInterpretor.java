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

import org.python.util.* ;
import org.python.core.* ;

import java.io.* ;
import java.util.*;

/**
 * @author jean-yves
 *
 * Embedded Jython Interpretor FrontEnd
 *
 */
public class JythonInterpretor
{
  private String _jythonHome = null ; 
  private ByteArrayOutputStream _outBuffer = new ByteArrayOutputStream() ; 
  private ByteArrayOutputStream _errBuffer = new ByteArrayOutputStream() ; 
  private PrintStream _myOut = new PrintStream( _outBuffer ) ;
  private PrintStream _myErr = new PrintStream( _errBuffer ) ;
  
  private Hashtable _pathCheck = new Hashtable() ;

  private static boolean _hasRtInited = false ; 
  
  private PySystemState      _context = null ; 
  //private InteractiveConsole _interp =  null ; 
  private PythonInterpreter _interp =  null ; 
  
  private void interpretorFactory()
  {
    _context = new PySystemState() ; 
    _context.argv = new PyList() ;
    // setup stderr / stdout contexts
    _context.__stdout__ = _context.stdout = new PyFile(_outBuffer, "<stdout>");
    _context.__stderr__ = _context.stderr = new PyFile(_errBuffer, "<stderr>");
    // setup minimal path inheriting System path
    _context.path = Py.getSystemState().path ; 
    _context.path.__setitem__(0 , new PyString(_jythonHome + "/lib" ) ) ;
    // Now create an interpreter
    _interp = new PythonInterpreter( null , _context ) ;


  }
  
   
  private void jython( String pgmName , String args[] )
  {
    // Setup Jython Argv
    if (args != null) 
    {
      for (int i=0; i<args.length; i++) {
        _context.argv.append(new PyString(args[i]));
      }
    }
    String path = new java.io.File(pgmName).getParent();
    if (path == null)
        path = "";
    Py.getSystemState().path.insert(0, new PyString(path));
    try 
    {
      //System.err.println("interp");
      PyModule mod = imp.addModule("__main__");
      _interp.setLocals(mod.__dict__);
      
      _interp.execfile(pgmName);
    } catch (Throwable t) 
    {
      Py.printException(t);
    }
  }
  
  private boolean pathDefined( String name )
  {
    if ( _pathCheck.containsKey(name ) )
      // allready there
      return true ; 
    // add it and say not there
    _pathCheck.put(name,name) ;
    return false ;
  }
  
  /**
   * populate a full PYTHONPATH context to use 
   * @param newPath
   */
  public void set_Path( Vector newPath )
  {
    // reset context Paths
    _pathCheck = new Hashtable() ; // reset control hash
    _context.path = new PyList() ; // and list
    Enumeration curPath = newPath.elements() ; 
    while ( curPath.hasMoreElements() )
    {
    PythonPathElement cur = (PythonPathElement) curPath.nextElement() ;
      add_Path( cur.get_value() ) ; 
    }
  }
  
  public void add_Path( String candidate )
  {
    if ( ! pathDefined( candidate ) ) 
      _context.path.insert( _context.path.__len__() , new PyString(candidate)) ;
  }  
  
  public String get_Path()
  {
  StringBuffer returned = new StringBuffer() ;  
    for ( int ii = 0 ; ii < _context.path.__len__() ; ii++ )
    {  
      returned.append( _context.path.__getitem__(ii).toString()) ;  
      returned.append(';') ;
    }
    return returned.toString() ;
  }
  
  public void exec( String expression )
  throws PythonDebugException
  {
    System.out.println("PATH=" + _context.path ) ;
    try 
    {
      _interp.exec(expression);
    } catch (Throwable t) 
    {
      throw new  PythonDebugException("eval "+ expression+ "= FAILED") ; 
    }
  }
  
  private PyObject eval( String expression )
  throws PythonDebugException
  {
    try 
    {
      return _interp.eval(expression);
    } catch (Throwable t) 
    {
      throw new  PythonDebugException("eval "+ expression+ "= FAILED") ; 
    }
  }
  
  public JythonInterpretor(String jythonHome ) 
  {
    _jythonHome = jythonHome ; 
    // first time only 
    if ( ! _hasRtInited )
    {
      // Setup the basic python system state from these options
      PySystemState.initialize( System.getProperties(),
                                null , new String[]{""} );
     
      _hasRtInited = true ; 
    }
    interpretorFactory() ;  
  }
  
  public void execute ( String pgmName , String args[] )
  throws PythonDebugException
  {
    //String params[] = new String[args.length +1] ; 
    //params[0] = pgmName ; 
    //for ( int ii = 0 ; ii < args.length ; ii++ )
    //  params[ii+1] = args[ii] ;
    // simple jython main activation 
    //jython.main( params ) ; 
    jython( pgmName , args ) ;
    _myOut.close() ;
    _myErr.close() ; 
    
    if ( getErr().trim().length() > 0  )
      throw new PythonDebugException("Jython errors : " +
           getErr() 
                 ) ;
 }

  public String getOut()
  { return  _outBuffer.toString() ; }
   
  private String getErr()
  { 
    return _errBuffer.toString() ; 
  }
  
  /**
   * proceed with import of requested package without calling main core 
   * initialization of modules
   * @param modName package name to import
   * @return Module Object or null if can't be resolved 
   */
  public JythonImported jImportCheck( String modName )
  {
    
    try {
      PyList backup = Py.getSystemState().path ;  
      Py.getSystemState().path = _context.path ;
      // try binary first by import 
      PyObject returned = imp.importName(modName,false) ;
      Py.getSystemState().path = backup ; 
      return new JythonImported(returned) ; 
    } catch( Exception e )
    { 
      // try load source second
      try 
      {
        PyTuple returned = (PyTuple)org.python.modules.imp.find_module( modName ,  _context.path ) ;
        PyFile f = (PyFile) returned.__finditem__(0) ; 
        return new JythonImported(f.name) ;
      } catch ( Exception ex )
      { return null ; }
    }
    
    /*
    StringBuffer toCheck = new StringBuffer("import ") ; 
    StringBuffer typeOf = new StringBuffer("type(") ; 
    toCheck.append(modName) ; 
    try {
      exec(toCheck.toString()) ; 
      typeOf.append(modName) ;
      typeOf.append(')') ; 
      return new JythonImported(eval(typeOf.toString())) ; 
    } catch ( PythonDebugException e )  
    { return null ; }
    */
  }
  
  public static void main( String args[])
  {
    System.out.println("testing jython interpreter startup") ;
    JythonInterpretor jy = new JythonInterpretor("f:/jython-2.1") ; 
    try {  

      System.out.println( jy.eval("2+2").toString()) ;
      //jy.exec("import socket") ;
      //jy.exec("import os") ;
      // jy.exec("import os.path") ;
      jy.exec("print 'test'") ;
      System.out.println(jy.getOut()) ;
      JythonImported imported = jy.jImportCheck("toto") ;
      imported = jy.jImportCheck("pawt") ;
      imported = jy.jImportCheck("java") ;
      imported = jy.jImportCheck("sys") ;
      imported = jy.jImportCheck("os.path") ;
      imported = jy.jImportCheck("os") ;
      imported = jy.jImportCheck("pdb") ;
      System.out.println(imported) ; 
      String argv[] = new String[1] ;
      argv[0] = "C:\\Documents and Settings\\jean-yves\\.jedit\\jpydbgxml\\JYTHON.TXT" ;
      jy.execute("D:\\eclipsesources\\jpydebugforge\\python\\jpytest.py",argv) ; 
      System.out.println("MESSAGES") ;
      System.out.println(jy.getOut()) ;
    } catch ( PythonDebugException e )
    { System.out.println(e.getMessage()) ; }
  }
  
}
