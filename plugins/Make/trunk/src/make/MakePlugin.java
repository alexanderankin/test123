package make;

import errorlist.*;
import java.util.LinkedList;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ServiceManager;

public class MakePlugin extends EBPlugin {
	private static DefaultErrorSource errors;
	private static LinkedList<BuildfileProvider> providers;
	
	public void start() {
		errors = new DefaultErrorSource("Make");
		ErrorSource.registerErrorSource(errors);
		
		providers = new LinkedList<BuildfileProvider>();
		String[] providerNames = ServiceManager.getServiceNames("make.BuildfileProvider");
		for (int i = 0; i<providerNames.length; i++) {
			providers.add((BuildfileProvider)ServiceManager.getService("make.BuildfileProvider", providerNames[i]));
		}
	}
	
	public void stop() {
		ErrorSource.unregisterErrorSource(errors);
	}
	
	public void handleMessage(EBMessage msg) {
		// if ProjectViewer is installed, handle its messages
		if (jEdit.getPlugin("projectViewer.ProjectPlugin") != null) {
			if (handleProjectMessage(msg))
				return;
		}
	}
	
	/**
	 * Handle ProjectViewer-specific events.
	 * @return true if it was a PV message, otherwise false
	 */
	private boolean handleProjectMessage(EBMessage msg) {
		return false;
	}
	
	public static Buildfile getBuildfileForPath(String dir, String name) {
		for (BuildfileProvider provider : providers) {
			if (provider.accept(name)) {
				return provider.createFor(dir, name);
			}
		}
		
		return null;
	}
	
	public static void addError(int type, String path, int line, int start, int end, String msg, LinkedList<String> extraMessages) {
		DefaultErrorSource.DefaultError err = new DefaultErrorSource.DefaultError(errors, type, path, line, start, end, msg);
		if (extraMessages != null) {
			for (String extra : extraMessages) {
				err.addExtraMessage(extra);
			}
		}
		
		errors.addError(err);
	}
	
	public static void clearErrors() {
		errors.clear();
	}
}
