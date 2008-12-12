package context;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import ctags.CtagsInterfacePlugin;
import ctags.Tag;
import db.Query;
import db.TagDB;

public class CtagsContextUtil {

	private static CtagsContextUtil instance = new CtagsContextUtil();
	private static String [] scopes =
		"class struct union enum interface namespace".split(" ");

	private String getMemberCondition(Set<String> classes)
	{
		StringBuffer classesStr = new StringBuffer();
		for (String clazz: classes) {
			if (classesStr.length() > 0)
				classesStr.append(",");
			classesStr.append(TagDB.quote(clazz));
		}
		HashSet<String> columns = CtagsInterfacePlugin.getTagColumns();
		StringBuffer conditionStr = new StringBuffer();
		for (String scope: scopes) {
			String column = TagDB.extension2column(scope);
			if (! columns.contains(column))
				continue;
			if (conditionStr.length() > 0)
				conditionStr.append(" OR ");
			conditionStr.append(column + " IN (" + classesStr + ")");
		}
		return conditionStr.toString();
	}
	
	public static CtagsContextUtil instance()
	{
		return instance;
	}
	
	public Set<String> getSuperClasses(String clazz)
	{
		HashSet<String> classes = new HashSet<String>();
		classes.add(clazz);
		collectSuperClasses(clazz, classes);
		return classes;
	}

	public Vector<Tag> getMembers(Set<String> classes)
	{
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = db.getBasicTagQuery();
		q.addCondition("(" + getMemberCondition(classes) + ")");
		try {
			Vector<Tag> members = db.getResultSetTags(db.query(q));
			return members;
		} catch (SQLException e) {
		}
		return null;
	}
	
	private void collectSuperClasses(String clazz, HashSet<String> classes)
	{
		TagDB db = CtagsInterfacePlugin.getDB();
		Query q = db.getBasicTagQuery();
		q.addCondition(db.field(TagDB.TAGS_TABLE, TagDB.TAGS_NAME) + "=" + TagDB.quote(clazz));
		q.addCondition(TagDB.extension2column("kind") + " IN ('class','struct')");
		Vector<Tag> tags;
		try {
			tags = db.getResultSetTags(db.query(q));
		} catch (SQLException e) {
			return;
		}
		if (tags.size() != 1)
			return;
		Tag tag = tags.firstElement();
		String inheritsStr = tag.getExtension("inherits");
		if (inheritsStr == null)
			return;
		String[] superClasses = inheritsStr.split(",");
		for (String superClass: superClasses) {
			if (classes.add(superClass))
				collectSuperClasses(superClass, classes);
		}
	}

}
