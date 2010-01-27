package launcher;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuItem;

import org.gjt.sp.jedit.ActionContext;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public abstract class LauncherType {
	
	public static final String LAUNCHER_TYPE_SERVICE_NAME =
		LauncherType.class.getName();
	
	public static final String OPT_BASE_PREFIX = LauncherPlugin.OPT_BASE_PREFIX;
	public static final String LABEL_SUFFIX = LauncherUtils.LABEL_SUFFIX;
	public static final String ACTION_SUFFIX = ".action";
	public static final String SHORT_LABEL_SUFFIX = LABEL_SUFFIX + ".short";
	public static final String DEFAULT_SUFFIX = ".default";
	
	private final String SERVICE_NAME;
	
	private String labelPropertyName = null;
	private String label = null;
	private String actionPropertyName = null;
	private String actionLabel = null;
	
	private String defaultLauncherPropertyName = null;

	private Launcher defaultLauncher;
	
	private ActionSet actionSet = null;
	
	private ActionContext actionContext = null;
	
	protected LauncherType() {
		super();
		SERVICE_NAME = this.getClass().getName();
	}
	
	protected LauncherType(String serviceName) {
		super();
		this.SERVICE_NAME = serviceName;
	}
	
	public abstract void registerLaunchers();
	
	public void reload() {
		if (actionSet != null) {
			setActionSet(buildActionSet());
		}
	}
	
	//{{{ unregisterUserDefinedLaunchers() method
	/**
	 * Unregisters all user-defined external application launchers:
	 * these are defined by the user in the option pane and do not
	 * come from another plugin (e.g services.xml). Therefore these
	 * can change at runtime, hence the requirement to be able to
	 * remove them from jEdit's services framework. This is typically
	 * needed when saving the list of launchers defined by the users:
	 * all previous user-defined launchers are cleared and a new list
	 * is registered. 
	 */
	public void unregisterUserDefinedLaunchers()
	{
		for (String name : ServiceManager.getServiceNames(SERVICE_NAME)) {
			Launcher launcher = (Launcher)
				ServiceManager.getService(SERVICE_NAME, name);
			if (launcher!=null && launcher.isUserDefined())
				ServiceManager.unregisterService(SERVICE_NAME, name);
		}
	} //}}}

	public abstract Object resolve(Object resource);
	
	public ActionSet buildActionSet() {
		ActionSet actions = new ActionSet(getLabel());
		for (Launcher launcher: getLaunchers()) {
			actions.addAction(launcher);
		}
		return actions;
	}
	
	public ActionContext getActionContext() {
		if (actionContext == null) {
			actionContext = LauncherActionContext.INSTANCE;
			actionContext.addActionSet(getActionSet());
		}
		return actionContext;
	}
	
	public ActionContext buildActionContextFor(Object resource) {
		ActionContext context = getActionContext();
		checkContext(context);
		LauncherActionContext launcherContext = (LauncherActionContext)context;
		try {
			launcherContext = launcherContext.clone();
			launcherContext.init(this, resource);
			context = launcherContext;
		} catch (Exception e) {
			Log.log(Log.ERROR, this,
				"Failed to clone " + LauncherActionContext.class.getName(), e);
			LauncherActionContext newContext = new LauncherActionContext();
			newContext.init(this, resource);
			context = newContext;
		}
		return context;
	}
	
	public boolean isFirstLevel(EditAction action, Object resolvedResource) {
		if (action instanceof Launcher) {
			Launcher launcher = (Launcher)action;
			return (launcher.canLaunch(resolvedResource) &&
					(launcher.isFirstLevelFor(resolvedResource) ||
					launcher == getDefaultLauncher()));
		}
		return action == getDefaultLauncher();
	}
	
	public JMenuItem[] buildMenuItemsFor(Object resource) {
		Object resolvedResource = resolve(resource);
		// Build the list of 1st and 2nd level actions
		Set<EditAction> level1Actions = new HashSet<EditAction>(); 
		Set<EditAction> level2Actions = new HashSet<EditAction>();
		Map<EditAction,ActionContext> actionContexts = new HashMap<EditAction,ActionContext>();
		getActionsFor(resolvedResource, level1Actions, level2Actions, actionContexts);
		JMenuItem[] items = LauncherUtils.buildMenuItemsWith(
							SERVICE_NAME,
							getActionLabel(new Object[]{"..."}),
							level1Actions,
							level2Actions,
							actionContexts);
		return items;
	}

	public void getActionsFor(Object resource, 
							Set<EditAction> level1Actions,
							Set<EditAction> level2Actions,
							Map<EditAction,ActionContext> actionContexts) {
		Object resolvedResource = resolve(resource);
		ActionContext context = buildActionContextFor(resolvedResource);
		for (EditAction action: getActionSet().getActions()) {
			if (action instanceof Launcher &&
					!((Launcher)action).canLaunch(resolvedResource))
				continue;
			boolean already1stLevel = level1Actions.contains(action);
			boolean already2ndLevel = level2Actions.contains(action);
			if (isFirstLevel(action, resolvedResource)) {
				if (already2ndLevel)
					level2Actions.remove(action);
				level1Actions.add(action);
			} else if (!already2ndLevel && !already1stLevel) {
				level2Actions.add(action);
			}
			if (!actionContexts.containsKey(action))
				actionContexts.put(action, context);
		}
	}

	public abstract OptionPane getOptionPane();
	
	public String getPropertyPrefix() {
		return this.getClass().getPackage().getName();
	}
	
	public String getLabelPropertyName() {
		if (labelPropertyName == null)
			labelPropertyName = 
				getPropertyPrefix() + LABEL_SUFFIX;
		return labelPropertyName;
	}
	
	public String getActionPropertyName() {
		if (actionPropertyName == null)
			actionPropertyName = 
				getPropertyPrefix() + ACTION_SUFFIX;
		return actionPropertyName;
	}
	
	public String getDefaultLauncherPropertyName() {
		if (defaultLauncherPropertyName == null)
			defaultLauncherPropertyName = 
				OPT_BASE_PREFIX + getPropertyPrefix() + DEFAULT_SUFFIX;
		return defaultLauncherPropertyName;
	}
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public String[] getLauncherNames() {
		return ServiceManager.getServiceNames(SERVICE_NAME);
	}
	
	public Launcher[] getLaunchers() {
		String[] launcherNames = ServiceManager.getServiceNames(SERVICE_NAME);
		Launcher[] launchers = new Launcher[launcherNames.length];
		int i = 0;
		for (String launcherName: launcherNames) {
			launchers[i++] = getLauncher(launcherName);
		}
		return launchers;
	}
	
	public Launcher getLauncher(String name) {
		return (Launcher)ServiceManager.getService(SERVICE_NAME, name);
	}
	
	public String getLabel() {
		if (label == null)
			label = jEdit.getProperty(getLabelPropertyName(),
				"Error: No label defined for " + this.getClass().getName());
		return label;
	}

	public String getActionLabel(Object[] args) {
		if (actionLabel == null)
			actionLabel = jEdit.getProperty(
					getActionPropertyName() + LABEL_SUFFIX, args);
		if (actionLabel == null || actionLabel.trim().isEmpty())
			actionLabel =
					"Error: no label defined by " + getActionPropertyName() + LABEL_SUFFIX;
		return actionLabel;
	}

	public Launcher[] getDefaultLaunchersChoice() {
		return getLaunchers();
	}


	public Launcher getDefaultLauncher() {
		if (defaultLauncher == null) {
			String defaultLauncherName =  jEdit.getProperty(
					getDefaultLauncherPropertyName());
			defaultLauncher = defaultLauncherName == null ? null :
					(Launcher)ServiceManager.getService(
							SERVICE_NAME, defaultLauncherName);
		}
		return defaultLauncher;
	}

	public void setDefaultLauncher(Launcher defaultLauncher) {
		this.defaultLauncher = defaultLauncher;
        jEdit.setProperty(getDefaultLauncherPropertyName(),
        					defaultLauncher.getName());
	}

	private ActionSet getActionSet() {
		if (actionSet == null)
			actionSet = buildActionSet();
		return actionSet;
	}

	private void setActionSet(ActionSet actionSet) {
		if (this.actionSet != null && actionContext != null) {
			actionContext.removeActionSet(this.actionSet);
		}
		this.actionSet = actionSet;
		if (actionContext != null)
			actionContext.addActionSet(actionSet);
	}
	
	private void checkContext(ActionContext context) {
		if (context != null && !(context == LauncherActionContext.INSTANCE)) {
			String message = "Context <> LauncherActionContext.INSTANCE, " +
			getClass().getName() +
			" must override these methods:\n" +
			" - buildActionContextFor()\n" + 
			" - getActionContext()\n" +
			" - reload()\n" +
			"";
			Log.log(Log.ERROR, this, message);
			throw new IllegalStateException(message);
		}
	}

}

