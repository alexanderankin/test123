/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ctags.sidekick;
import javax.swing.text.Position;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;


public class LinePosition implements Position
{
	int line;
	Buffer buffer = null;
	boolean begin = true;
	boolean eob = false;
	boolean mid = false;
	int linePos;
	
	LinePosition(Buffer buffer)
	{
		this.buffer = buffer;
		eob = true;
	}
	LinePosition(Buffer buffer, int lineNumber, int linePosition)
	{
		this.buffer = buffer;
		line = lineNumber;
		linePos = linePosition;
		mid = true;
	}
	LinePosition(Buffer buffer, int lineNumber, boolean begin)
	{
		this.line = lineNumber;
		this.buffer = buffer;
		this.begin = begin;
	}
	public int getOffset()
	{
		if (buffer != null)
		{
			if (buffer.getLineCount() <= line)
				return 0;
			try
			{
				if (eob)
					return buffer.getLength() - 1;
				if (mid)
					return buffer.getLineStartOffset(line) + linePos;
				if (begin)
					return buffer.getLineStartOffset(line);
				else
					return buffer.getLineEndOffset(line);
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, this, e);
				StringBuffer sb = new StringBuffer(
						"Exception occurred for " +
						buffer.getPath() + ":" + line + " ");
				if (eob)
					sb.append("eob");
				else if (mid)
					sb.append("mid:" + linePos);
				else if (begin)
					sb.append("begin");
				else sb.append("end");
				Log.log(Log.ERROR, this, sb.toString());
			}
		}
		return 0;
	}
}
