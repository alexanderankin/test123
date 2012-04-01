package console;

import java.awt.Color;

import javax.swing.text.AttributeSet;

public class ErrorOutput implements Output
{

	private Output m_output;
	private Color m_color;
	AttributeSet m_set;

	public ErrorOutput(Console c) {
		m_output = c.getOutput();
		m_color = c.getErrorColor();
		m_set = ConsolePane.colorAttributes(m_color);	
	}
	
	public ErrorOutput(Output output, Color errorColor)
	{
		m_output = output;
		m_color = errorColor;
		m_set = ConsolePane.colorAttributes(m_color);
	}

	public void print(Color color, String msg)
	{
		m_output.print(m_color, msg);

	}

	public void writeAttrs(AttributeSet attrs, String msg)
	{
		m_output.writeAttrs(m_set, msg);
	}

	public void setAttrs(int length, AttributeSet attrs)
	{
		// Do nothing.
	}

	public void commandDone()
	{
//		m_output.commandDone();
	}

}
