package xsearch;

import java.util.HashMap;
import java.util.Iterator;

import org.gjt.sp.jedit.ActionContext;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.jEdit;

/**
 * 
 * Renames some of the XSearch actions to be the same as built-in actions. In
 * particular, the "find" and "search in directory" actions are replaced by
 * xsearch version.
 * 
 * @author ezust
 * @version $Id$
 */
public class ReplaceActions
{
	public static final String propname = "xsearch.actionreplace.enabled";

	private static boolean sm_enabled;

	private static boolean sm_remapped = false;

	private static ReplaceActions sm_instance = null;

	/** A mapping of built in actions to xfind actions we wish to remap */
	private HashMap actionMap;

	public static boolean isEnabled()
	{
		return jEdit.getBooleanProperty(propname);
	}

	public static void setEnabled(boolean enabled)
	{
		jEdit.setBooleanProperty(propname, enabled);
		reset();
	}

	public static void reset()
	{
		if (sm_instance == null)
			sm_instance = new ReplaceActions();
		sm_instance.remap();
	}

	private ReplaceActions()
	{
		actionMap = new HashMap();
		actionMap.put("find", "xfind");
		actionMap.put("search-in-directory", "xsearch-in-directory");
		actionMap.put("replace-in-selection", "xreplace-in-selection");
		remap();
	}

	synchronized public void remap()
	{
		if (sm_remapped || !isEnabled())
			return;
		ActionSet actionSet = new ActionSet("Plugin: XSearch (remapped)");
		ActionContext ac = jEdit.getActionContext();
		Iterator itr = actionMap.keySet().iterator();
		while (itr.hasNext())
		{
			String builtIn = (String) itr.next();
			String extended = (String) actionMap.get(builtIn);
			EditAction xaction = ac.getAction(extended);
			xaction.setName(builtIn);
			actionSet.addAction(xaction);
		}
		ac.addActionSet(actionSet);
		sm_remapped = true;
	}
}
