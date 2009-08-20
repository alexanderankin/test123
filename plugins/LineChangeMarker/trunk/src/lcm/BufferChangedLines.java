/*
 * BufferChangedLines - Stores ranges of lines changed in a buffer.
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
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import lcm.RangeChangeUndoManager.CompoundChange;
import lcm.RangeChangeUndoManager.RangeAdd;
import lcm.RangeChangeUndoManager.RangeRemove;
import lcm.RangeChangeUndoManager.RangeUpdate;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class BufferChangedLines extends BufferAdapter
{
	Buffer buffer;
	TreeSet<Range> ranges;
	RangeChangeUndoManager undoManager;

	// Methods for supporting undo
	public void add(Range r)
	{
		ranges.add(r);
	}
	public void remove(Range r)
	{
		ranges.remove(r);
	}
	public void updateRanges(Range precedingRange, int lineDiff)
	{
		if (lineDiff == 0)
			return;
		for (Range r: ranges.tailSet(precedingRange))
			r.update(lineDiff);
	}

	public BufferChangedLines(Buffer buffer)
	{
		this.buffer = buffer;
		ranges = new TreeSet<Range>();
		undoManager = new RangeChangeUndoManager(this);
		buffer.addBufferListener(this);
	}

	private void printRanges()
	{
		if (! LCMPlugin.getInstance().isDebugging())
			return;
		StringBuilder sb = new StringBuilder("Ranges: ");
		Iterator<Range> it = ranges.iterator();
		while (it.hasNext())
		{
			Range current = it.next();
			sb.append("[" + current.first + "-" + current.last + "] ");
		}
		System.err.println(sb.toString());
	}

	@Override
	public void contentInserted(JEditBuffer buffer, int startLine, int offset,
			int numLines, int length)
	{
		// Check if the inserted range needs to be merged with an existing one
		// Note: numLines==0 for single-line change
		Range changed = new Range(startLine, numLines + 1);
		// Content insertion can trigger many range changes, so a compound
		// is created. Some of the changes are calculated now (mergeRanges),
		// so it's important to perform each change before performing the
		// calculations - cannot just 
		CompoundChange compound = undoManager.new CompoundChange();
		RangeUpdate ru = undoManager.new RangeUpdate(changed, numLines);
		ru.redo();
		compound.add(ru);
		mergeRanges(compound, changed);
		RangeAdd ra = undoManager.new RangeAdd(changed);
		ra.redo();
		compound.add(ra);
		undoManager.add(compound);
		printRanges();
	}

	@Override
	public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
			int numLines, int length)
	{
		// Check if the removed range needs to be merged with an existing one
		if (buffer.getLineStartOffset(startLine) == offset)
			startLine--;
		// Note: numLines==0 for single-line change
		Range changed = new Range(startLine, 1);
		CompoundChange compound = undoManager.new CompoundChange();
		RangeUpdate ru = undoManager.new RangeUpdate(changed, 0 - numLines);
		ru.redo();
		compound.add(ru);
		mergeRanges(compound, changed);
		RangeAdd ra = undoManager.new RangeAdd(changed);
		ra.redo();
		compound.add(ra);
		undoManager.add(compound);
		printRanges();
	}

	/*
	 * Merge all mergable ranges with 'changed', removing them from the set.
	 * Update 'changed' to the merged range. Ranges merged into 'changed' are
	 * removed from the set, which will eventually be added to replace them.
	 * The range removals should be recorded; the merge operation does not,
	 * since eventually the addition of updated 'changed' range will be
	 * recorded with the correct value.
	 */
	private void mergeRanges(CompoundChange compound, Range changed)
	{
		Iterator<Range> it = ranges.iterator();
		boolean found = false;
		// Defer removals until the iteration is over, to prevent
		// ConcurrentModificationException.
		Vector<RangeRemove> removals = new Vector<RangeRemove>();
		while (it.hasNext())
		{
			Range current = it.next();
			if (changed.canMerge(current))
			{
				found = true;
				changed.merge(current);
				// Current item will be replaced by merged range
				RangeRemove rr = undoManager.new RangeRemove(current);
				compound.add(rr);
				removals.add(rr);
			}
			else if (found)	// Merge all relevant ranges
				break;
		}
		// Perform the deferred removals.
		for (RangeRemove rr: removals)
			rr.redo();
	}

	public boolean isChanged(int line)
	{
		Range r = new Range(line, 1);
		return ranges.contains(r);
	}

	public void clear()
	{
		ranges.clear();
	}
}
