/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 jEdit contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.remotecontrol.xstream;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPane;

/**
 * @author Matthieu Casanova
 */
public class BufferSetMessage extends EBMessage
{
	public static final String BUFFER_ADDED = "BUFFER_ADDED";
	public static final String BUFFER_REMOVED = "BUFFER_REMOVED";
	public static final String BUFFER_MOVED = "BUFFER_MOVED";
	public static final String BUFFERSET_SORTED = "BUFFERSET_SORTED";

	private final Buffer buffer;
	private int index;

	private int newIndex;

	private final Object what;

	public BufferSetMessage(EditPane editPane, Buffer buffer, int index, int newIndex, Object what)
	{
		super(editPane);
		this.buffer = buffer;
		this.index = index;
		this.newIndex = newIndex;
		this.what = what;
	}

	public BufferSetMessage(EditPane editPane, Buffer buffer, int index, Object what)
	{
		this(editPane, buffer, index, 0, what);
	}

	public BufferSetMessage(EditPane editPane, Object what)
	{
		this(editPane, null, 0, 0, what);
	}

	public EditPane getEditPane()
	{
		return (EditPane) getSource();
	}

	public Buffer getBuffer()
	{
		return buffer;
	}

	public int getIndex()
	{
		return index;
	}

	public int getNewIndex()
	{
		return newIndex;
	}

	public Object getWhat()
	{
		return what;
	}
}
