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

	private ActionSet builtinActions;
	private ActionSet xActionSet;
	
	/** A mapping of built in actions to xfind actions we wish to remap */
	private HashMap actionMap;

	public static boolean isEnabled()
	{
		sm_enabled = jEdit.getBooleanProperty(propname);
		return sm_enabled;
	}

	public static void setEnabled(boolean enabled)
	{
		sm_enabled = enabled;
		jEdit.setBooleanProperty(propname, enabled);
		reset();
	}

	public static void reset()
	{
		if (sm_instance == null)
			sm_instance = new ReplaceActions();
		sm_instance.remap();
	}

	public static void restore() {
		sm_instance.undo();
	}
	private ReplaceActions()
	{
		actionMap = new HashMap();
		actionMap.put("find", "xfind");
		actionMap.put("search-in-directory", "xsearch-in-directory");
		actionMap.put("replace-in-selection", "xreplace-in-selection");
		if (isEnabled()) remap();
	}

	synchronized public void remap()
	{
		if (sm_remapped || !sm_enabled)
			return;
		sm_remapped = true;
		ActionContext ac = jEdit.getActionContext();
		xActionSet = ac.getActionSetForAction("xfind");
		builtinActions = new ActionSet("SearchAndReplace restored actions");
		
		Iterator itr = actionMap.keySet().iterator();
		while (itr.hasNext())
		{
			String builtIn = (String) itr.next();
			String extended = (String) actionMap.get(builtIn);
			EditAction xaction = ac.getAction(extended);
			EditAction origAction = ac.getAction(builtIn);
			builtinActions.addAction(origAction);
			xaction.setName(builtIn);
			xActionSet.addAction(xaction);
		}
//		ac.addActionSet(actionSet);

	}
	
	synchronized public void undo() 
	{
		if (!sm_remapped || !isEnabled() ) return;
		ActionContext ac = jEdit.getActionContext();
		ActionSet actionSet = ac.getActionSetForAction("save");
		EditAction actions[] = builtinActions.getActions();
		for (int i=0; i<actions.length; ++i) {
			xActionSet.removeAction(actions[i].getName());
			actionSet.addAction(actions[i]);
		}
	}
}
