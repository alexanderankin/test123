package console.ssh;

import java.awt.Color;
import org.gjt.sp.jedit.View;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import errorlist.DefaultErrorSource.DefaultError;

public class CommandOutputParser extends console.CommandOutputParser
{
	
	public CommandOutputParser(View v, DefaultErrorSource es, Color defaultColor) {
		super (v, es, defaultColor);
	}

	/**
	 * Process a line of input. Checks all the enabled ErrorMatchers'
	 *  regular expressions, sets the  proper current color,
	 *  changes directories if there are chdir patterns found.
	 * Adds errors to the ErrorList plugin if necessary.
	 *
	 * @param text a line of text
	 * @param disp if true, will also send to the Output.
	 * @return -1 if there is no error, or ErrorSource.WARNING,
	 *       or ErrorSource.ERROR if there is a warning or an error found in text.
	 */
	public int processLine(String text, boolean disp)
	{
		int retval = super.processLine(text, disp);
		if (retval == -1) {
			return retval;
		}
		else {
			String fn = lastError.getFileName();
			ConsoleState cs = ConnectionManager.getConsoleState(console);
			
			String path = lastError.getFilePath();
			int type = lastError.getErrorType();
			int line = lastError.getLineNumber();
			int start = lastError.getStartOffset();
			int end = lastError.getEndOffset();
			String msg = lastError.getErrorMessage();
			ErrorSource errorSource = lastError.getErrorSource();

			DefaultError de = new DefaultError(errorSource, type, path, line, start, end, msg  ); 	
			errorSource.addError(de);
			ErrorSource.registerErrorSource(errorSource);
			return retval;
		}
	}
	// }}}
	

}
