/*
 * BufferHandler - Interface for buffer change handlers.
 *
 * Copyright (C) 2009 Shlomy Reinstein
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
package lcm;

import lcm.painters.DirtyMarkPainter;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferListener;


public interface BufferHandler extends BufferListener
{
	/*
	 * Returns an object to paint the dirty state of the specified buffer line.
	 */
	DirtyMarkPainter getDirtyMarkPainter(Buffer buffer, int physicalLine);
	/*
	 * Clear the buffer's dirty state when the buffer is saved.
	 */
	void bufferSaved(Buffer buffer);
	/*
	 * Start processing (handler already attached to buffer).
	 */
	void start();
}
