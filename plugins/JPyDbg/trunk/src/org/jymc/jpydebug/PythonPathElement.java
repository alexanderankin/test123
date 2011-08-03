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

import java.io.* ;

/**
 * @author jean-yves
 * PytonPath Vector content
 */
public class PythonPathElement
{
  private String _value ; 
  private boolean _curResolver = false ;
  private File _candidate = null ; 
    
  public PythonPathElement( String value )
  { _value = value  ; } 
    
  public void set_curResolver( boolean resolver )
  { _curResolver = resolver ; }
  public boolean is_curResolver()
  { return _curResolver ; }
    
  public String get_value()
  { return _value ; }
    
  public String toString()
  { return _value ; }
  
  public boolean set_candidate( File directory , String source )
  {
  File dir = new File( _value ) ;   
  File candidate = new File( source ) ;
    if ( ( dir.equals(directory)) &&
         ( candidate.isFile() )
       )  
    {  
      _candidate = candidate ; 
      return true ; 
    }  
    _candidate = null ; 
    return false ; 
  }
  
  public String get_candidate()
  {
    if ( _candidate != null )
      return ( _candidate.getAbsolutePath() ) ;
    return null ;   
  }
}

