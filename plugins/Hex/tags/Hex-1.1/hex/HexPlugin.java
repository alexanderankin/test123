/*
 * HexPlugin.java
 * Copyright (c) 2000, 2001, 2002 Andre Kaplan
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


package hex;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.msg.BufferUpdate;


public class HexPlugin extends EditPlugin
{
	public void start()
	{
		EditBus.addToBus(this);
	}


	public void stop()
	{
		EditBus.removeFromBus(this);
	}

	@EditBus.EBHandler
	public void handleBufferUpdate(BufferUpdate bu)
	{
		if (bu.getWhat() == BufferUpdate.LOADED)
		{
			Buffer buffer = bu.getBuffer();
			VFS vfs = buffer.getVFS();
			Mode mode = null;
			if (vfs instanceof HexVFS)
			{
				mode = jEdit.getMode("hex");
				if (mode == null)
				{
					mode = jEdit.getMode("text");
				}
			}

			if ((mode != null) && (mode != buffer.getMode()))
			{
				buffer.setMode(mode);
			}
		}
	}
}

