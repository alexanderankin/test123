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
	private boolean recordUndo = false;
	private boolean disableUndo = false;
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
	 * This method is used to initialize the undo history for the buffer.
	 * This is needed if the plugin is activated via the plugin manager
	 * at some point after buffers have been edited (so they have an
	 * undo list). To fully support undo/redo, the plugin must follow the
	 * existing undo list to build the required undo data. If the buffer's
	 * non-dirty state is somewhere along the undo list, then the undo
	 * data has to be built in both directions from that position in the
	 * undo list. Otherwise, the plugin must use 'diff' against the saved
	 * file at the beginning of the undo list to find the initial dirty
	 * ranges.
	 * This is how to build the required undo information:
	 * - Disable auto-save.
	 * - Find the position of the buffer's non-dirty state in the core's
	 *   undo list. This is done by going backward all way (undo) and then
	 *   forward all way (redo) until the non-dirty state is reached or
	 *   the undo list is exhausted.
	 * - If the non-dirty state was not reached:
	 *   - Undo all changes.
	 *   - Diff the buffer vs. the saved file to initialize the dirty ranges.
	 *   Else: (the buffer's non-dirty state was reached)
	 *   - Undo all changes, building undo information.
	 *   - Reverse the list of undo information built.
	 *   - Redo all changes until the buffer's non-dirty state is reached.
	 * - Redo all changes, building undo information.
	 * - Undo some operations again to get back where we started.
	 * - Restore auto-save.
	 */
	private void initHistory()
	{
		if (jEdit.getActiveView() == null)
			return;
		JEditTextArea ta = jEdit.getActiveView().getTextArea();
		// Look for the non-dirty state of the buffer in the undo list.
		// While at it, find the current position in the undo list.
		disableUndo = true;		// Disable undo listeners
		int position = 0;
		while (buffer.isDirty() && buffer.canUndo())
		{
			buffer.undo(ta);
			position++;
		}
		boolean positionFound = buffer.isDirty();
		while (buffer.isDirty() && buffer.canRedo())
			buffer.redo(ta);
		// If the non-dirty state was not found, rewind and diff.
		int listSize = 0;
		if (buffer.isDirty())
		{
			while (buffer.canUndo())
				buffer.undo(ta);
			BufferFileDiff diff = new BufferFileDiff(buffer);
			Vector<Range> dirty = diff.getDiff();
			if (dirty != null)
				ranges.addAll(dirty);
		}
		else	// Build backward undo information, count steps 
		{
			recordUndo = true;
			while (buffer.canUndo())
			{
				buffer.undo(ta);
				if (! positionFound)	// Still on way back in undo 
					position++;
			}
			recordUndo = false;
			undoManager.reverse();
			// Get back to the non-dirty state
			while (buffer.isDirty() && buffer.canRedo())
			{
				buffer.redo(ta);
				listSize++;
			}
			// Clear the dirty line state, we're now in non-dirty state.
			bufferSaved(buffer);
		}
		// Build forward undo information, count steps
		recordUndo = true;
		while (buffer.canRedo())
		{
			buffer.redo(ta);
			listSize++;
		}
		recordUndo = false;
		// Finally, go back to initial buffer state
		int stepsBack = listSize - position;
		for (int i = 0; i < stepsBack; i++)
			buffer.undo(ta);
		undoManager.rewind(stepsBack);
		disableUndo = false;	// Enable undo listeners
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
		if ((! recordUndo) && buffer.isUndoInProgress())
			return;
		// Check if the inserted range needs to be merged with an existing one
		// Note: numLines==0 for single-line change
		Range changed = new Range(startLine, numLines + 1);
		handleContentChange(numLines, changed);
	}

	public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
		if ((! recordUndo) && buffer.isUndoInProgress())
			return;
		// Check if the removed range needs to be merged with an existing one
		if (buffer.getLineStartOffset(startLine) == offset)
			startLine--;
		// Note: numLines==0 for single-line change
		Range changed = new Range(startLine, 1);
		handleContentChange(0 - numLines, changed);
	}

	public void beginRedo(JEditBuffer buffer)
	{
	}

	public void endRedo(JEditBuffer buffer)
	{
		if (disableUndo)
			return;
		undoManager.redo();
		printRanges();
		LCMPlugin.getInstance().repaintAllTextAreas();
	}

	public void beginUndo(JEditBuffer buffer)
	{
	}

	public void endUndo(JEditBuffer buffer)
	{
		if (disableUndo)
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
		undoManager.setCleanState();
	}

	public DirtyMarkPainter getDirtyMarkPainter(Buffer buffer, int physicalLine)
	{
		if (! isChanged(physicalLine))
			return null;
		painter.setColor(SimpleOptions.getBgColor());
		return painter;
	}

	public void start()
	{
		initHistory();
		printRanges();
		LCMPlugin.getInstance().repaintAllTextAreas();
	}
}
