/*
 * BufferOutput.java - Output to buffer implementation
 * Copyright (C) 2001 Slava Pestov
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

package console;

import javax.swing.text.BadLocationException;
import javax.swing.SwingUtilities;
import java.awt.Color;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class BufferOutput implements Output
{
	public BufferOutput(View view)
	{
		this.view = view;
		buf = new StringBuffer();
	}

	public void print(Color color, String msg)
	{
		buf.append(msg);
		buf.append('\n');
	}

	public void commandDone()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Buffer buffer = jEdit.newFile(view);
				try
				{
					buffer.insertString(0,buf.toString(),null);
				}
				catch(BadLocationException e)
				{
					Log.log(Log.ERROR,this,e);
				}
			}
		});
	}

	// private members
	private View view;
	private StringBuffer buf;
}
