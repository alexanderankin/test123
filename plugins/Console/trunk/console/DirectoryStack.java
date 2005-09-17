package console;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public class DirectoryStack
{
	private static Pattern makeEntering, makeLeaving;
	static
	{
		try
		{
			makeEntering = Pattern.compile(jEdit.getProperty("console.error.make.entering"));
			makeLeaving = Pattern.compile(jEdit.getProperty("console.error.make.leaving"));
		} catch (PatternSyntaxException re)
		{
			Log.log(Log.ERROR, ConsoleProcess.class, re);
		}
	} // }}}

	List mList;

	String currentDirectory;

	public String current()
	{
		return currentDirectory;
	}

	DirectoryStack()
	{
		mList = (List) Collections.synchronizedList(new LinkedList());
	}

	void push(String v)
	{
		if (v != null)
		{
			currentDirectory = v;
			mList.add(v);
		}
	}

	boolean isEmpty()
	{
		return mList.isEmpty();
	}

	String pop()
	{
		int size = mList.size();
		if (size < 1)
			return null;
		Object r = mList.get(size - 1);
		if (r == null)
			return null;
		currentDirectory = r.toString();
		mList.remove(size - 1);
		return currentDirectory;
	}

	public boolean processLine(String line)
	{
		Matcher match = makeEntering.matcher(line);
		if (match.matches())
		{
			String enteringDir = match.group(1);
			push(currentDirectory);
			return true;
		}

		match = makeLeaving.matcher(line);
		if (match.matches() && !isEmpty())
		{
			pop();
			return true;
		}
		return false;
	}

	public static void main(String args[])
	{
		DirectoryStack ds = new DirectoryStack();
		ds.push("Hello");
		ds.push("there");
		System.out.println(ds.pop());
		System.out.println(ds.pop());
		System.out.println(ds.pop());
	}

}
