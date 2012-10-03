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
import java.io.File;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;
import projectviewer.ProjectViewer;
import projectviewer.vpt.*;

public class MakePlugin extends EditPlugin {
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
	
	public static Buildfile getBuildfileForPath(String dir, String name) {
		for (BuildfileProvider provider : providers) {
			if (provider.accept(name)) {
				return provider.createFor(dir, name);
			}
		}
		return null;
	}
	
	public static Buildfile getBuildfileForFile(VPTFile fileNode) {
		File file = new File(fileNode.getNodePath());
		return MakePlugin.getBuildfileForPath(MiscUtilities.getParentOfPath(file.getParent()), file.getName());
	}
	
	public static Buildfile getBuildfileForProject(VPTProject project) {
		Buildfile file = null;
		File root = new File(project.getRootPath());
		File[] files = root.listFiles();
		if (files != null) {
			for (int i = 0; i<files.length; i++) {
				if ((file = MakePlugin.getBuildfileForPath(files[i].getParent(), files[i].getName())) != null) {
					return file;
				}
			}
		}
		return null;
	}
	
	public static Buildfile getBuildfileForDirectory(VPTDirectory directory) {
		Buildfile file = null;
		File dir = new File(directory.getNodePath());
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i<files.length; i++) {
				if ((file = MakePlugin.getBuildfileForPath(files[i].getParent(), files[i].getName())) != null) {
					return file;
				}
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
	
	public static void makeProject(View view) {
		ProjectViewer viewer = projectviewer.ProjectViewer.getViewer(view);
		if (viewer == null) {
			GUIUtilities.error(view, "make.msg.no-project-found", new String[] {});
			return;
		}
		
		VPTProject project = viewer.getActiveProject(view);
		if (project == null) {
			GUIUtilities.error(view, "make.msg.no-project-found", new String[] {});
			return;
		}
		
		Buildfile buildfile = make.MakePlugin.getBuildfileForProject(project);
		if (buildfile == null) {
			GUIUtilities.error(view, "make.msg.no-buildfile-found", new String[] {});
			return;
		}
		
		BuildTarget[] targets = new BuildTarget[buildfile.targets.size()];
		int i = 0;
		for (BuildTarget target : buildfile.targets) {
			targets[i++] = target;
		}
		
		BuildTarget def = targets[0];
		String defName = jEdit.getProperty("make.make-project.default." + buildfile.getPath(), "");
		if (defName.length() > 0) {
			for (i = 0; i<targets.length; i++) {
				if (defName.equals(targets[i].name)) {
					def = targets[i];
					break;
				}
			}
		}
		
		BuildTarget t = (BuildTarget)JOptionPane.showInputDialog(view,
		    	jEdit.getProperty("make.msg.make-project.message", "Target to run:"),
		    	jEdit.getProperty("make.msg.make-project.title", "Build Project"),
		    	JOptionPane.PLAIN_MESSAGE, null, targets, def);
		
		if (t != null) {
			buildfile.runTarget(t);
			jEdit.setTemporaryProperty("make.make-project.default." + buildfile.getPath(), t.name);
		}
	}
}
