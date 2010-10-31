/*
 * ChapterHyperlink.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Matthieu Casanova
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

package gatchan.jedit.rfcreader;

import gatchan.jedit.hyperlinks.AbstractHyperlink;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.search.CurrentBufferSet;
import org.gjt.sp.jedit.search.SearchAndReplace;

/**
 * @author Matthieu Casanova
 */
public class ChapterHyperlink extends AbstractHyperlink
{
	private final String search;
	private final String bufferPath;

	public ChapterHyperlink(int start, int end, int startLine, String tooltip, String search, String bufferPath)
	{
		super(start, end, startLine, tooltip);
		this.search = search;
		this.bufferPath = bufferPath;
	}

	public void click(View view)
	{
		Buffer buffer = jEdit.getBuffer(bufferPath);
		if (buffer == null || !buffer.isLoaded())
			return;

		SearchAndReplace.setRegexp(true);
		SearchAndReplace.setSearchString(search);
		SearchAndReplace.setSearchFileSet(new CurrentBufferSet());
		try
		{
			SearchAndReplace.find(view, buffer, getEndOffset(), true, false);
		}
		catch (Exception e)
		{
		}
	}
}
