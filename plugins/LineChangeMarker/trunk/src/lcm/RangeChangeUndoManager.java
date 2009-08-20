package lcm;

import java.util.Vector;

public class RangeChangeUndoManager
{
	// Keep a dummy node at the beginning of the list
	RangeChange listStart = new DummyNode();
	// Head points at the node for next Undo
	RangeChange head = listStart;
	// The range set
	private BufferChangedLines bcl;

	public RangeChangeUndoManager(BufferChangedLines bcl)
	{
		this.bcl = bcl;
	}

	public void add(RangeChange op)
	{
		op.prev = head;
		op.next = null;
		head.next = op;
		head = op;
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

	public abstract class RangeChange
	{
		public RangeChange prev = null, next = null;
		abstract void undo();
		abstract void redo();
	}

	private class DummyNode extends RangeChange
	{
		@Override
		public void redo()
		{
		}
		@Override
		public void undo()
		{
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
	}

	public class RangeUpdate extends RangeChange
	{
		private int lineDiff;
		private Range precedingRange;
		public RangeUpdate(Range r, int diff)
		{
			precedingRange = r;
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
	}

	public class CompoundChange extends RangeChange
	{
		Vector<RangeChange> operations = new Vector<RangeChange>();
		public void add(RangeChange op)
		{
			operations.add(op);
		}
		@Override
		void redo()
		{
			for (RangeChange op: operations)
				op.redo();
		}
		@Override
		void undo()
		{
			for (int i = operations.size() - 1; i >= 0; i--)
				operations.get(i).undo();
		}
	}
}
