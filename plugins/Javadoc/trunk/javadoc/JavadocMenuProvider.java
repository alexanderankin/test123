package javadoc;
/**
 * @author Damien Radtke
 * class JavadocMenuProvider
 * Dynamically updates the "View" plugin menu for viewing apis
 */
//{{{ Imports
import infoviewer.InfoViewerPlugin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.menu.DynamicMenuProvider;
//}}}
public class JavadocMenuProvider implements DynamicMenuProvider {
	
	public boolean updateEveryTime() {
		return false;
	}
	
	public void update(JMenu superMenu) {
		JMenu viewMenu = new JMenu(jEdit.getProperty("javadoc-view.label"));
		StringTokenizer tokenizer = new StringTokenizer(
			jEdit.getProperty("options.javadoc.path", ""), File.pathSeparator);
		boolean empty = true;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String name = JavadocPlugin.getApiName(token);
			String action = ApiAction.actionName(name);
			System.out.println("action = "+action);
			System.out.println(JavadocPlugin.getApiAction(action));
			JMenuItem item = GUIUtilities.loadMenuItem(JavadocPlugin.getApiAction(action), false);
			item.setLabel(name);
			/*
			item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						InfoViewerPlugin.openURL(jEdit.getActiveView(), url);
					}
			});
			*/
			viewMenu.add(item);
			empty = false;
		}
		viewMenu.setEnabled(!empty);
		superMenu.add(viewMenu, 0);
	}
}
