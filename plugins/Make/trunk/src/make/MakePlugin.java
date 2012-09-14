/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package make;

import errorlist.*;
import java.util.LinkedList;
import java.util.Iterator;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ServiceManager;

public class MakePlugin extends EBPlugin {
	private static DefaultErrorSource errors;
	private static LinkedList<BuildfileProvider> providers;
	public static StringBuilder output;
	
	public static final String TARGET_CACHE_PROPERTY = "make.cache";
	
	public void start() {
		errors = new DefaultErrorSource("Make");
		ErrorSource.registerErrorSource(errors);
		output = new StringBuilder();
		
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
	
	/**
	 * If there's a list of targets cached for the given path, return it.
	 * Otherwise return null.
	 */
	public static LinkedList<BuildTarget> getCachedTargets(String path) {
		String prop = TARGET_CACHE_PROPERTY + "." + path;
		LinkedList<BuildTarget> targets = new LinkedList<BuildTarget>();
		
		for (int i = 0; true; i++) {
			String name = jEdit.getProperty(prop + "." + i + ".name");
			if (name == null || name.length() == 0) {
				break;
			}
			String desc = jEdit.getProperty(prop + "." + i + ".desc");
			BuildTarget target = new BuildTarget(name, desc);
			for (int j = 0; true; j++) {
				String param = jEdit.getProperty(prop + "." + i + ".param." + j);
				if (param == null || param.length() == 0) {
					break;
				} else {
					target.params.add(param);
				}
			}
			
			targets.add(target);
		}
		
		return targets;
	}
	
	/**
	 * Cache a list of targets. These are saved as temporary properties, so they should
	 * only exist for this session.
	 */
	public static void cacheTargets(String path, LinkedList<BuildTarget> targets) {
		String prop = TARGET_CACHE_PROPERTY + "." + path;
		LinkedList<String> names = new LinkedList<String>();
		
		int i = 0;
		for (BuildTarget target : targets) {
			jEdit.setTemporaryProperty(prop + "." + i + ".name", target.name);
			jEdit.setTemporaryProperty(prop + "." + i + ".desc", target.desc);
			int j = 0;
			for (String p : target.params) {
				jEdit.setTemporaryProperty(prop + "." + i + ".param." + j, p);
				j++;
			}
			i++;
		}
	}
	
	public static void clearCachedTargets(String path) {
		String prop = TARGET_CACHE_PROPERTY + "." + path;
		for (int i = 0; true; i++) {
			if (jEdit.getProperty(prop + "." + i + ".name") == null) {
				break;
			}
			
			jEdit.unsetProperty(prop + "." + i + ".name");
			jEdit.unsetProperty(prop + "." + i + ".desc");
			for (int j = 0; true; j++) {
				String paramProp = prop + "." + i + ".param." + j;
				if (jEdit.getProperty(paramProp, "").length() == 0) {
					break;
				}
				jEdit.unsetProperty(paramProp);
			}
		}
	}
	
	public static void clearOutput() {
		output = new StringBuilder();
	}
	
	public static void writeToOutput(String line) {
		output.append(line + System.getProperty("line.separator"));
		
		OutputPanel panel = (OutputPanel)jEdit.getActiveView().getDockableWindowManager().getDockable("make.outputPanel");
		if (panel != null) {
			String str = output.toString();
			panel.textArea.setText(str);
			panel.textArea.setCaretPosition(str.length());
		}
	}
}
