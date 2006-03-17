package console;

import java.util.ArrayList;
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
	ArrayList<ErrorMatcher> m_matchers;
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
		
		jEdit.setProperty("console.errors.list", m_default.join(" ") );
		super.clear();
		load();
	}
	
	public void save()
	{
		StringList visible = new StringList();
		for (ErrorMatcher matcher: m_matchers) {
			String key = matcher.internalName();
			if (matcher.user) matcher.save();
			visible.add(key);
		}
		jEdit.setProperty("console.errors.list", visible.join(" "));
	}

	private void restore()
	{
		super.clear();
		m_matchers = new ArrayList<ErrorMatcher>();
		m_default = StringList.split(jEdit.getProperty("console.errors.default", ""), "\\s+");
		StringList visible = StringList.split(jEdit.getProperty("console.errors.list", ""), "\\s+");
		
		for (String key: visible) {
			ErrorMatcher m = new ErrorMatcher(key);
			m_matchers.add(m);
			super.addElement(m);
		}
		
	}

	public ErrorListModel()
	{
	}


	@Override
	public void removeElementAt(int index)
	{
		int i = 0;
		m_matchers.remove(index);
		super.removeElementAt(index);
	}

	@Override
	public void insertElementAt(Object obj, int index)
	{
		ErrorMatcher matcher = (ErrorMatcher) obj;
		String key = matcher.internalName();
		m_matchers.add(index, matcher);
		super.insertElementAt(obj, index);
	}

	@Override
	public void addElement(Object m)
	{
		ErrorMatcher matcher = (ErrorMatcher) m;
		String key = matcher.internalName();
		m_matchers.add(matcher);
		super.addElement(m);
	}
}
