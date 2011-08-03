
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
* along with this program; if not, write to the Free Software_dbgtool
* 
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package org.jymc.jpydebug.swing.ui;

import org.jymc.jpydebug.CompositeCallback ;

/**
 * implement a compatible interface for netbeans / jedit python shell 
 * containers
 * @author jean-yves
 */
public interface PythonContainer 
{
   public void inspectCompositeCommand( CompositeCallback callBack , String varName ) ;
  
   public void dbgVariableChanged( String name , String value , boolean global ) ;

}
