package recentbuffer;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.*;

/**
 * The RecentBufferSwitcher plugin, shows a dialog of most recently uses buffer
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1.1.1 $ $Date: 2005/10/06 13:51:34 $
 */
public class RecentBufferSwitcherPlugin extends EditPlugin
{
	public static final String NAME = "recentbufferswitcher";
	public static final String OPTION_PREFIX = "options.recentbufferswitcher.";
	public static BufferAccessMonitor bufAccessObj = new BufferAccessMonitor();
	
	/**
	 * Default Constructor for the <tt>RecentBufferSwitcherPlugin</tt> object
	 */
	public void RecentBufferSwitcherPlugin() {}
	
	public static void openBufferSwitcher(View view) {		
		RecentBuffer rb = new RecentBuffer(jEdit.getActiveView(), bufAccessObj);
	}
}
