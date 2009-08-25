/*
 * DiffBufferHandler - A diff-based buffer change handler.
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

import java.awt.Color;
import java.util.TreeMap;
import java.util.Vector;

import jdiff.util.Diff;
import jdiff.util.Diff.Change;

import lcm.BufferHandler;
import lcm.LCMPlugin;
import lcm.painters.ColoredRectDirtyMarkPainter;
import lcm.painters.DirtyMarkPainter;
import lcm.providers.diff.Range.ChangeType;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class DiffBufferHandler extends BufferAdapter implements BufferHandler
{
	private Buffer buffer;
	private TreeMap<Range, Range> ranges;
	private ColoredRectDirtyMarkPainter painter;

	public DiffBufferHandler(Buffer buffer)
	{
		this.buffer = buffer;
		ranges = new TreeMap<Range, Range>();
		painter = new ColoredRectDirtyMarkPainter();
	}

	@Override
	public void contentInserted(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
		handleContentChange(startLine, numLines, ChangeType.ADDED);
	}

	@Override
	public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
		handleContentChange(startLine, 0 - numLines, ChangeType.REMOVED);
	}

	private void handleContentChange(int startLine, int numLines,
		ChangeType change)
	{
		if (numLines == 0)
			addLine(startLine, change);
		else
			doDiff();
		LCMPlugin.getInstance().repaintAllTextAreas();
	}

	private void doDiff()
	{
		ranges = new TreeMap<Range, Range>();
		int nBuffer = buffer.getLineCount();
		String [] bufferLines = new String[nBuffer];
		for (int i = 0; i < nBuffer; i++)
			bufferLines[i] = buffer.getLineText(i);
		String [] fileLines = LCMPlugin.getInstance().readFile(
			buffer.getPath());
		if (fileLines == null)
			return;
		Diff diff = new Diff(fileLines, bufferLines);
		Change edit = diff.diff_2();
		for (; edit != null; edit = edit.next)
		{
			Range range = new Range(edit.first1, edit.lines1, ChangeType.CHANGED);
			ranges.put(range, range);
		}
	}

	public void addLine(int startLine, ChangeType change)
	{
		Range r = new Range(startLine, 1, change);
		Range current = ranges.get(r);
		if (current == null)
		{
			ranges.put(r, r);
			return;
		}
		if (current.type != change)
			current.type = ChangeType.CHANGED;
	}

	public void bufferSaved(Buffer buffer)
	{
		ranges.clear();
	}

	public DirtyMarkPainter getDirtyMarkPainter(Buffer buffer, int physicalLine)
	{
		Range r = ranges.get(new Range(physicalLine, 1));
		if (r == null)
			return null;
		Color c;
		if (r.type == ChangeType.ADDED)
			c = Color.GREEN;
		else if (r.type == ChangeType.REMOVED)
			c = Color.RED;
		else if (r.type == ChangeType.CHANGED)
			c = Color.ORANGE;
		else
			return null;
		painter.setColor(c);
		return painter;
	}

}
