/**
* Copyright (C) 2003-2004 Jean-Yves Mengant
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

import org.gjt.sp.jedit.*;
import org.jymc.jpydebug.* ; 

/**
 * @author jean-yves
 * Event populated by tree node syntax analysis
 */
public class PythonTreeNodeEvent
{
   private Buffer _buf ; 
   private PythonSyntaxTreeNode _node ; 
  
   public PythonTreeNodeEvent( Buffer buf , PythonSyntaxTreeNode node )
   {
     _buf = buf ; 
     _node = node ; 
   }
  
   public PythonSyntaxTreeNode get_node()
   { return _node ; }
   
   public Buffer get_buf()
   { return _buf ; }
  
}
