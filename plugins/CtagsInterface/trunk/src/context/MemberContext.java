package context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;

import org.gjt.sp.jedit.Buffer;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class MemberContext extends FunctionContextProvider implements
		IContextFinder {

	static String [] scopes =
		"class struct union enum interface namespace".split(" ");

	@Override
	public String getContext(String identifier, Buffer buffer, int line,
			int pos) {
		String namespace = getMemberNamespace(buffer, line);
		if (namespace == null)
			return null;
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
		q.addCondition(TagDB.TAGS_NAME + "=" + TagDB.quote(identifier));
		ResultSet rs;
		boolean isMember = false;
		try {
			rs = CtagsInterfacePlugin.getDB().query(q);
			isMember = rs.next();
			rs.close();
		} catch (SQLException e) {
		}
		return isMember ? namespace : null;
	}

	private String getMemberNamespace(Buffer buffer, int line) {
		Tag tag = getFunctionContext(buffer.getPath(), line);
		if (tag == null)
			return null;
		return tag.getNamespace();
	}

}
