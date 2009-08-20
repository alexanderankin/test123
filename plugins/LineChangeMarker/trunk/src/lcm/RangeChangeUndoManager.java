package lcm;

import java.util.Vector;

public class RangeChangeUndoManager
{
	// Keep two dummy nodes at the beginning and end of the list
	static RangeChange listStart = new DummyNode();
	// Head points at the node for next Undo
	static RangeChange head = listStart;

	public static void add(RangeChange op)
	{
		op.prev = head;
		op.next = null;
		head.next = op;
		head = op;
	}

	public static void undo()
	{
		if (head == listStart)	// Nothing to undo
			return;
		head.undo();
		head = head.prev;
	}
	
	public static void redo()
	{
		if (head.next == null)	// Nothing to redo
			return;
		head = head.next;
		head.redo();
	}

	public static abstract class RangeChange
	{
		public RangeChange prev = null, next = null;
		protected BufferChangedLines bcl;
		public RangeChange()
		{
			this(null);
		}
		public RangeChange(BufferChangedLines bcl)
		{
			this.bcl = bcl;
		}
		abstract void undo();
		abstract void redo();
	}

	private static class DummyNode extends RangeChange
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

	public static class RangeAdd extends RangeChange
	{
		private Range r;
		public RangeAdd(BufferChangedLines bcl, Range r)
		{
			super(bcl);
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

	public static class RangeRemove extends RangeChange
	{
		private Range r;
		public RangeRemove(BufferChangedLines bcl, Range r)
		{
			super(bcl);
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

	public static class RangeUpdate extends RangeChange
	{
		private int lineDiff;
		private Range precedingRange;
		public RangeUpdate(BufferChangedLines bcl, Range r, int diff)
		{
			super(bcl);
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

	public static class CompoundChange extends RangeChange
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
