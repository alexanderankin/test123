package context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import ctags.CtagsInterfacePlugin;

public class CaretContext {
	static FunctionLocalVarContextFinder localVarContext;
	static FunctionArgContextFinder argContext;
	static MemberContext memberContext;
	
	public static Pattern getVarDeclPattern(String var) {
		return Pattern.compile(".*\\b(\\w+)\\s*([*&]+\\s*|\\s+)(const\\s+)?" + var + "\\b.*");
	}

	{
		localVarContext = new FunctionLocalVarContextFinder();
		argContext = new FunctionArgContextFinder();
		memberContext = new MemberContext();
	}
	
	public static String getContextUnderCaret(final View view, boolean includeCaret)
	{
		CaretContext context = new CaretContext(view);
		return context.getContextUnderCaret(includeCaret);
	}
	
	private Pattern pat, identifier, memberRef;
	private int pos, line;
	private String path;
	private Buffer buffer;
	private String tag;
	
	public CaretContext(final View view) {
		pat = Pattern.compile("(\\w+)((\\.|->)(\\w)*)*");
		identifier = Pattern.compile("\\w+");
		memberRef = Pattern.compile("(\\.|->)");
		buffer = view.getBuffer();
		path = buffer.getPath();
		line = view.getTextArea().getCaretLine() + 1;
		pos = view.getTextArea().getCaretPosition();
		tag = CtagsInterfacePlugin.getDestinationTag(view);
	}
	/* Algorithm:
	 * In general (for a simple case), we have a construct like:
	 * a1.a2.a3.a4.prefix<caret>
	 * where each 'aN' is a variable, each '.' is a reference for
	 * a member of the type of 'aN' (e.g. '->' in C++), and 'prefix'
	 * is the prefix of a member of the type of the last variable in
	 * the list (and may be empty).
	 * We need to find the one such construct that ends at the caret,
	 * then start from the first one, find its type, and go on to
	 * find the types of the other constructs.
	 * The first type can be found in (according to the following order):
	 * 1. The function - if it's a local variable.
	 * 2. The function arguments - if it's an argument.
	 * 3. A member of the current type (to which the current function belongs).
	 * 4. A static variable defined in the file.
	 * 5. A global variable defined somewhere else.
	 */
	private String getContextUnderCaret(boolean includeCaret)
	{
		String context = findPatternAtCaret(pat, includeCaret);
		if (context == null)
			return null;
		String [] parts = memberRef.split(context);
		context = null;
		for (int i = 0; i < parts.length; i++)
			context = findContext(context, parts[i]);
		return context;
	}
	private String findContext(String context, String identifier)
	{
		if (context == null) {
			/* use following search order:
			 * 1. Local variable in function
			 * 2. Function argument
			 * 3. Member of current class
			 * 4. Static variable in file
			 * 5. Global variable
			 */
			context = localVarContext.getContext(identifier, buffer, line, pos);
			if (context != null)
				return context;
			context = argContext.getContext(identifier, buffer, line, pos);
			if (context != null)
				return context;
			context = memberContext.getContext(identifier, buffer, line, pos);
			if (context != null)
				return context;
			return null;
		} else {
			/* look for identifier member of context */
		}
		return null;
	}
	
	private String findPatternAtCaret(Pattern pattern, boolean includeCaret)
	{
		int index = pos - buffer.getLineStartOffset(line - 1);
		String text = buffer.getLineText(line - 1);
		if (includeCaret) {
			Matcher m = identifier.matcher(text);
			if (m.find(index))
				index = m.end();
		} else {
			StringBuffer reversed = new StringBuffer(text.substring(0, index)).reverse();
			Matcher m = identifier.matcher(reversed.toString());
			if (m.find())
				index -= m.end();
		}
		Matcher m = pattern.matcher(text.substring(0, index));
		int end = -1;
		int start = -1;
		String selected = "";
		while (end < index) {
			if (! m.find())
				return null;
			end = m.end();
			start = m.start();
			selected = m.group();
		}
		if (start > index || selected.length() == 0)
			return null;
		return selected;

	}
}
