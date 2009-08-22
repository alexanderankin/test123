/*
 * RangeChangeUndoManager - Undo handler for the range list of each buffer.
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

public class RangeChangeUndoManager
{
	// Keep a dummy node at the beginning of the list
	RangeChange listStart = new DummyNode();
	// Head points at the node for next Undo
	RangeChange head = listStart;
	// The range set
	private BufferChangedLines bcl;
	// Last undo id - for merging RangeChange objects of the same undo
	private Object lastUndoId = null;

	public RangeChangeUndoManager(BufferChangedLines bcl)
	{
		this.bcl = bcl;
	}

	public void add(RangeChange op)
	{
		Object undoId = bcl.buffer.getUndoId();
		// If 'op' is added to the same undoId as the previous, merge them
		if (undoId == lastUndoId)
		{
			if (op instanceof DummyNode)	// This undo already has an op
				return;
			if (head instanceof DummyNode)	// Dummy node can be replaced
			{
				if (! (op instanceof DummyNode))
				{
					if (head != listStart)
						head = head.prev;
					head.append(op);
					head = op;
				}
				return;
			}
			// Put both under a compound change
			CompoundChange cc;
			if (! (head instanceof CompoundChange))
			{
				cc = new CompoundChange();
				head.prev.append(cc);
				cc.add(head);
				head = cc;
			}
			else
				cc = (CompoundChange) head;
			cc.add(op);
		}
		else
		{
			lastUndoId = undoId;
			head.append(op);
			head = op;
		}
	}

	public void undo()
	{
		if (head == listStart)	// Nothing to undo
			return;
		head.undo();
		head = head.prev;
	}
	
	public void redo()
	{
		if (head.next == null)	// Nothing to redo
			return;
		head = head.next;
		head.redo();
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder("Undo: ");
		RangeChange rc = listStart.next;
		while (rc != null)
		{
			if (rc == head)
				sb.append("-->");
			sb.append(rc.toString());
			rc = rc.next;
			if (rc != null)
				sb.append(",");
		}
		return sb.toString();
	}

	public abstract class RangeChange
	{
		public RangeChange prev = null, next = null;
		abstract void undo();
		abstract void redo();
		public void append(RangeChange op)
		{
			op.prev = this;
			op.next = null;
			next = op;
		}
	}

	public class DummyNode extends RangeChange
	{
		@Override
		public void redo()
		{
		}
		@Override
		public void undo()
		{
		}
		public String toString()
		{
			return "Dummy";
		}
	}

	public class RangeAdd extends RangeChange
	{
		private Range r;
		public RangeAdd(Range r)
		{
			this.r = r;
		}
		@Override
		public void undo()
		{
			bcl.remove(r);
		}
		@Override
		public void redo()
		{
			bcl.add(r);
		}
		public String toString()
		{
			return "Add(" + r.first + "-" + r.last + ")";
		}
	}

	public class RangeRemove extends RangeChange
	{
		private Range r;
		public RangeRemove(Range r)
		{
			this.r = r;
		}
		@Override
		public void undo()
		{
			bcl.add(r);
		}
		@Override
		public void redo()
		{
			bcl.remove(r);
		}
		public String toString()
		{
			return "Remove(" + r.first + "-" + r.last + ")";
		}
	}

	public class RangeUpdate extends RangeChange
	{
		private int lineDiff;
		private Range precedingRange;
		public RangeUpdate(Range r, int diff)
		{
			precedingRange = new Range(r);	// Make a copy, 'r' can change...
			lineDiff = diff;
		}
		@Override
		public void undo()
		{
			bcl.updateRanges(precedingRange, 0 - lineDiff);
		}
		@Override
		public void redo()
		{
			bcl.updateRanges(precedingRange, lineDiff);
		}
		public String toString()
		{
			return "Update(" + precedingRange.first + "-" + precedingRange.last +
				"," + lineDiff + ")";
		}
	}

	public class CompoundChange extends RangeChange
	{
		RangeChange first = null, last = null;
		public void add(RangeChange op)
		{
			if (first == null)
				first = op;
			else
				last.append(op);
			last = op;
		}
		@Override
		void redo()
		{
			for (RangeChange op = first; op != null; op = op.next)
				op.redo();
		}
		@Override
		void undo()
		{
			for (RangeChange op = last; op != null; op = op.prev)
				op.undo();
		}
		public String toString()
		{
			StringBuilder sb = new StringBuilder("Compound:[");
			for (RangeChange op = first; op != null; op = op.next)
			{
				if (op != first)
					sb.append(",");
				sb.append(op.toString());
			}
			sb.append("]");
			return sb.toString();
		}
	}
}
