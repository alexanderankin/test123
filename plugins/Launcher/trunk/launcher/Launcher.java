package launcher;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.JEditAbstractEditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * The base class for all Launchers, extending jEdit's action base class
 * {@link EditAction}. All Launchers must extend this abstract class.
 * <p>
 * A Launcher encapsulate the logic of launching one or more resources
 * within its {@link #launch(View, Object)} method. The object passed
 * as parameter represents the resource to be launched (e.g. the
 * selected file). The resource can be of any type, including array and
 * {@link Collection}, however a Launcher can only apply its logic to a
 * type it supports. Therefore one of the first thing done in the
 * {@link #canLaunch(Object)} is to resolve the type of the resource to
 * something it can launch, which most of the time should be a file.
 * When resolving the resource to a {@link File} or {@link URI}, one
 * can use the various <code>resolveTo</code> methods from the
 * {@link LauncherUtils} class.
 * <p>
 * If possible a Launcher should be designed in a way that makes it autonomous,
 * so that all is needed to use it is to instantiate it and call its
 * {@link #launch(View, Object)} method. Moreover, like most
 * {@link EditAction}s, Launchers are typically stateless: the same instance
 * can be reused across multiple launches.
 * <p>
 * However the way in which the super class {@link JEditAbstractEditAction}
 * is coded shows that it stores a <code>Object[]</code> initialized via
 * constructor and overwritten with the argument passed to
 * {@link JEditAbstractEditAction#invoke(View, Object[])}.
 * That method is final and is just a delegation to
 * {@link JEditAbstractEditAction#invoke(View)}, so it's not really clear
 * whether these objects are meant to be parameters for the action
 * logic. In fact, {@link EditAction#getLabel()} uses them to compute the
 * label by giving them as parameters to
 * {@link jEdit#getProperty(String, Object[])}.
 * <p>
 * Therefore in order to avoid this stateful/stateless ambiguity, Launchers
 * are implemented to be stateless by default. Unless the relevant contructor
 * is used to mark it as stateful, the use of state-related methods such as 
 * {@link #setResource(Object)}, {@link #getResource()}, {@link #launch(View)},
 * and {@link #invoke(View)} will throw an {@link IllegalStateException}.
 * Also the default implementation of {@link EditAction#getLabel()} and
 * {@link EditAction#getName()} are reused, therefore the name of a
 * Launcher is used as prefix to build the name of the property storing its
 * label (&lt;name&gt;.label). This also means the label property can be
 * parameterized as supported by {@link jEdit#getProperty(String, Object[])}.
 * The methods {@link #getLabel()} and {@link #getShortLabel()} are the only
 * ones using the <code>Object[]</code> argument passed to the constructor.
 * Sublasses are free to override these methods and specify their own
 * convention for what the <code>Object[]</code> parameter should contain.
 * <p>
 * 
 * @author François Rey
 *
 */
public abstract class Launcher extends EditAction {
	
	private static final String ERR_LAUNCH_FAILED = LauncherPlugin.ERR_PREFIX + ".launch-failed";
	
	private static final String FIRST_LEVEL_SUFFIX = ".first-level";

	private boolean userDefined;
	
	private boolean firstLevel;
	
	private final boolean stateful;
	
	private Object resource = null;
	private boolean resourceNeverSet = true;

	/**
	 * The most basic constructor that creates a stateless Launcher.
	 * @param property The name of the Launcher which is used as prefix
	 * for building the label property name (&lt;name&gt;.label).
	 * This name must be unique across all defined Launchers.
	 * @param args the arguments for the launcher. Default behavior is
	 * to pass this array to {@link jEdit#getProperty(String, Object[])}
	 * when retrieving the label (see {@link EditAction#getLabel()}).
	 * Parameterization helps in making sure the label is unique
	 * otherwise the list of Launchers in the GUI will contain duplicates.
	 * Default behavior is also to use the first element in the array
	 * as short label (using its <code>toString()</code> method, see
	 * {@link #getShortLabel()}).
	 */
	public Launcher(String property, Object[] args) {
		this(property, args, false, false);
	}
	
	/**
	 * Fully specified constructor that allows the creation of stateful
	 * Launchers. Another flag is needed to indicate whether the
	 * new Launcher corresponds to a user entry in the GUI.
	 * It's important to distinguish these from other Launchers mainly
	 * because they need to be persisted. 
	 * @param property The name of the Launcher which is used as prefix
	 * for building the label property name (&lt;name&gt;.label).
	 * This name must be unique across all defined Launchers.
	 * @param args the arguments for the launcher. Default behavior is
	 * to pass this array to {@link jEdit#getProperty(String, Object[])}
	 * when retrieving the label (see {@link EditAction#getLabel()}).
	 * Parameterization helps in making sure the label is unique
	 * otherwise the list of Launchers in the GUI will contain duplicates.
	 * Default behavior is also to use the first element in the array
	 * as short label (using its <code>toString()</code> method, see
	 * {@link #getShortLabel()}).
	 * @param stateful true if this Launcher is to be stateful, thus enabling the use
	 * of state-related methods.
	 * @param userDefined true if this Launcher is created by the user in the GUI.
	 * This flag is intended to be used by the persistence logic for user-defined
	 * Launchers.
	 */
	public Launcher(String property, Object[] args, boolean stateful, boolean userDefined) {
		super(property, args);
		this.stateful = stateful;
		this.userDefined = userDefined;
		firstLevel = jEdit.getBooleanProperty(name + FIRST_LEVEL_SUFFIX, false);
	}
	
	/**
	 * Returns the short label for this launcher.
	 * Default implementation is to call <code>toString()</code> on the first
	 * object from the <code>Object[]</code> array passed to the constructor.
	 * @return the short label or null if not defined.
	 */
	public String getShortLabel() {
		return args == null || args.length == 0 ? null : args[0].toString();
	}
	
	public boolean launch(View view) {
		if (!isStateful()) {
			String message = "Plugin Bug: Launcher.invoke(View) called on a stateless launcher.";
			Log.log(Log.ERROR, this, message);
			throw new IllegalStateException(message);
		}
		Object resource = getResource();
		return launch(view, resource);
	}
	
	@Override
	public void invoke(View view) {
		launch(view);
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
