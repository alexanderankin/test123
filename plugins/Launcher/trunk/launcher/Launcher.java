package launcher;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public abstract class Launcher extends EditAction {
	
	public static final String ERR_LAUNCH_FAILED = LauncherPlugin.ERR_PREFIX + ".launch-failed";
	
	public static final String FIRST_LEVEL_SUFFIX = ".first-level";

	private boolean userDefined;
	
	private boolean firstLevel;
	
	private final boolean stateful;
	
	private Object resource = null;
	private boolean resourceNeverSet = true;

	public Launcher(String property, Object[] args) {
		this(property, args, false, false);
	}
	
	public Launcher(String property, Object[] args, boolean stateful, boolean userDefined) {
		super(property, args);
		this.stateful = stateful;
		this.userDefined = userDefined;
		firstLevel = jEdit.getBooleanProperty(name + FIRST_LEVEL_SUFFIX, false);
	}
	
	public String getShortLabel() {
		return args[0].toString();
	}
	
	@Override
	public void invoke(View view) {
		if (!isStateful()) {
			String message = "Plugin Bug: Launcher.invoke(View) called on a stateless launcher.";
			Log.log(Log.ERROR, this, message);
			throw new IllegalStateException(message);
		}
		Object resource = getResource();
		launch(view, resource);
	}
	
	public abstract boolean launch(View view, Object resource);
	
	public static void logFailedLaunch(Object source, Object resource, Throwable t) {
    	Log.log(Log.ERROR,
    			source,
    			jEdit.getProperty(ERR_LAUNCH_FAILED, new Object[]{resource.toString(), t.toString()}),
    			t);
	}

	public static void logFailedLaunch(Object source, Object resource, String message) {
    	Log.log(Log.ERROR,
    			source,
    			jEdit.getProperty(ERR_LAUNCH_FAILED, new Object[]{resource.toString(), message}));
	}
	
	public abstract boolean canLaunch(Object resolvedResource);
	
	@Override
	public String toString() {
		return getLabel();
	}

	public boolean isUserDefined() {
		return userDefined;
	}

	public boolean isFirstLevelFor(Object resolvedResource) {
		return firstLevel;
	}

	public void setFirstLevel(boolean firstLevel) {
		this.firstLevel = firstLevel;
		jEdit.setBooleanProperty(name + FIRST_LEVEL_SUFFIX, firstLevel);
	}
	
	@Override
	public String getCode() {
		Object resource = null;
		if (false)
				((LauncherPlugin)jEdit.getPlugin(
					launcher.LauncherPlugin.class.getName())).launch(
						getName(), resource);
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "((LauncherPlugin)jEdit.getPlugin(\"" +
					launcher.LauncherPlugin.class.getName() + "\")).launch(\"" + 
						getName() + "\", resource);";
	}


	public abstract String getCode(View view, Object resolvedResource);
	
	public boolean isStateful() {
		return stateful;
	}
	
	public Object getResource() {
		if (!isStateful()) {
			String message = "Launcher.getResource() called on a stateless launcher";
			Log.log(Log.ERROR, this, message);
			throw new IllegalStateException(message);
		} else if (resourceNeverSet) {
			String message = "Launcher.getResource(): resource was never set";
			Log.log(Log.ERROR, this, message);
			throw new IllegalStateException(message);
		}
		return resource; 
	}
	
	public void setResource(Object resource) {
		if (!isStateful()) {
			String message = "Launcher.setResource(Object) called on a stateless launcher";
			Log.log(Log.ERROR, this, message);
			throw new IllegalStateException(message);
		} 
		this.resource = resource; 
		this.resourceNeverSet = false;
	}
}
