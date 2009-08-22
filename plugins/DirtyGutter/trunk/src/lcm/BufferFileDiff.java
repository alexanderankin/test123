package lcm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import jdiff.util.Diff;
import jdiff.util.Diff.Change;

import org.gjt.sp.jedit.Buffer;

public class BufferFileDiff
{
	private Buffer b;

	public BufferFileDiff(Buffer b)
	{
		this.b = b;
	}

	public Vector<Range> getDiff()
	{
		int nBuffer = b.getLineCount();
		String [] bufferLines = new String[nBuffer];
		for (int i = 0; i < nBuffer; i++)
			bufferLines[i] = b.getLineText(i);
		String [] fileLines = readFile(b.getPath());
		Diff diff = new Diff(fileLines, bufferLines);
		Change edit = diff.diff_2();
		Vector<Range> ranges = new Vector<Range>();
		for (; edit != null; edit = edit.next)
			ranges.add(new Range(edit.first1, edit.lines1));
		return ranges;
	}

	private String[] readFile(String path)
	{
		BufferedReader in;
		try
		{
			in = new BufferedReader(new FileReader(path));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		String [] ret = null;
		Vector<String> lines = new Vector<String>();
		try
		{
			String line;
			while ((line = in.readLine()) != null)
				lines.add(line);
			ret = new String[lines.size()];
			lines.toArray(ret);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
}
