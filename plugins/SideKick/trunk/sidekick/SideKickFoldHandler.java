/*
 * SideKickFoldHandler.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package sidekick;

//{{{ Imports
import javax.swing.text.Segment;
import javax.swing.tree.TreePath;
import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.Buffer;
//}}}

/**
* Provides a <code>FoldHandler</code> based on the {@link sidekick.Asset}s parsed from the buffer.  Each <code>Asset</code> will become a fold.
 */
public class SideKickFoldHandler extends FoldHandler
{
	//{{{ SideKickFoldHandler constructor
	public SideKickFoldHandler()
	{
		super("sidekick");
	} //}}}

	//{{{ getFoldLevel() method
	public int getFoldLevel(Buffer buffer, int lineIndex, Segment seg)
	{
		if(lineIndex == 0)
			return 0;

		SideKickParsedData data = (SideKickParsedData)buffer.getProperty(
			SideKickPlugin.PARSED_DATA_PROPERTY);
		if(data == null)
			return 0;
		TreePath path = data.getTreePathForPosition(
			buffer.getLineStartOffset(lineIndex) - 1);
		if(path == null)
			return 0;
		else
			return path.getPathCount();
	} //}}}
}
