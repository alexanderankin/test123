package context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class CppContextFinder extends AbstractContextFinder {

	CppContextFinder(Buffer buffer, int pos) {
		super(buffer, pos);
	}

	@Override
	public String findExpressionAtPos() {
		return findPattern(true);
	}

	@Override
	public String findExpressionBeforePos() {
		return findPattern(false);
	}

	@Override
	public Object findFirstContext(Object firstExpressionPart) {
		Tag functionTag = getFunctionContext();
		if (functionTag != null)
		{
			String context = getLocalVarContext(functionTag,
				(String) firstExpressionPart);
			if (context != null)
				return context;
			context = getFunctionArgContext(functionTag,
				(String) firstExpressionPart);
			if (context != null)
				return context;
			context = getMemberContext(functionTag,
				(String) firstExpressionPart);
			if (context != null)
				return context;
			// Look for static identifier in current buffer
			// Look for global identifier
		}
		return null;
	}

	@Override
	/* Find expressionPart (an identifier) in the context prevContext (a
	 * namespace - type name). Note that the identifier can be a member of
	 * the named type or any of its base classes.
	 */
	public Object findNextContext(Object prevContext, Object expressionPart) {
		Tag memberTag = findMemberTag(prevContext, expressionPart);
		if (memberTag == null)
			return null;
		return getMemberType(memberTag);
	}

	private String getMemberType(Tag memberTag)
	{
		Pattern pat = getVarDeclPattern(memberTag.getName());
		int line = memberTag.getLine();
		String file = memberTag.getFile();
		String text = getLineText(file, line);
		Matcher m = pat.matcher(text);
		if (m.matches())
			return m.group(1);
		return null;
	}
	
	private Tag findMemberTag(Object context, Object member)
	{
		String type = (String) context;
		Vector<Tag> tags = tagMap.get(member);
		if (tags == null)
			return null;
		Set<String> classes = CtagsContextUtil.instance().getSuperClasses(type);
		for (Tag tag: tags) {
			String namespace = tag.getNamespace();
			if (namespace == null || namespace.isEmpty())
				continue;
			if (classes.contains(namespace))
				return tag;
		}
		return null;
	}
	
	@Override
	public Tag getContextTag(Object context) {
		String type = (String) context;
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = db.getBasicTagQuery();
		q.addCondition(TagDB.TAGS_NAME + "=" + TagDB.quote(type));
		q.addCondition(TagDB.extension2column("kind") + " IN (" +
			"'class','struct','union','enum','interface','namespace')");
		Vector<Tag> tags;
		try {
			tags = db.getResultSetTags(db.query(q));
			if (tags.isEmpty())
				return null;
			return tags.firstElement();
		} catch (SQLException e) {
		}
		return null;
	}

	@Override
	public Tag getCurrentContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] parseExpression(String expression) {
		String[] parts = memberRef.split(expression);
		if (parts.length == 0)
			return null;
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = db.getBasicTagQuery();
		StringBuffer condition = new StringBuffer();
		for (String part: parts) {
			if (condition.length() > 0)
				condition.append(",");
			condition.append(TagDB.quote(part));
		}
		q.addCondition(db.field(TagDB.TAGS_TABLE, TagDB.TAGS_NAME) + " IN (" +
			condition.toString() + ")");
		tagMap = new HashMap<String, Vector<Tag>>();
		try {
			Vector<Tag> tags = db.getResultSetTags(db.query(q));
			for (Tag tag: tags) {
				String name = tag.getName();
				if (! tagMap.containsKey(name))
					tagMap.put(name, new Vector<Tag>());
				tagMap.get(name).add(tag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parts;
	}

	private static Pattern expression = Pattern.compile("(\\w+)((\\.|->)(\\w)*)*");
	private static Pattern identifier = Pattern.compile("\\w+");
	private static Pattern memberRef = Pattern.compile("(\\.|->)");
	private Map<String, Vector<Tag>> tagMap;
	
	private String findPattern(boolean includePos)
	{
		int line = buffer.getLineOfOffset(pos);
		int index = pos - buffer.getLineStartOffset(line);
		String text = buffer.getLineText(line);
		if (includePos) {
			Matcher m = identifier.matcher(text);
			if (m.find(index))
				index = m.end();
		} else {
			StringBuffer reversed = new StringBuffer(text.substring(0, index)).reverse();
			Matcher m = identifier.matcher(reversed.toString());
			if (m.find())
				index -= m.end();
		}
		Matcher m = expression.matcher(text.substring(0, index));
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
	
	private Tag getFunctionContext()
	{
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = new Query(new String[]{"*"},
			new String[]{TagDB.TAGS_TABLE, TagDB.FILES_TABLE},
			new String[]{
				db.field(TagDB.TAGS_TABLE, TagDB.TAGS_FILE_ID) +
		           	"=" + db.field(TagDB.FILES_TABLE, TagDB.FILES_ID),
		        db.field(TagDB.FILES_TABLE, TagDB.FILES_NAME) +
		        	"=" + TagDB.quote(buffer.getPath())});
		ResultSet rs = null;
		int best = 0;
		Tag tag = null;
		try {
			rs = db.query(q);
			Vector<Tag> tags = db.getResultSetTags(rs);
			for (Tag t: tags) {
				int n = t.getLine();
				if (n > line + 1 || n < best)
					continue;
				best = n;
				tag = t;
			}
		} catch (SQLException e) {
		}
		return tag;		
	}

	public static Pattern getVarDeclPattern(String var) {
		return Pattern.compile(".*\\b(\\w+)\\s*([*&]+\\s*|\\s+)(const\\s+)?" + var + "\\b.*");
	}

	public String getLocalVarContext(Tag functionTag, String identifier)
	{
		int functionLine = functionTag.getLine();
		if (functionLine == 0)
			return null;
		Pattern pat = getVarDeclPattern(identifier);
		// Go back from current line to function definition, since there may
		// be several declarations of the variable in nested blocks of code.
		for (int i = line; i >= functionLine - 1; i--) {
			String l = buffer.getLineText(i);
			Matcher m = pat.matcher(l);
			if (m.matches()) {
				String type = m.group(1);
				if (! type.equals("return"))
				return m.group(1);
			}
		}
		return null;
	}
	
	public String getFunctionArgContext(Tag functionTag, String identifier)
	{
		String signature = functionTag.getExtension(
			TagDB.extension2column("signature"));
		if (signature == null)
			return null;
		Pattern pat = getVarDeclPattern(identifier);
		Matcher m = pat.matcher(signature);
		if (m.matches())
			return m.group(1);
		return null;
	}
	
	public String getMemberContext(Tag functionTag, String identifier)
	{
		String namespace = functionTag.getNamespace();
		if (namespace == null)
			return null;
		return (String) findNextContext(namespace, identifier);
	}
	
	private String getLineText(String file, int line)
	{
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(file));
			while (line > 1) {
				input.readLine();
				line--;
			}
			return input.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
