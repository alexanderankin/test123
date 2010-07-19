package javadoc;
//{{{ imports
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.File;
import javax.swing.JOptionPane;

import infoviewer.InfoViewerPlugin;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
//}}}
public class JavadocPlugin extends EditPlugin {
	public void start() {}
	public void stop() {}
	
	public static void search(final View view, final String name) {
		view.getStatus().setMessage("Searching ... ");
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
						File cls = new File(pkgDir, name+".html");
						if (cls.exists()) {
							pathList.add(cls.getPath());
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
					InfoViewerPlugin.openURL(view, "file:/"+chosenDoc);
				}
				view.getStatus().setMessage("");
			}
		}.start();
	}
}
