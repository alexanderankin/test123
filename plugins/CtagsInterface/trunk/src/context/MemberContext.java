package context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import org.gjt.sp.jedit.Buffer;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class MemberContext extends FunctionContextProvider implements
		IContextFinder {

	@Override
	public String getContext(String identifier, Buffer buffer, int line,
			int pos) {
		String namespace = getMemberNamespace(buffer, line);
		if (namespace == null)
			return null;
		NamespaceMemberContext c = new NamespaceMemberContext(namespace); 
		boolean isMember = c.isMember(identifier);
		return isMember ? namespace : null;
	}

	private String getMemberNamespace(Buffer buffer, int line) {
		Tag tag = getFunctionContext(buffer.getPath(), line);
		if (tag == null)
			return null;
		return tag.getNamespace();
	}

}
