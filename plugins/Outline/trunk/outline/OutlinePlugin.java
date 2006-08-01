package outline;

import org.gjt.sp.jedit.*;

import errorlist.DefaultErrorSource;

/**
 * Core class of Outline plugin.
 *
 * @author     mace
 * @version    $Revision: 1.5 $
 */
public class OutlinePlugin extends EditPlugin {
	public static final String NAME = "outline";
	public static final String OPTION_PREFIX = "options.outline.";
	public static final String PROPERTY_PREFIX = "plugin.outline.";
	
	protected static DefaultErrorSource errorSource;

	public void start() {
		jEdit.getPlugin("errorlist.ErrorListPlugin", true);
		jEdit.getPlugin("sidekick.SideKickPlugin", true);
		
		errorSource = new DefaultErrorSource("OutlinePlugin");
	}


	public void stop()
	{
		errorSource = null;
	}
}

