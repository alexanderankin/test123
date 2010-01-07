package cppcheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.View;

import cppcheck.Runner.LineHandler;

import errorlist.ErrorSource;

public class ErrorHandler implements LineHandler
{
	private static Pattern error = Pattern.compile(
		"\\[([^\\]]+)\\]:\\s+\\(error\\)\\s+(.*)");
	//[CodeLite\FlexLexer.h:77]: (error) Invalid number of character ({). Can't process file.

	public ErrorHandler(View view)
	{
	}

	public void handle(String line)
	{
		Matcher m = error.matcher(line);
		if (m.find())
		{
			String where = m.group(1);
			int index = where.lastIndexOf(':');
			String file = where.substring(0, index);
			String lineNum = where.substring(index + 1);
			Plugin.getErrorSource().addError(ErrorSource.ERROR, file,
				Integer.valueOf(lineNum).intValue(), 0, 0, m.group(2));
		}
	}

	public void start(String path)
	{
	}

	public void end(String path)
	{
	}
}
