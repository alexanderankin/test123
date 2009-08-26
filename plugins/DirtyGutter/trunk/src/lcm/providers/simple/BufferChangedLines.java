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

package lcm.providers.simple;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import lcm.BufferHandler;
import lcm.LCMPlugin;
import lcm.painters.ColoredRectDirtyMarkPainter;
import lcm.painters.DirtyMarkPainter;
import lcm.providers.simple.RangeChangeUndoManager.CompoundChange;
import lcm.providers.simple.RangeChangeUndoManager.DummyNode;
import lcm.providers.simple.RangeChangeUndoManager.RangeAdd;
import lcm.providers.simple.RangeChangeUndoManager.RangeRemove;
import lcm.providers.simple.RangeChangeUndoManager.RangeUpdate;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.BufferUndoListener;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class BufferChangedLines extends BufferAdapter
	implements BufferUndoListener, BufferHandler
{
	private Buffer buffer;
	private TreeSet<Range> ranges;
	private RangeChangeUndoManager undoManager;
	private boolean undoExists;
	private boolean initUndo = false;
	private boolean initRedo = false;
	private ColoredRectDirtyMarkPainter painter = null;

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
			r.update(precedingRange.first, lineDiff);
	}

	public BufferChangedLines(Buffer buffer)
	{
		this.buffer = buffer;
		ranges = new TreeSet<Range>();
		undoManager = new RangeChangeUndoManager(this);
		buffer.addBufferUndoListener(this);
		if (buffer.isDirty() && (! buffer.isUntitled()))
			initDirtyRanges();	// Get the initial dirty ranges using Diff
		painter = new ColoredRectDirtyMarkPainter();
	}

	public void remove()
	{
		buffer.removeBufferUndoListener(this);
		undoManager = null;
	}

	public Buffer getBuffer()
	{
		return buffer;
	}

	/*
	 * This method is called when the plugin starts handling a buffer
	 * that is already dirty. This can happen, for example, if the
	 * plugin is activated via the plugin manager at some point after
	 * buffers have been edited (and marked dirty). In this state,
	 * the plugin does not know which content changes were performed
	 * on the buffer since last save, so it must use 'diff' against
	 * the saved file to find the initial dirty ranges. But this is
	 * not enough: Since the plugin did not handle the buffer before,
	 * it does not know what undo operations are available on it. So
	 * the first "undo" in the current state will not change anything
	 * in the dirty ranges (as the plugin has no "undo list"). To
	 * work around this, the plugin uses a crazy scheme:
	 * 1. Undo all changes in the core's undo list, ignoring any
	 *    content changes made by this undo.
	 * 2. If the resulting buffer is dirty (i.e. undo limit reached
	 *    before saving), run 'diff' to get the initial dirty state.
	 * 3. Finally, redo all changes in the undo list, to get back to
	 *    the current state, this time recording all changes so they
	 *    will be available in the plugin's undo list.
	 */
	private void initDirtyRanges()
	{
		JEditTextArea ta = jEdit.getActiveView().getTextArea();
		// 1. Undo all changes, ignoring content changes
		int undoCount = 0;
		initUndo = true;	// Mark forced full-undo state
		do
		{
			undoExists = false;
			buffer.undo(ta);
			if (undoExists)
				undoCount++;
		}
		while (undoExists);
		initUndo = false;	// Forced full-undo done
		// 2. Run 'diff' against the saved file
		if (buffer.isDirty())
		{
			BufferFileDiff diff = new BufferFileDiff(buffer);
			Vector<Range> dirty = diff.getDiff();
			if (dirty != null)
				ranges.addAll(dirty);
		}
		// 3. Redo all changes, recording undo operations
		initRedo = true;	// Mark forced full-redo state
		for (int i = 0; i < undoCount; i++)
			buffer.redo(ta);
		initRedo = false;	// Forced full-redo done
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
		System.err.println(undoManager.toString());
	}

	public void contentInserted(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
		if (initUndo)	// forced full-undo: ignore change, mark undo existence
		{
			undoExists = true;
			return;
		}
		// ignore undo/redo except forced full-redo
		if ((! initRedo) && buffer.isUndoInProgress())
			return;
		// Check if the inserted range needs to be merged with an existing one
		// Note: numLines==0 for single-line change
		Range changed = new Range(startLine, numLines + 1);
		handleContentChange(numLines, changed);
	}

	public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
		if (initUndo)	// forced full-undo: ignore change, mark undo existence
		{
			undoExists = true;
			return;
		}
		// ignore undo/redo except forced full-redo
		if ((! initRedo) && buffer.isUndoInProgress())
			return;
		// Check if the removed range needs to be merged with an existing one
		if (buffer.getLineStartOffset(startLine) == offset)
			startLine--;
		// Note: numLines==0 for single-line change
		Range changed = new Range(startLine, 1);
		handleContentChange(0 - numLines, changed);
	}

	public void redo(JEditBuffer buffer)
	{
		if (initRedo)
			return;
		undoManager.redo();
		printRanges();
		LCMPlugin.getInstance().repaintAllTextAreas();
	}

	public void undo(JEditBuffer buffer)
	{
		if (initUndo)
			return;
		undoManager.undo();
		printRanges();
		LCMPlugin.getInstance().repaintAllTextAreas();
	}

	/*
	 * Content insertion or removal can trigger many range changes, e.g.
	 * ranges can be removed and a new range is added, so a compound
	 * is created. Some of the changes are calculated now (mergeRanges),
	 * so it's important to perform each change on time, before performing
	 * the calculations. Performing the compound as a whole would result
	 * in incorrect computation.
	 */
	private void handleContentChange(int numLines, Range changed)
	{
		// Use short-paths when possible - see below for cases.
		// In particular, if the changed range is fully contained inside an
		// existing range in the set, there is nothing to do.
		// "ranges.ceiling(changed)" will return:
		// - Last set range overlapping with 'changed', if exists.
		// - Next set range after 'changed', if no overlap.
		// - null, if no overlap and no next range.
		// If the result is an overlapping range, it is the only
		// candidate for full containing 'changed'.
		boolean multiLine = (numLines != 0);
		Range ceiling = ranges.ceiling(changed);
		if ((ceiling == null) || (! ceiling.contains(changed)))
		{
			// 'changed' is not fully contained in any range in the set.
			//
			// A compound change is needed for range updates and range
			// merges. Range updates are needed only for multiple-line
			// changes followed by existing dirty ranges. Range merges
			// are needed for overlaps and also for consecutive ranges.
			boolean updateNeeded = (multiLine && (ceiling != null));
			boolean mergeNeeded = ((ceiling != null) &&
				ceiling.canMerge(changed)); 
			if (! mergeNeeded)
			{
				// No overlap in the set, check consecutive floor.
				Range floor = ranges.floor(changed);
				mergeNeeded = ((floor != null) &&
					floor.consecutive(changed));
			}
			boolean useCompound = (updateNeeded || mergeNeeded);
			CompoundChange compound = null;
			if (useCompound)
			{
				compound = undoManager.new CompoundChange();
				undoManager.add(compound);
				if (updateNeeded)
				{
					RangeUpdate ru = undoManager.new RangeUpdate(changed, numLines);
					ru.redo();
					compound.add(ru);
				}
				if (mergeNeeded)
					mergeRanges(compound, changed);
			}
			RangeAdd ra = undoManager.new RangeAdd(changed);
			ra.redo();
			if (useCompound)
				compound.add(ra);
			else
				undoManager.add(ra);
		}
		else
		{
			// 'changed' is fully contained in a range of the set. Only
			// range updates are needed if this is a multi-line change.
			if (multiLine)
			{
				RangeUpdate ru = undoManager.new RangeUpdate(changed, numLines);
				ru.redo();
				undoManager.add(ru);
			}
			else
			{
				// Add a dummy change, to account for this undoId
				DummyNode dc = undoManager.new DummyNode();
				undoManager.add(dc);
			}
		}
		printRanges();
		LCMPlugin.getInstance().repaintAllTextAreas();
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

	public void bufferSaved(Buffer buffer)
	{
		ranges.clear();
	}

	public DirtyMarkPainter getDirtyMarkPainter(Buffer buffer, int physicalLine)
	{
		if (! isChanged(physicalLine))
			return null;
		painter.setColor(SimpleOptions.getBgColor());
		return painter;
	}
}
