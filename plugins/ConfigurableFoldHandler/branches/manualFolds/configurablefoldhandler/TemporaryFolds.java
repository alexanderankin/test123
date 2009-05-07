package configurablefoldhandler;

import java.util.Vector;

public class TemporaryFolds {
	private Vector<Integer> start = new Vector<Integer>();
	private Vector<Integer> end = new Vector<Integer>();
	
	public TemporaryFolds()
	{
	}
	public void add(int start, int end)
	{
		this.start.add(start);
		this.end.add(end);
	}
	public boolean remove(int line)
	{
		int i = getIndex(line, true);
		if (i < 0)
			return false;
		start.remove(i);
		end.remove(i);
		return true;
		
	}
	private int getIndex(int line, boolean includeFirst)
	{
		for (int i = 0; i < start.size(); i++)
		{
			int first = start.get(i);
			if (includeFirst)
				first--;
			int last = end.get(i);
			if (first < line && last > line)
				return i;
		}
		return -1;
	}
	private int getIndex(int line)
	{
		return getIndex(line, false);
	}
	public boolean isFold(int line)
	{
		return getIndex(line) >= 0;
	}
	public boolean isEmpty()
	{
		return (start.size() == 0);
	}
}
