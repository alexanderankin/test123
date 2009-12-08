/*
 * DirtyLineProvider - Interface for providers of dirty line information.
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

import org.gjt.sp.jedit.Buffer;

public interface DirtyLineProvider
{
	/*
	 * Attach the dirty line provider to the given buffer.
	 * Returns the buffer listener that updates the buffer's dirty state when
	 * the content is changed.
	 */
	BufferHandler attach(Buffer buffer);
	/*
	 * Detach the buffer listener that was previously returned by 'attach'
	 * from the given buffer.
	 */
	void detach(Buffer buffer, BufferHandler handler);
	/*
	 * Returns the provider-specific options, to be added to the option pane.
	 */
	DirtyLineProviderOptions getOptions();
}
