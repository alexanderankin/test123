package columnruler;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 5, 2003
 * @modified   $Date: 2003-06-06 06:46:18 $ by $Author: bemace $
 * @version    $Revision: 1.2 $
 */
public class ColumnRulerPlugin extends EditPlugin {
	private static Hashtable rulerMap = new Hashtable();
	public final static String NAME = "columnruler";
	public final static String OPTION_PREFIX = "options.columnruler.";
	public final static String PROPERTY_PREFIX = "plugin.columnruler.";

	public static ColumnRuler getColumnRulerForTextArea(JEditTextArea textArea) {
		return (textArea != null) ? (ColumnRuler) rulerMap.get(textArea) : null;
	}


	public static void toggleColumnRulerForTextArea(JEditTextArea textArea) {
		boolean vis = !(rulerMap.get(textArea) != null);
		if (vis) {
			addColumnRulerToTextArea(textArea);
		} else {
			removeColumnRulerFromTextArea(textArea);
		}
	}

	private static void addColumnRulerToTextArea(JEditTextArea textArea) {
		ColumnRuler columnRuler = new ColumnRuler(textArea);
		textArea.addTopComponent(columnRuler);
		rulerMap.put(textArea, columnRuler);
	}

	private static void removeColumnRulerFromTextArea(JEditTextArea textArea) {
		ColumnRuler columnRuler = (ColumnRuler) rulerMap.get(textArea);
		textArea.removeTopComponent(columnRuler);
		rulerMap.remove(textArea);
		columnRuler.destroy();
	}

	public void stop() {
		Enumeration keys = rulerMap.keys();
		while (keys.hasMoreElements()) {
			JEditTextArea textArea = (JEditTextArea) keys.nextElement();
			removeColumnRulerFromTextArea(textArea);
		}
	}
}

