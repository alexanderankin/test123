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

  Utility class used to capture
  either COMPILE TIME OR RUNTIME ASSERTION FAILURE

*/

public class Assert
{ 
 
  public final static void that ( boolean condition )
  {
    if ( condition ) return ;
    throw new IllegalStateException("## JPyDebug ASSERTION FAILLED :=( ##") ;
  }


  public static void main ( String args[] )
  {
  int i = -1 ;
    Assert.that( (i==-1) ) ;
    Assert.that( (i!=-1) ) ;

  }

}