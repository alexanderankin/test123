package console;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.View;

import errorlist.DefaultErrorSource;
/**
 * Replaces the StreamThread classes. Parses the output of a running Process.
 * 
 * @author ezust
 * @since Java 1.5, Jedit 4.3
 * 
 */

public class CommandOutputParserThread extends Thread
{

	DirectoryStack directoryStack = new DirectoryStack();

	private DefaultErrorSource.DefaultError lastError = null;

	InputStream stdout;

//	ConsoleInputStream sreader;
	InputStreamReader reader;
	BufferedReader breader;

	View view;

	DefaultErrorSource errorSource;

	ErrorMatcher[] errorMatchers;

	ErrorMatcher lastMatcher;

	ConsoleProcess consoleProcess;
	Console console;
//	ScreenFilterReader screenFilter;

	Color color;
	boolean done;

	public CommandOutputParserThread(View v, ConsoleProcess consoleP,
			DefaultErrorSource es)
	{
		consoleProcess = consoleP;
		console = consoleProcess.getConsole();
		color = console.getInfoColor();
		lastMatcher = null;
		done = false;
		view = v;
		errorSource = es;
		/*
		 * errorSource=new DefaultErrorSource(name);
		 * errorSource.registerErrorSource(errorSource);
		 */
		errorMatchers = ConsolePlugin.getErrorMatchers();

		stdout = consoleP.getMergedOutputs();
		reader = new InputStreamReader(stdout);
		breader = new BufferedReader(reader, 80);
	}

	public Color getColor() {
		return color;
	}
	
	public void setDirectory(String currentDirectory)
	{
		directoryStack.push(currentDirectory);
	}

	static final Pattern newLine = Pattern.compile("\r?\n");
	protected void display(Color c, String text) {
		if (text == null) return;
	//	consoleProcess.getOutput().writeAttrs(ConsolePane.colorAttributes(c),  text + "\n" );
		
	}
	
	protected void display(String text) {
		if (text == null) return;
		consoleProcess.getOutput().writeAttrs(ConsolePane.colorAttributes(color),  text + "\n");
	}
	
	public void run()
	{
		console = consoleProcess.getConsole();

		done = false;
		try {
			while (!done)
			{
				if (!consoleProcess.isRunning()) {
					done = true;
					break;
				}
				console.updateAnimation();
				String text = breader.readLine();
				processLine(text);
			}
			breader.close();
		}
		catch (IOException ioe) {
			done = true;
		}
		
		consoleProcess.threadDone();
		Shell s = console.getShell();
		s.printPrompt(console, console.getOutput());
		console.setShell(s);

	}

	void processLine(String text)
	{
		if (text == null) return;
		if (directoryStack.processLine(text)) {
			display(console.getWarningColor(), text);			
			return;
		}
		String directory = directoryStack.current();
		if (lastError != null)
		{
			String message = null;
			if (lastMatcher != null
					&& lastMatcher.match(view, text, directory, errorSource) == null)
				message = lastMatcher.matchExtra(text);
			if (message != null)
			{
				display(text);
				lastError.addExtraMessage(message);
				return;
			} else
			{
				errorSource.addError(lastError);
				lastMatcher = null;
				lastError = null;
			}
		}
		color = consoleProcess.getConsole().getInfoColor();
		for (int i = 0; i < errorMatchers.length; i++)
		{
			ErrorMatcher m = errorMatchers[i];

			DefaultErrorSource.DefaultError error = 
				m.match(view, text, directory, errorSource);
			if (error != null)
			{
//				Log.log(Log.WARNING, CommandOutputParserThread.class, "New Error in dir:" + directory);
				lastError = error;
				color = consoleProcess.getConsole().getErrorColor();
				lastMatcher = m;
				break;
			}
		}
		display(text);
		
	}

	public void finishErrorParsing()
	{
		if (lastError != null)
		{
			done = true;
			errorSource.addError(lastError);
			lastError = null;
			lastMatcher = null;
		}
		
	}
	/*
	 * public void writeLine(String text) { try { bwriter.write(text + "\n");
	 * bwriter.flush(); } catch (IOException e) { Log.log(Log.ERROR,
	 * CommandOutputParserThread.class, e); } }
	 */
}
