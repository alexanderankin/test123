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
	

	public void start() {
		jEdit.getPlugin("errorlist.ErrorListPlugin", true);
		jEdit.getPlugin("sidekick.SideKickPlugin", true);
		
	}


	public void stop()
	{

	}
}

