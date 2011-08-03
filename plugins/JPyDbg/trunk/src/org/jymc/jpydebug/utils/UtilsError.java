/**
* Copyright (C) 1998,1999,200,2001,2002,2003 Jean-Yves Mengant
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

package org.jymc.jpydebug.utils;


public class UtilsError extends Exception {

  /**
  default constructor for UtilsError
  @param no parameters 
  */
  public UtilsError (){}

  /**
  Constructor providing an Explanation message
  @param msg Exception explanation message
  */
  public UtilsError ( String msg  )
  { super(msg) ; }

}
