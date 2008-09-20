package context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class FunctionContextProvider {
	public Tag getFunctionContext(String path, int line) {
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = new Query(new String[]{"*"},
			new String[]{TagDB.TAGS_TABLE, TagDB.FILES_TABLE},
			new String[]{
				db.field(TagDB.TAGS_TABLE, TagDB.TAGS_FILE_ID) +
		           	"=" + db.field(TagDB.FILES_TABLE, TagDB.FILES_ID),
		        db.field(TagDB.FILES_TABLE, TagDB.FILES_NAME) +
		        	"=" + db.quote(path)});
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
	private String getFunctionContextField(String path, int line, String field) {
		Tag tag = getFunctionContext(path, line);
		if (tag != null)
			return tag.getExtension(field);
		return null;
	}
	public String getFunctionContextSignature(String path, int line) {
		return getFunctionContextField(path, line, TagDB.extension2column("signature"));
	}
	public int getFunctionContextLine(String path, int line) {
		Tag tag = getFunctionContext(path, line);
		if (tag != null)
			return tag.getLine();
		return 0;
	}
}
