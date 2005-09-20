package console;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

	LinkedList<String> mList;



	public String current()
	{
		return mList.getLast();
	}

	DirectoryStack()
	{
		mList = new LinkedList<String>();
	}

	void push(String v)
	{
		if (v != null)
		{
//			Log.log(Log.WARNING, DirectoryStack.class, "Push: " + v);
			mList.add(v);
		}
	}

	boolean isEmpty()
	{
		return mList.isEmpty();
	}

	String pop()
	{
		if (mList.size() < 1)
			return null;
		String retval =  mList.removeLast(); 
//		Log.log(Log.WARNING, DirectoryStack.class, "Pop: " + retval);
		return retval;
	}

	public boolean processLine(String line)
	{
		Matcher match = makeEntering.matcher(line);
		if (match.find())
		{
			String enteringDir = match.group(1);
			push(enteringDir);
			return true;
		}

		match = makeLeaving.matcher(line);
		if (match.find() && !isEmpty())
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
