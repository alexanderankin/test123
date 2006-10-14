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


public class LinePosition implements Position
{
	int line;
	Buffer buffer = null;
	boolean begin = true;
	boolean eob = false;
	
	LinePosition(Buffer buffer)
	{
		this.buffer = buffer;
		eob = true;
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
			try
			{
				if (eob)
					return buffer.getLineEndOffset(buffer.getLineCount() - 1);
				if (begin)
					return buffer.getLineStartOffset(line);
				else
					return buffer.getLineEndOffset(line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return 0;
	}
}
