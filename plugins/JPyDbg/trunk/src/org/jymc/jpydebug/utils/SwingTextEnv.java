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

import java.awt.*    ;
 
/**

  Define all parameters needed To establish Text Label
  display context :

  - FONT
  - BCKGROUND color
  - FOREGROUND color

  @author Jean-Yves MENGANT

*/ 

public class SwingTextEnv {

  private Font  _font ;
  private Color _backGround ;
  private Color _foreGround ;

  public SwingTextEnv( Font  font ,
                       Color backGround ,
                       Color foreGround
                     )
  {
    _font        = font ;
    _backGround  = backGround ;
    _foreGround  = foreGround ;
  }


  public Font get_font()
  { return _font ; }

  public Color get_backGround()
  { return _backGround ; }

  public Color get_foreGround()
  { return _foreGround ; }
}
