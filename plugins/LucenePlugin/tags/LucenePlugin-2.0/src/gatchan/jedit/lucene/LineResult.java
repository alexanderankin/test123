package gatchan.jedit.lucene;

public class LineResult extends Result
{
	public int getLine()
	{
		return Integer.valueOf(getDocument().getField("line").stringValue());
	}

	public String getText()
	{
		return getDocument().getField("content").stringValue();
	}

	public String toString()
	{
		return super.toString() + ":" + getLine() + " - " + getText();
	}
}
