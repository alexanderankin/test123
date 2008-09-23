package context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class CaretContext {
	
	public static Pattern getVarDeclPattern(String var) {
		return Pattern.compile(".*\\b(\\w+)\\s*([*&]+\\s*|\\s+)(const\\s+)?" + var + "\\b.*");
	}

	public static String getContextUnderCaret(final View view, boolean includeCaret)
	{
		CaretContext context = new CaretContext(view);
		return context.getContextUnderCaret(includeCaret);
	}
	
	private Pattern pat, identifier, memberRef;
	private int pos, line;
	private Buffer buffer;
	
	public CaretContext(final View view) {
		pat = Pattern.compile("(\\w+)((\\.|->)(\\w)*)*");
		identifier = Pattern.compile("\\w+");
		memberRef = Pattern.compile("(\\.|->)");
		buffer = view.getBuffer();
		buffer.getPath();
		line = view.getTextArea().getCaretLine() + 1;
		pos = view.getTextArea().getCaretPosition();
		CtagsInterfacePlugin.getDestinationTag(view);
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
			Tag functionTag = getFunctionContext(buffer.getPath(), line);
			if (functionTag != null) {
				context = getLocalVarContext(functionTag, identifier, buffer, line, pos);
				if (context != null)
					return context;
				context = getFunctionArgContext(functionTag, identifier, buffer, line, pos);
				if (context != null)
					return context;
				context = getMemberContext(functionTag, identifier, buffer, line, pos);
				if (context != null)
					return context;
			}
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
	public Tag getFunctionContext(String path, int line)
	{
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = new Query(new String[]{"*"},
			new String[]{TagDB.TAGS_TABLE, TagDB.FILES_TABLE},
			new String[]{
				db.field(TagDB.TAGS_TABLE, TagDB.TAGS_FILE_ID) +
		           	"=" + db.field(TagDB.FILES_TABLE, TagDB.FILES_ID),
		        db.field(TagDB.FILES_TABLE, TagDB.FILES_NAME) +
		        	"=" + TagDB.quote(path)});
		ResultSet rs = null;
		int best = 0;
		Tag tag = null;
		try {
			rs = db.query(q);
			Vector<Tag> tags = db.getResultSetTags(rs);
			rs.close();
			for (Tag t: tags) {
				int n = t.getLine();
				if (n > line || n < best)
					continue;
				best = n;
				tag = t;
			}
		} catch (SQLException e) {
		}
		return tag;		
	}
	public String getLocalVarContext(Tag functionTag, String identifier,
			Buffer buffer, int line, int pos)
	{
		int functionLine = functionTag.getLine();
		if (functionLine == 0)
			return null;
		Pattern pat = getVarDeclPattern(identifier);
		// Go back from current line to function definition, since there may
		// be several declarations of the variable in nested blocks of code.
		for (int i = line; i >= functionLine; i--) {
			String l = buffer.getLineText(i - 1);
			Matcher m = pat.matcher(l);
			if (m.matches())
				return m.group(1);
		}
		return null;
	}
	public String getFunctionArgContext(Tag functionTag, String identifier,
			Buffer buffer, int line, int pos)
	{
		String signature = functionTag.getExtension(TagDB.extension2column("signature"));
		if (signature == null)
			return null;
		Pattern pat = getVarDeclPattern(identifier);
		Matcher m = pat.matcher(signature);
		if (m.matches())
			return m.group(1);
		return null;
	}
	public String getMemberContext(Tag functionTag, String identifier, Buffer buffer,
			int line, int pos)
	{
		String namespace = functionTag.getNamespace();
		if (namespace == null)
			return null;
		NamespaceMemberContext c = new NamespaceMemberContext(namespace); 
		boolean isMember = c.isMember(identifier);
		return isMember ? namespace : null;
	}

}
