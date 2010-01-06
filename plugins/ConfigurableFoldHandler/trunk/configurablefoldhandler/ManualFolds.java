package configurablefoldhandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManualFolds {
	
	private class Range {
		public int start, end;
		public boolean persistent;
		public Range(int start, int end, boolean persistent)
		{
			this.start = start;
			this.end = end;
			this.persistent = persistent;
		}
	}
	private Vector<Range> folds = new Vector<Range>();

	public ManualFolds()
	{
	}

	public void add(int start, int end, boolean persistent)
	{
		folds.add(new Range(start, end, persistent));
	}
	public boolean remove(int line)
	{
		int i = getIndex(line, true);
		if (i < 0)
			return false;
		folds.remove(i);
		return true;
		
	}
	private int getIndex(int line, boolean includeFirst)
	{
		for (int i = 0; i < folds.size(); i++)
		{
			Range fold = folds.get(i);
			int first = fold.start;
			if (includeFirst)
				first--;
			int last = fold.end;
			if (first < line && last >= line)
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
		return (folds.size() == 0);
	}
	public boolean save(String file)
	{
		if (isEmpty())
		{
			File f = new File(file);
			return f.delete();
		}
		BufferedWriter output;
		try
		{
			output = new BufferedWriter(new FileWriter(file));
			try
			{
				for (Range fold: folds)
				{
					if (! fold.persistent)
						continue;
					output.write(String.valueOf(fold.start));
					output.write("\t");
					output.write(String.valueOf(fold.end));
					output.newLine();
				}
			}
			finally
			{
				output.close();
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	public boolean load(String file)
	{
		Pattern rangePattern = Pattern.compile("(\\d+)\\s+(\\d+)");
		BufferedReader input;
		try
		{
			input = new BufferedReader(new FileReader(file));
			try
			{
				String line;
				while ((line = input.readLine()) != null)
				{
					Matcher m = rangePattern.matcher(line);
					if (! m.matches())
						continue;
					try
					{
						int first = Integer.valueOf(m.group(1));
						int last = Integer.valueOf(m.group(2));
						add(first, last, true);
					}
					catch (Exception e)
					{
					}
				}
			}
			finally
			{
				input.close();
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
