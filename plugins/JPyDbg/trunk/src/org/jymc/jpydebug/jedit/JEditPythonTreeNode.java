/*
 * JEditPythonTreeNode.java
 *
 * Created on December 21, 2005, 12:49 PM
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
 *
 */

package org.jymc.jpydebug.jedit;

import org.gjt.sp.jedit.Buffer;

import sidekick.Asset;

import javax.swing.Icon;


/**
 * Bridgind JpyDbg PythonNodes back to Jedit Asset Compliant object
 *
 * @author jean-yves
 */
public class JEditPythonTreeNode extends Asset
{
  private PythonTreeNode _pNode;

  /**
   * Creates a new instance of JEditPythonTreeNode
   */
  public JEditPythonTreeNode( PythonTreeNode pNode, Buffer buffer )
  {
    super( pNode.getShortString() );
    _pNode      = pNode;
    super.start = super.end = buffer.createPosition(
                                                    buffer.getLineStartOffset(pNode.getLineNo() )
                                                 );
  }

  public Icon getIcon()
  {
    return _pNode.getIcon();
  }


  public String getLongString()
  {
    return _pNode.getLongString();
  }


  public String getShortString()
  {
    return _pNode.getShortString();
  }

}
