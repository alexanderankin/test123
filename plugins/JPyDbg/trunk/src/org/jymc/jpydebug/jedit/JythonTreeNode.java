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

package org.jymc.jpydebug.jedit;

import org.jymc.jpydebug.*;

/**
 * @author jean-yves
 *
 * Specializing Node for Jython 
 *
 */
public class JythonTreeNode
extends PythonTreeNode
{
  private JythonInterpretor _jython ; 
  
  public JythonTreeNode( Object buffer , 
                         PythonSyntaxTreeNode node ,
                         JythonInterpretor jython 
                       )	
  {
    super(PythonDebugParameters.ideFront.getBufferPath (buffer) ,node ) ;
    _jython = jython ;
  }

  /**
   * identify Jython module types
   */
  public void identify()
  {
    // do not check root buffer node for at least two reasons :
    // 1 it may implements Jython Swing strange stuff on init _call_ 
    // 2 it is resolved BY DESIGN
    //
      
    if ( get_location() == null )
      _node.set_location( _sourceName ) ;
    
    // check for to module node 
    if ( ( _sourceName != null  ) && 
         ( _sourceName.indexOf(_shortText) != -1 )
       )  
      return ;   

    JythonImported imported = _jython.jImportCheck(_shortText ) ;
    if ( imported == null )
    {
      set_hasLoadfailed(true) ;
      return ;
    }

    switch ( imported.get_type())  
    {
      case JythonImported.BUILTIN :
        set_builtin(true) ; 
        break ; 
      case JythonImported.PYUNDEFINED :
        set_hasLoadfailed(true) ; 
        break ; 
      case JythonImported.JAVACLASS :
      case JythonImported.PYJAVAPACKAGE :
      case JythonImported.PYMODULE :
        set_binary(true) ;
        _node.set_location( imported.get_information() ) ; 
        break ; 

      case JythonImported.PYSOURCEMODULE :  
        _node.set_location( imported.get_information() ) ; 
        break ; 
    }  
  }
  
  public String get_location() 
  { return _node.get_location() ; }
  
}
