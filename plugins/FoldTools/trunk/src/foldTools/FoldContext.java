package foldTools;

import java.util.ArrayList;
import java.util.HashSet;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class FoldContext
{
	public class LineContext
	{
		public int line;
		public String text;
		public LineContext(int line, String text)
		{
			this.line = line;
			this.text = text;
		}
	}

	private JEditTextArea textArea;
	private Buffer buffer;
	// fold line numbers from highest to lowest
	private ArrayList<Integer> context = new ArrayList<Integer>();
	private ArrayList<LineContext> lines = new ArrayList<LineContext>();
	private HashSet<Integer> printed = new HashSet<Integer>();

	public FoldContext(View view)
	{
		update(view);
	}

	public void update(View view)
	{
		textArea = view.getTextArea();
		buffer = view.getBuffer();
		context.clear();
		lines.clear();
		printed.clear();
		int line = textArea.getCaretLine();
		int foldLevel = buffer.getFoldLevel(line);
		int i = line - 1;
		while ((i >= 0) && (foldLevel > 0))
		{
			for (; i >= 0; i--)
			{
				int prevFoldLevel = buffer.getFoldLevel(i);
				if (prevFoldLevel < foldLevel)
				{
					context.add(Integer.valueOf(i));
					foldLevel = prevFoldLevel;
					break;
				}
			}
		}
	}

	private LineContext getLine(int line)
	{
		printed.add(line);
		return new LineContext(line, textArea.getLineText(line));
	}
	private ArrayList<LineContext> getContext(int line)
	{
		ArrayList<LineContext> localContext = new ArrayList<LineContext>();
		if (line > 0)
			localContext.add(getLine(line - 1));
		if (! printed.contains(line))
		{
			localContext.add(getLine(line));
			if (! (printed.contains(line + 1) || (line >= buffer.getLineCount() - 1)))
			{
				localContext.add(getLine(line + 1));
				if (! (printed.contains(line + 2) || (line >= buffer.getLineCount() - 2)))
					localContext.add(new LineContext(line + 2, "\t..."));
			}
		}
		return localContext;
	}
	private void getContextLines()
	{
		if (! lines.isEmpty())
			return;
		for (int line: context)
		{
			ArrayList<LineContext> localContext = getContext(line);
			lines.addAll(0, localContext);
		}
		return;
	}

	public String toString()
	{
		getContextLines();
		StringBuilder sb = new StringBuilder();
		for (LineContext lc: lines)
			sb.append((lc.line + 1) + ":\t" + lc.text + "\n");
		return sb.toString();
	}

	public String toLabelString()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
