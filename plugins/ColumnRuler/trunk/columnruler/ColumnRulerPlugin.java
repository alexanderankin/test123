package columnruler;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 5, 2003
 * @modified   $Date: 2004-01-15 19:47:36 $ by $Author: bemace $
 * @version    $Revision: 1.4 $
 */
public class ColumnRulerPlugin extends EBPlugin {
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
	}

	public void start() {
		addNotify();
	}

	public void stop() {
		removeNotify();
		Enumeration keys = rulerMap.keys();
		while (keys.hasMoreElements()) {
			JEditTextArea textArea = (JEditTextArea) keys.nextElement();
			removeColumnRulerFromTextArea(textArea);
		}
	}

	/**
	 * Handles a message sent on the EditBus.
	 */
	public void handleMessage(EBMessage message) {
		if (jEdit.getProperty("plugin.columnruler.ColumnRulerPlugin.activate").equals("startup")) {
			if (message instanceof ViewUpdate) {
				ViewUpdate vu = (ViewUpdate) message;
				if (vu.getWhat().equals(ViewUpdate.CREATED)) {
					addColumnRulerToTextArea(vu.getView().getTextArea());
					Log.log(Log.DEBUG,this,"Added ruler");
				}
			}
		}
	}

	public void addNotify() {
		EditBus.addToBus(this);
	}

	public void removeNotify() {
		EditBus.removeFromBus(this);
	}

}

