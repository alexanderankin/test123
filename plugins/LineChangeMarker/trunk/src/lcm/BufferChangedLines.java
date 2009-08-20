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


import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class BufferChangedLines extends BufferAdapter
{
	@SuppressWarnings("unchecked")
	private static class Range implements Comparable
	{
		public int first, last;
		public Range(int first, int num)
		{
			this.first = first;
			last = first + num - 1;
		}
		public int compareTo(Object arg0)
		{
			Range other = (Range) arg0;
			// This check enables to use TreeSet<Range>.contains() on a 1-line
			// range to see if it's in the set.
			if (overlaps(other)) 
				return 0;
			if (first < other.first)
				return (-1);
			if (first > other.first)
				return 1;
			return 0;
		}
		// Returns true if this range overlaps with 'other'.
		private boolean overlaps(Range other)
		{
			/* There are three overlap cases:
			 * 1. First line of this range is inside 'other'.
			 * 2. Last line of this range is inside 'other'.
			 * 3. 'other' is an absolute sub-range of this range.
			 * Case (1) is covered by the first line below.
			 * Cases (2) & (3) are identified by the first line of this range
			 * preceding 'other', and the last line inside 'other' or
			 * following it. 
			 */
			return  (((other.first >= first) && (other.first <= last)) ||
				 ((other.first < first) && (other.last >= first)));
		}
		// Returns true if this range and 'other' are consecutive.
		private boolean consecutive(Range other)
		{
			return ((other.first == last + 1) || (other.last + 1 == first));
		}
		// Returns true if this range and 'other' overlap or are consecutive.
		public boolean canMerge(Range other)
		{
			return overlaps(other) || consecutive(other);
		}
		// Merge 'other' into this range.
		// Note: this.canMerge(other) must be true to call this function.
		public void merge(Range other)
		{
			if (other.first < first)
				first = other.first;
			if (other.last > last)
				last = other.last;
		}
		// Update the line numbers in this range
		public void update(int lineDiff)
		{
			first += lineDiff;
			last += lineDiff;
		}
	}

	Buffer buffer;
	TreeSet<Range> ranges;

	public BufferChangedLines(Buffer buffer)
	{
		this.buffer = buffer;
		ranges = new TreeSet<Range>();
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
		updateRanges(changed, numLines);
		mergeRanges(changed);
		ranges.add(changed);
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
		updateRanges(changed, 0 - numLines);
		mergeRanges(changed);
		ranges.add(changed);
		LCMPlugin.getInstance().repaintAllTextAreas();
		printRanges();
	}

	private void updateRanges(Range changed, int lineDiff)
	{
		if (lineDiff == 0)
			return;
		for (Range r: ranges.tailSet(changed))
			r.update(lineDiff);
	}

	// Merge all mergable ranges with 'changed', removing them from the set.
	private void mergeRanges(Range changed)
	{
		Iterator<Range> it = ranges.iterator();
		boolean found = false;
		while (it.hasNext())
		{
			Range current = it.next();
			if (changed.canMerge(current))
			{
				found = true;
				changed.merge(current);
				it.remove(); // Current item will be replaced by merged range
			}
			else if (found)	// Merge all relevant ranges
				return;
		}
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
