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


package org.jymc.jpydebug.swing.ui ;

import javax.swing.* ;
import java.awt.*    ;

/**
*  Subclass JSplitPane to workaround on placement of divider location
*
*/

public class PythonSplitPane
extends JSplitPane
{
  private double   _proportionalLocation ;
  private boolean  _hasProportionalLocation = false ;
  private boolean  _isPainted = false ;

  public void setDividerLocation( double proportionalLocation )
  {
    if (!_isPainted)
    {
      _hasProportionalLocation = true;
      _proportionalLocation = proportionalLocation;
    }
    else
      super.setDividerLocation(proportionalLocation);
  }
  public void setDividerLocation(int location) {
    super.setDividerLocation(location);
  }

  public void paint(Graphics g)
  {
    if (!_isPainted)
    {
      if ( _hasProportionalLocation)
        super.setDividerLocation(_proportionalLocation);
      _isPainted = true;
      doLayout() ; // prevent flicker
    }
    super.paint(g);
  }

  public PythonSplitPane( int newOrientation , Component c1 , Component c2 )
  {
    super(newOrientation,c1,c2) ;
  }

}