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

/**
 * @author jean-yves
 *
 * commuenication event between Debugger Front End and Pluggin editors
 */
public class PluginEvent
{
	public final static int UNDEFINED = -1 ;

	public final static int NEWSOURCE = 0 ;
	public final static int NEWLINE   = 1 ; 
	public final static int STARTING  = 2 ; 
	public final static int ENDING    = 3 ; 
	public final static int ENTERCALL = 4 ; 
	public final static int LEAVECALL = 5 ; 
	public final static int BUSY      = 6 ; 
	public final static int NOTBUSY   = 7 ; 
	 

    private int _type = UNDEFINED ; 
    private String _source = null ; 
    private int    _line   = UNDEFINED	 ; 
    
    
    public PluginEvent( int type , String source , int line )
    {
      _type = type ; 
      _source = source ; 
      _line = line ;     	
    }

    public int get_type()
    { return _type ;}
    
    public String get_source()
    { return _source ;}

    public int get_line()
    { return _line ; }

}
