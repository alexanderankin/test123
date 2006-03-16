package console;

import java.util.HashSet;
import java.util.LinkedHashMap;
import javax.swing.DefaultListModel;
import org.gjt.sp.jedit.jEdit;
import console.utils.StringList;

/**
 * A model for storing a collection of ErrorMatchers.
 * 
 * @version $Id$
 * @author ezust
 * @since Console 4.2.5
 * 
 */
public class ErrorListModel extends DefaultListModel 
{
	LinkedHashMap<String, ErrorMatcher> m_matchers;

	// A list of items that were deleted, we do not want to see anymore.
	StringList m_deleted;
	StringList m_user;
	StringList m_default;

	public ErrorMatcher get(int i)
	{
		return (ErrorMatcher) super.get(i);
		
	}

	static public ErrorListModel load()
	{
		ErrorListModel retval = new ErrorListModel();
		retval.restore();
		
		return retval;
	}

	public void reset() {
		jEdit.setProperty("console.error.default", "perl ant python vhdl msvc msnet jade antemacs emacs generic");
		jEdit.setProperty("console.error.deleted", "");
		clear();
		load();
	}
	
	public void save()
	{
		jEdit.setProperty("console.error.user", m_user.join(" "));
		jEdit.setProperty("console.error.deleted", m_user.join(" "));
		HashSet<String> deletedSet = new HashSet<String>();
		deletedSet.addAll(m_deleted);
		for (String key: m_matchers.keySet()) {
			if (m_deleted.contains(key)) continue;
			ErrorMatcher matcher = m_matchers.get(key);
			matcher.save();
		}
	}

	private void restore()
	{
		m_matchers = new LinkedHashMap<String, ErrorMatcher>();
		m_user = StringList.split(jEdit.getProperty("console.error.user", ""), "\\s+");
		m_default = StringList.split(jEdit.getProperty("console.error.default", ""), "\\s+");
		m_deleted = StringList.split(jEdit.getProperty("console.error.deleted", ""), "\\s+");
		loadMatchers(true, m_user);
		loadMatchers(false, m_default);
		for (ErrorMatcher matcher : m_matchers.values())
		{
			String internalName = matcher.internalName();
			if (m_deleted.contains(internalName))
			{
				m_matchers.remove(internalName);
			}
			else
			{
				addElement(matcher);
			}
		}
	}

	private ErrorListModel()
	{
	}

	private void loadMatchers(boolean user, StringList names)
	{
		if ((names == null) || names.size() == 0)
			return;
		for (String key : names)
		{
			if (key == null || key.equals("null"))
				continue;
			if (m_matchers.containsKey(key))
				continue;
			if (m_deleted.contains(key))
				continue;
			ErrorMatcher newMatcher = new ErrorMatcher(key);
			if (!newMatcher.isValid())
				continue;
			newMatcher.user = user;
			m_matchers.put(key, newMatcher);
		}
	}

	// }}}

	@Override
	public void removeElementAt(int index)
	{
		int i = 0;
		ErrorMatcher m = get(index);
		String matcherKey = m.internalName();
		m_matchers.remove(matcherKey);
		m_deleted.add(matcherKey);
		super.removeElementAt(index);
	}

	@Override
	public void insertElementAt(Object obj, int index)
	{
		ErrorMatcher matcher = (ErrorMatcher) obj;
		super.insertElementAt(matcher, index);
		String key = matcher.internalName();
		m_deleted.remove(key);
		m_matchers.put(key, matcher);
		m_user.add(key);
		super.insertElementAt(obj, index);
	}

	@Override
	public void addElement(Object m)
	{
		ErrorMatcher matcher = (ErrorMatcher) m;
		String key = matcher.internalName();
		m_matchers.put(key, matcher);
		m_deleted.remove(key);
		super.addElement(m);
	}
}
