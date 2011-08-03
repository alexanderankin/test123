/**
* Copyright (C) 2004-2005 Jean-Yves Mengant
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

// Requires jython.jar
import org.python.core.* ;


/**
 * @author jean-yves
 *
 * Provide details about imported jython module 
 */
public class JythonImported
{
  private final static String _JYTHON_JAVA_CLASS_ = "Jython java class : " ;
  private final static String _JYTHON_JAVA_PACKAGE_ = "Jython java package : " ;
  private final static String _JYTHON_MODULE_ = "Jython Module " ;
  
  public final static int PYMODULE = 0 ;
  public final static int PYJAVAPACKAGE = 1 ;
  public final static int BUILTIN = 2 ; 
  public final static int JAVACLASS = 3 ;   
  public final static int PYUNDEFINED = 4 ;   
  public final static int PYSOURCEMODULE = 5 ;   
  

  private PyObject _imported  = null ;
  private int      _type      = PYUNDEFINED ; 
  public String    _information = null ; 
  
  /**
   * Binary Jython modules
   * @param imported
   */
  public JythonImported( PyObject imported  )
  {
    _imported = imported ; 
    if ( _imported == null )
      _type = PYUNDEFINED ;
    
    if ( imported instanceof PyModule ) 
    {  
      _type = PYMODULE ;
      _information = _JYTHON_MODULE_ ; 
    }  
    else if ( imported instanceof PyJavaClass )
    {
    PyJavaClass cls = (PyJavaClass) imported ; 
      _type = JAVACLASS ;
      _information = _JYTHON_JAVA_CLASS_ + cls.__name__ ; 
    }
    else if ( imported instanceof PyJavaPackage ) 
    {  
    PyJavaPackage pkg = (PyJavaPackage) imported ; 
      _information = _JYTHON_JAVA_PACKAGE_ + pkg.__file__ ;
      _type = PYJAVAPACKAGE ;
    }  
    else if ( imported instanceof PySystemState )
      _type = BUILTIN ;
    else 
      _type = PYUNDEFINED ; 
  }

  public JythonImported( String location  )
  {
    _type = PYSOURCEMODULE ; 
    _information = location ; 
  }
  
  public int get_type()
  { return _type ; }

  public String get_information()
  { return _information ; }
  
}
