/*
 * BufferFileDiff - Diffs the buffer with the saved file.
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
package lcm.providers.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import jdiff.util.Diff;
import jdiff.util.Diff.Change;
import lcm.LCMPlugin;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;


@SuppressWarnings("unused")
public class BufferFileDiff
{
	private Buffer b;

	public BufferFileDiff(Buffer b)
	{
		this.b = b;
	}

	public Vector<Range> getDiff()
	{
		int nBuffer = b.getLineCount();
		String [] bufferLines = new String[nBuffer];
		for (int i = 0; i < nBuffer; i++)
			bufferLines[i] = b.getLineText(i);
		String [] fileLines = LCMPlugin.getInstance().readFile(b.getPath());
		if (fileLines == null)
			return null;
		Diff diff = new Diff(fileLines, bufferLines);
		Change edit = diff.diff_2();
		Vector<Range> ranges = new Vector<Range>();
		for (; edit != null; edit = edit.next)
			ranges.add(new Range(edit.first1, edit.lines1));
		return ranges;
	}

}
