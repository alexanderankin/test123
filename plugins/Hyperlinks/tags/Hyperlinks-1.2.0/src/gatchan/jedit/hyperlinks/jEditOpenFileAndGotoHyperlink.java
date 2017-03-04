/*
 * jEditOpenFileAndGotoHyperlink.java - open file in jEdit and move to a given location
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011-2017 Eric Le Lay
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
package gatchan.jedit.hyperlinks;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;

/**
 * This hyperlink will open a file path in jEdit
 * and move to given location
 * @author Eric Le Lay
 * @version $Id$
 */
public class jEditOpenFileAndGotoHyperlink extends jEditOpenFileHyperlink
{
	public final int gotoLine;
	public final int gotoCol;
	
	public jEditOpenFileAndGotoHyperlink(int start, int end, int line, String url, int gotoLine, int gotoColumn)
	{
		super(start, end, line, url);
		this.gotoLine = gotoLine;
		this.gotoCol = gotoColumn;
	}

	@Override
	public void click(View view)
	{
		Buffer buffer = jEdit.openFile(view, path);
		if(buffer == null)
			return;
		// waiting for complete loading of buffer seems
		// to prevent the NullPointerException
		VFSManager.waitForRequests();
		int line = Math.min(buffer.getLineCount() - 1, gotoLine);
		int column = Math.min(buffer.getLineLength(line), gotoCol);
		int offset = buffer.getLineStartOffset(line) + column;
		// TODO: follow best practice from Navigator plugin
		try
		{
			view.getTextArea().setCaretPosition(offset);
		}
		catch(NullPointerException npe)
		{
			Log.log(Log.ERROR,jEditOpenFileAndGotoHyperlink.class,"FIXME : setCaretPosition("+offset+")");
			Log.log(Log.ERROR,jEditOpenFileAndGotoHyperlink.class,npe);
		}
	}
	
	@Override
	public String toString()
	{
		return "jEditOpenFileAndGotoHyperlink["+path+":"+gotoLine+":"+gotoCol+"]";
	}
}

