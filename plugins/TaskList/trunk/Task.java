/*
 * Task.java - TaskList plugin
 * Copyright (C) 2001 Oliver Rutherfurd
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


import javax.swing.Icon;
import javax.swing.text.*;

import org.gjt.sp.jedit.Buffer;

import org.gjt.sp.util.Log;

public class Task
{

	public Task(Buffer buffer, Icon icon, int line, String text,
		int startOffset, int endOffset)
	{
		// Log.log(Log.DEBUG, Task.class,
		// 	"Task.Task(buffer=" + buffer + ",icon=" + icon + ",line=" + line +
		// 	",startOffset=" + startOffset + ",endOffset=" + endOffset +
		// 	",text=" + text + ")");//##

		this.buffer = buffer;
		this.icon = icon;
		this.line = line;
		this.text = text.replace('\t', (char)187);
		this.startOffset = startOffset;
		this.endOffset = endOffset;

	}

	public Buffer getBuffer(){ return this.buffer; }
	public Icon getIcon(){ return this.icon; }
	public String getText(){ return this.text; }
	public int getLine(){ return this.line; }

	public int getStartOffset()
	{
		return startOffset;
	}

	public int getEndOffset()
	{
		return endOffset;
	}

	public String toString()
	{
		return "[" + this.line + "]" + this.text;
	}

	private Buffer buffer;
	private Icon icon;

	private String text;

	private int startOffset;
	private int endOffset;

	private int line;

}
