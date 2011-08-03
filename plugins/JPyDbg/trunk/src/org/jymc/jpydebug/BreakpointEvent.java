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

import java.util.* ; 

/**
 * @author jean-yves
 *
 * populated for breakpoint changes 
 */
public class BreakpointEvent
{

	public final static int NONE   = -1 ;
	public final static int SET    = 0 ;
	public final static int REMOVE = 1 ;
	public final static int REMOVEALL = 2 ;
  
    private int _action       = NONE   ; 
    private int _lineNumber   = NONE   ; 
    private String _sourcePath = null ; 
    private Hashtable _bpSet  = null ; 

    public BreakpointEvent( int action , int lineNumber , String source  ) 
    {
      _action = action ; 
      _lineNumber = lineNumber ; 
      _sourcePath = source ; 	
    }
    
    public BreakpointEvent( int action , Hashtable bpSet )
    {
	  _action = action ; 
      _bpSet = bpSet   ; 	
    }
    
    public int get_action()
    { return _action ; }
    public int get_lineNumber()
    { return _lineNumber ; }
    public String get_sourcePath()
    { return _sourcePath ; } 
    public Hashtable get_bpSet()
    { return _bpSet ; }
}
