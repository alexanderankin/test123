package context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class NamespaceMemberContext {
	static String [] scopes =
		"class struct union enum interface namespace".split(" ");
	private String namespace;

	public NamespaceMemberContext(String namespace) {
		this.namespace = namespace;
	}
	private Vector<Tag> getMember(String member) {
		StringBuffer sb = new StringBuffer();
		HashSet<String> columns = CtagsInterfacePlugin.getTagColumns();
		boolean first = true;
		for (String scope: scopes) {
			String cname = TagDB.extension2column(scope);
			if (! columns.contains(cname))
				continue;
			if (! first)
				sb.append(" OR ");
			else
				first = false;
			sb.append(TagDB.extension2column(scope) + "=" + TagDB.quote(namespace));
		}
		Query q = CtagsInterfacePlugin.getBasicTagQuery();
		q.addCondition("(" + sb.toString() + ")");
		q.addCondition(TagDB.TAGS_NAME + "=" + TagDB.quote(member));
		ResultSet rs;
		Vector<Tag> tags = null;
		TagDB db = CtagsInterfacePlugin.getDB();
		try {
			rs = db.query(q);
			tags = db.getResultSetTags(rs);
			rs.close();
		} catch (SQLException e) {
		}
		return tags;		
	}
	public boolean isMember(String identifier) {
		Vector<Tag> tags = getMember(identifier);
		return (tags != null && (! tags.isEmpty()));
	}
	public String read(String file, int line) {
		String text = null;
		BufferedReader input;
		// Ignore exceptions, tag database may be outdated
		try {
			input = new BufferedReader(new FileReader(file));
			while (line > 0)
				text = input.readLine();
			input.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return text;
	}
	public String getContext(String member) {
		if (namespace == null)
			return null;
		Vector<Tag> tags = getMember(member);
		if (tags.isEmpty())
			return null;
		Tag t = tags.firstElement();
		// Parse the declaration of the member to find its type.
		Pattern pat = CaretContext.getVarDeclPattern(member);
		String line = read(t.getFile(), t.getLine());
		if (line == null)
			return null;
		Matcher m = pat.matcher(line);
		if (m.matches())
			return m.group(1);
		return null;
	}

}
