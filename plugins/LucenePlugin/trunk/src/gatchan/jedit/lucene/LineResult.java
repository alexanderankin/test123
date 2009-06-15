package gatchan.jedit.lucene;

public class LineResult extends Result
{
	public int getLine()
	{
		return Integer.valueOf(getDocument().getField("line").stringValue());
	}
}
