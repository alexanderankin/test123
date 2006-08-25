/*
 * TagStackModel.java - part of the Tags plugin.
 *
 * Copyright (C) 2003 Ollie Rutherfurd (oliver@rutherfurd.net)
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
 * $Id: TagStackModel.java,v 1.3 2004/11/07 15:52:36 orutherfurd Exp $
 */
/*
 * This file originates from the Tags Plugin version 2.0.1
 * whose copyright and licensing is seen above.
 * The original file was modified to become the derived work you see here
 * in accordance with Section 2 of the Terms and Conditions of the GPL v2.
 *
 * The derived work is called the CscopeFinder Plugin and is
 * Copyright 2006 Dean Hall.
 *
 * 2006/08/09
 */

package cscopefinder;

//{{{ imports
import java.util.EmptyStackException;
import javax.swing.DefaultListModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
//}}}

public class TargetStackModel
	extends DefaultListModel
{
	//{{{ push() method
	public void push(TargetStackPosition pos)
	{
		this.insertElementAt(pos,0);
	} //}}}

	//{{{ pop() method
	public TargetStackPosition pop()
		throws EmptyStackException
	{
		TargetStackPosition pos = null;
		try
		{
			pos = (TargetStackPosition)elementAt(0);
			this.removeElementAt(0);
			return pos;
		}catch(ArrayIndexOutOfBoundsException e)
		{
			throw new EmptyStackException();
		}
	} //}}}

  //{{{ peek() method
  public TargetStackPosition peek()
    throws EmptyStackException
  {
    try
    {
      return peek(0);
    }catch(ArrayIndexOutOfBoundsException e)
    {
      throw new EmptyStackException();
    }
  } //}}}

  //{{{ peek() method
  public TargetStackPosition peek(int index)
    throws ArrayIndexOutOfBoundsException
  {
    TargetStackPosition pos = null;
    pos = (TargetStackPosition)elementAt(index);
    return pos;
  } //}}}

  //{{{ releaseBuffer() method
  public void releaseBuffer(Buffer buffer)
  {
    for(int i=0; i < size(); i++)
    {
      TargetStackPosition p = (TargetStackPosition)elementAt(i);
      String path = buffer.getPath();
      if(path.equalsIgnoreCase(p.getPath()))
      {
        Log.log(Log.DEBUG, this,
          p.getPath() + " == " + path); // ##
        p.releasePosition();
      }
      else
        Log.log(Log.DEBUG, this,
          p.getPath() + " != " + path); // ##
    }
  } //}}}
}

// :collapseFolds=1:noTabs=true:tabSize=2:indentSize=2:deepIndent=false:folding=explicit:
