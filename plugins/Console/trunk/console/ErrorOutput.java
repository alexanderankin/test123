/*
 * ErrorOutput.java - Console's error output
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 */

package console;

//{{{ Imports
import java.awt.Color;
import javax.swing.text.AttributeSet;
import org.gjt.sp.util.Log;
//}}}

/**
 * ErrorOutput is a writable Output if it is created by Console or some Output instance.
 * Otherwise (Console or some Output instance is null) ErrorOutput is a view of jEdit's Log
 * with the error urgency (Log.ERROR).
 */

//{{{ class ErrorOutput
public class ErrorOutput implements Output
{
	//{{{ members
	private Output m_output;
	private Color m_color;
	AttributeSet m_set;
	private boolean writeToOutput;
	//}}}
	
	//{{{ constructors
	public ErrorOutput(Console c)
	{
		writeToOutput = c != null;
		
		if (writeToOutput)
		{
			bindToOutput( c.getOutput(), c.getErrorColor() );
		}
	}
	
	public ErrorOutput(Output output, Color errorColor)
	{
		writeToOutput = output != null;
		
		if (writeToOutput)
		{
			bindToOutput(output, errorColor);
		}
	}
	
	private void bindToOutput(Output output, Color errorColor)
	{
		m_output = output;
		m_color  = errorColor;
		m_set    = ConsolePane.colorAttributes(m_color);
	}
	//}}}
	
	//{{{ methods
	public void print(Color color, String msg)
	{
		if (writeToOutput)
		{
			m_output.print(m_color, msg);
		}
		else
		{
			Log.log(Log.ERROR, null, msg);
		}
	}

	public void writeAttrs(AttributeSet attrs, String msg)
	{
		if (writeToOutput)
		{
			m_output.writeAttrs(m_set, msg);
		}
		else
		{
			Log.log(Log.ERROR, null, msg);
		}
	}

	public void setAttrs(int length, AttributeSet attrs)
	{
		// Do nothing.
	}

	public void commandDone()
	{
//		m_output.commandDone();
	}
	//}}}
	
} //}}}
