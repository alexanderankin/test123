package javadoc;
//{{{ imports
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.File;
import javax.swing.JOptionPane;

import infoviewer.InfoViewerPlugin;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;
//}}}
public class JavadocPlugin extends EBPlugin {
	private static ActionSet apiCommands;
	
	public void start() {
		apiCommands = new ActionSet("Plugin: Javadoc - Api Trees");
		updateActions();
	}
	public void stop() {}
	
	public void handleMessage(EBMessage message) {
		if (message instanceof PropertiesChanged) {
			updateActions();
		}
	}
	
	public static EditAction getApiAction(String name) {
		return (EditAction) apiCommands.getAction(name);
	}
	
	public static void updateActions() {
		// TODO: This isn't quite working correctly, fix it
		jEdit.removeActionSet(apiCommands);
		apiCommands.removeAllActions();
		StringTokenizer tokenizer = new StringTokenizer(
			jEdit.getProperty("options.javadoc.path", ""), File.pathSeparator);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			try {
				apiCommands.addAction(ApiAction.create(token));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		jEdit.addActionSet(apiCommands);
	}
	
	/**
	 * Searches the list for the given unqualified class name,
	 * displaying it in InfoViewer if found
	 * @param view the view
	 * @param name the unqualified class name
	 */
	public static void search(final View view, final String name) {
		view.getStatus().setMessage("Searching ... ");
		// TODO: re-write this to use ThreadUtilities
		new Thread() {
			public void run() {
				String path = jEdit.getProperty("options.javadoc.path", "");
				StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
				ArrayList<String> pathList = new ArrayList<String>();
				while (tokenizer.hasMoreTokens()) {
					String dir = tokenizer.nextToken();
					Buffer packageList = jEdit.openTemporary(view, dir, "package-list", false);
					if (packageList == null || packageList.isNewFile()) {
						Log.log(Log.ERROR, JavadocPlugin.class, "Invalid API root: "+dir);
						continue;
					}
					for (int i = 0; i<packageList.getLineCount(); i++) {
						String pkg = packageList.getLineText(i).replace(".", File.separator);
						File pkgDir = new File(dir, pkg);
						String pkgDirPath = pkgDir.getPath();
						String[] pages = pkgDir.list();
						for (int j = 0; j<pages.length; j++) {
							if (pages[j].equalsIgnoreCase(name+".html"))
								pathList.add(MiscUtilities.constructPath(pkgDirPath, pages[j]));
						}
					}
				}
				if (pathList.size() == 0) {
					view.getStatus().setMessageAndClear("Class "+name+" not found");
					return;
				}
				String chosenDoc = null;
				if (pathList.size() > 1) {
					chosenDoc = (String) JOptionPane.showInputDialog(view,
						jEdit.getProperty("msg.javadoc.resolve-class.message"),
						jEdit.getProperty("msg.javadoc.resolve-class.title"),
						JOptionPane.QUESTION_MESSAGE, null, pathList.toArray(), pathList.get(0));
				} else {
					chosenDoc = pathList.get(0);
				}
				if (chosenDoc != null) {
					InfoViewerPlugin.openURL(view, new File(chosenDoc).toURI().toString());
				}
				view.getStatus().setMessage("");
			}
		}.start();
	}
	
	/**
	 * Extracts the title of an api from its index.html file
	 * @param path the root of the api tree
	 */
	public static String getApiName(String path) {
		Buffer buffer = jEdit.openTemporary(jEdit.getActiveView(), path, "index.html", false);
		if (buffer == null || buffer.isNewFile()) {
			return null;
		}
		int start = -1, end = -1;
		for (int i = 0; i<buffer.getLineCount(); i++) {
			String line = buffer.getLineText(i).toLowerCase();
			int j = line.indexOf("<title>");
			if (start == -1 && j != -1) {
				start = buffer.getLineStartOffset(i)+j+7;
			}
			j = line.indexOf("</title>");
			if (start != -1 && end == -1 && j != -1) {
				end = buffer.getLineStartOffset(i)+j;
			}
			if (start != -1 && end != -1) {
				break;
			}
		}
		return buffer.getText(start, end-start).trim();
	}
	
	/**
	 * Adds an api root to the list only if it is not already added
	 * @param api the path of the api to add
	 */
	public static void addApi(String api) {
		String path = jEdit.getProperty("options.javadoc.path", "");
		if (path.indexOf(api) != -1) {
			return;
		}
		if (path.length() > 0) {
			path += File.pathSeparator;
		}
		path += api;
		jEdit.setProperty("options.javadoc.path", path);
		jEdit.propertiesChanged();
	}
	
	/**
	 * Removes an api root from the list
	 * @param api the api to remove
	 */
	public static void removeApi(String api) {
		String path = jEdit.getProperty("options.javadoc.path", "");
		int index;
		if ((index = path.indexOf(api)) == -1) {
			return;
		}
		String sep = File.pathSeparator;
		path = path.substring(0, index)+path.substring(index+api.length());
		path = path.replace(sep+sep, sep);
		jEdit.setProperty("options.javadoc.path", path);
		jEdit.propertiesChanged();
	}
}
