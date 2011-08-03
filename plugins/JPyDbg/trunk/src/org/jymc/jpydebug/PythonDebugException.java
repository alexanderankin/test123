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

import java.lang.Exception ;

/**
 * @author jean-yves
 *
 * PyDebugException : exception thrown by PyDebug environment
 */

public class PythonDebugException
extends Exception 
{
  public PythonDebugException()
  {}

  // constructor with String input message
  public PythonDebugException( String Msg  )
  { super(Msg) ; }
}
 