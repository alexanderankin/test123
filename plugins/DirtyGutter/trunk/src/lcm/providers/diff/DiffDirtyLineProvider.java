/*
 * DiffDirtyLineProvider - A diff-based dirty line provider.
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
package lcm.providers.diff;
import org.gjt.sp.jedit.Buffer;

import lcm.BufferHandler;
import lcm.DirtyLineProvider;


public class DiffDirtyLineProvider implements DirtyLineProvider
{

	public BufferHandler attach(Buffer buffer)
	{
		return new DiffBufferHandler(buffer);
	}

	public void detach(Buffer buffer, BufferHandler handler)
	{
	}

}
