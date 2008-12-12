package context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	static String scopeCondition;
	private Tag namespaceTag = null;

	{
		StringBuffer sb = new StringBuffer();
		for (String scope: scopes) {
			if (!(sb.length() > 0))
				sb.append(",");
			sb.append("'" + scope + "'");
		}
		scopeCondition = TagDB.extension2column("kind") + " IN (" + sb + ")";
	}
	public NamespaceMemberContext(String namespace) {
		Query q = new Query("*", TagDB.TAGS_TABLE, "NAME='" + namespace + "'");
		q.addCondition(scopeCondition);
		TagDB db = CtagsInterfacePlugin.getDB();
		ResultSet rs;
		try {
			rs = db.query(q);
		} catch (SQLException e) {
			return;
		}
		Vector<Tag> tags = db.getResultSetTags(rs);
		if (! tags.isEmpty())
			namespaceTag = tags.firstElement();
	}
	private Vector<Tag> getMember(String member) {
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = db.getBasicTagQuery();
		q.addCondition(TagDB.TAGS_NAME + "=" + TagDB.quote(member));
		q.addCondition(TagDB.extension2column("kind") + "=" + TagDB.quote(namespaceTag.getKind()));
		ResultSet rs;
		Vector<Tag> tags = null;
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
		if (namespaceTag == null)
			return null;
		Vector<Tag> tags = getMember(member);
		if (tags.isEmpty())
			return null;
		Tag t = tags.firstElement();
		// Parse the declaration of the member to find its type.
		Pattern pat = CppContextFinder.getVarDeclPattern(member);
		String line = read(t.getFile(), t.getLine());
		if (line == null)
			return null;
		Matcher m = pat.matcher(line);
		if (m.matches())
			return m.group(1);
		return null;
	}

}
