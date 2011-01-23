package console;
// {{{ imports
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.StringList;
// }}}

// {{{ ErrorListModel
/**
 * A model for storing a collection of ErrorMatchers.
 *
 * @version $Id$
 * @author ezust
 * @author Eric Le Lay
 * @since Console 4.2.5
 *
 */
public class ErrorListModel extends DefaultListModel
{
    // {{{ Data Members
	ArrayList<ErrorMatcher> m_matchers;
	StringList m_default;
    // }}}
    // {{{ constructor
	public ErrorListModel()
	{
    } // }}}
    // {{{ Member Functions
	// {{{ get
	public ErrorMatcher get(int i)
	{
		return (ErrorMatcher) super.get(i);
    } // }}}

    // {{{ load
	static public ErrorListModel load()
	{
		ErrorListModel retval = new ErrorListModel();
		retval.restore();
		return retval;
    } // }}}

    // {{{ reset
	/* reset the visible list to default, but don't save the changes
	   to allow for cancel in the option pane
	 */
	public void reset() {
		restore(m_default.join(" ") );
    } // }}}
	// {{{
	public void save()
	{
		StringList visible = new StringList();
		for (ErrorMatcher matcher: m_matchers)
		{
			String key = matcher.internalName();
			if (matcher.isValid())
				matcher.save();
			visible.add(key);
		}
		jEdit.setProperty("console.errors.list", visible.join(" "));
    } // }}}

    // {{{ restore
	/* Restores from properties, the default list */
	public void restore()
	{
		restore(jEdit.getProperty("console.errors.list", ""));
    }
	
    /** Restores the given visible list from properties
     *  @param	visibleList	list of matcher names, separated by spaces
     */
	private void restore(String visibleList)
	{
		super.clear();
		m_matchers = new ArrayList<ErrorMatcher>();
		m_default = StringList.split(jEdit.getProperty("console.errors.default", ""), "\\s+");
		StringList visible = StringList.split(visibleList, "\\s+");
		if (visible.size() == 0) {
			jEdit.setProperty("console.errors.list", m_default.join(" "));
			visible = m_default;
		}

		for (String key: visible)
		{
			ErrorMatcher m = new ErrorMatcher(key);
			m_matchers.add(m);
			super.addElement(m);
		}

    } // }}}


	// {{{ removeElementAt
	public void removeElementAt(int index)
	{
		m_matchers.remove(index);
		super.removeElementAt(index);
    } // }}}

	// {{{ insertElementAt
	public void insertElementAt(Object obj, int index)
	{
		ErrorMatcher matcher = (ErrorMatcher) obj;
		m_matchers.add(index, matcher);
		super.insertElementAt(obj, index);
    } // }}}

	// {{{ addElement
	public void addElement(Object m)
	{
		ErrorMatcher matcher = (ErrorMatcher) m;
		m_matchers.add(matcher);
		super.addElement(m);
	}   // }}}
    // }}}
} // }}}
