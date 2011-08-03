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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.* ;


/**
 * @author jean-yves
 *
 * Internal JpyDebug representation of a python PYTHONPATH
 */
public class PythonPath
{
  private final static String _PYTHON_SUFFIX_ = ".py" ;
    
  /** final System+ user consolidated path */ 
  private Vector _path = new Vector() ; 
  
  public Vector get_Path()
  { return _path ;}
  
  public void setPath( String pythonPath )
  {
    if ( File.pathSeparatorChar != ';' && pythonPath.indexOf(';') != 0 )
    {
      // cleanly take care of previous PYTHONPATH convention for compatibility
      // purposes
      pythonPath = pythonPath.replace(';' , File.pathSeparatorChar) ;
    }
    
    StringTokenizer parser = new StringTokenizer(pythonPath,File.pathSeparator) ; 
    while( parser.hasMoreTokens() )
    {
    String cur = parser.nextToken() ;   
    PythonPathElement content = new  PythonPathElement(cur) ;
      _path.addElement(content) ; 
    }  
  }

  private File buildPythonPathFile( boolean isJython )
  {
    if (PythonDebugParameters.get_debugTrace())
      PythonDebugParameters.ideFront.logInfo( this , "looking for Pythonpath here : " + PythonDebugParameters.get_currentPathLocation(isJython) ) ;
    if ( PythonDebugParameters.get_currentPathLocation(isJython) != null )
      return  new File (PythonDebugParameters.get_currentPathLocation(isJython)) ;
    return null ; 
  }
  
  public void readPath( boolean isJython )
  {
  File f = buildPythonPathFile(isJython) ;
    if ( f == null )
      return ; // path undefined by user
    // clear previous content
    _path = new Vector() ;  
    String pythonPath = "" ; 
    if ( f.exists() )
    {
    char rBuf[]= new char[(int)(f.length())] ;  
      try {
        BufferedReader r = new BufferedReader( new FileReader(f)) ;
        r.read(rBuf) ;
        pythonPath = new String(rBuf) ; 
        r.close() ; 
      } catch ( IOException e )
      {
        PythonDebugParameters.ideFront.logError( this , "IOError on PYTHONPATH read " +  f.toString()+":"
                                                 + e.getMessage() ) ;
      }
    }
    else 
      // Initial Jython Case
      if ( PythonDebugParameters.get_jythonActivated() )
      {
      JythonInterpretor jy = new JythonInterpretor(PythonDebugParameters.get_jythonHome()) ;  
        pythonPath = jy.get_Path() ;
      }  
    setPath(pythonPath) ; 
  }
  
  public void writePath( boolean isJython )
  {
  Enumeration pyPathList = _path.elements() ;
  StringBuffer pyPath = new StringBuffer() ; 
  File f = buildPythonPathFile(isJython) ;   
    
    if ( f == null )
      return ; // PATH undefined by user

    while ( pyPathList.hasMoreElements())
    {  
      PythonPathElement cur = (PythonPathElement)pyPathList.nextElement() ; 
      pyPath.append(cur.get_value()) ;
      pyPath.append(File.pathSeparatorChar) ;
    }  

    try {
      BufferedWriter w = new BufferedWriter( new FileWriter(f)) ;
      w.write(pyPath.toString()) ;
      w.close() ;
    } catch ( IOException e )
    {
      PythonDebugParameters.ideFront.logError( this,"IOError on PYTHONPATH write " +  f.toString()+":"
                                               + e.getMessage() ) ;
    }
  }
  
  
  public PythonPathElement locatePythonSource( String source )
  {
  Enumeration paths = _path.elements() ;  
  int cur = 0 ; 
  int pycPos = source.indexOf(".pyc") ; 
    if (pycPos != -1 )
      source = source.substring(0,pycPos) + _PYTHON_SUFFIX_ ;        
      
    File sourceF = new File(source) ;
    if ( sourceF.isFile() )
    {  
    File sourceDir =  sourceF.getParentFile() ;  
  
      while ( paths.hasMoreElements() )
      {
      PythonPathElement curP = (PythonPathElement)paths.nextElement() ;  
        if ( curP.set_candidate( sourceDir , source) ) 
          return curP ; 
        cur++ ; 
      }
    }  
    return null ;
      
  }

  public int size()
  { return _path.size() ; }
  
  public Object elementAt( int pos )
  { return _path.elementAt(pos) ; }
  
  public int indexOf( Object element )
  { return _path.indexOf(element) ; }
  
  public void removeElementAt( int pos )
  { _path.removeElementAt(pos) ;}
  
  public void addElement( Object element )
  { _path.addElement(element) ; }
  
  public void setElementAt( Object element , int pos )
  { _path.setElementAt(element , pos) ; }
  
  public void insertElementAt( Object element , int pos )
  { _path.insertElementAt(element , pos) ; }
  
  public Enumeration get_lines()
  { return _path.elements() ; }
}
