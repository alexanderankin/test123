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
 * $Id$
 */

package tags;

//{{{ imports
import java.util.EmptyStackException;
import javax.swing.DefaultListModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
//}}}

public class TagStackModel
	extends DefaultListModel
{
	//{{{ push() method
	public void push(StackPosition pos)
	{
		this.insertElementAt(pos,0);
	} //}}}

	//{{{ pop() method
	public StackPosition pop()
		throws EmptyStackException
	{
		StackPosition pos = null;
		try
		{
			pos = (StackPosition)elementAt(0);
			this.removeElementAt(0);
			return pos;
		}catch(ArrayIndexOutOfBoundsException e)
		{
			throw new EmptyStackException();
		}
	} //}}}

  //{{{ peek() method
  public StackPosition peek()
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
  public StackPosition peek(int index)
    throws ArrayIndexOutOfBoundsException
  {
    StackPosition pos = null;
    pos = (StackPosition)elementAt(index);
    return pos;
  } //}}}

  //{{{ releaseBuffer() method
  public void releaseBuffer(Buffer buffer)
  {
    for(int i=0; i < size(); i++)
    {
      StackPosition p = (StackPosition)elementAt(i);
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
