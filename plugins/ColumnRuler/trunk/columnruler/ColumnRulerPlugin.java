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
 * @modified   $Date: 2004-02-09 22:21:38 $ by $Author: bemace $
 * @version    $Revision: 1.8 $
 */
public class ColumnRulerPlugin extends EBPlugin {
	private static Hashtable rulerMap = new Hashtable();
	public final static String NAME = "columnruler";
	public final static String OPTION_PREFIX = "options.columnruler.";
	public final static String PROPERTY_PREFIX = "plugin.columnruler.";

	/**
	 * Returns the ColumnRuler for the given text area, or null.
	 */
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
		if (getColumnRulerForTextArea(textArea) != null)
			return;
		ColumnRuler columnRuler = new ColumnRuler(textArea);
		textArea.addTopComponent(columnRuler);
		rulerMap.put(textArea, columnRuler);
	}

	private static void removeColumnRulerFromTextArea(JEditTextArea textArea) {
		ColumnRuler columnRuler = (ColumnRuler) rulerMap.get(textArea);
		textArea.removeTopComponent(columnRuler);
		rulerMap.remove(textArea);
	}

	//{{{ start/stop
	public void start() {
		//guides = new LineGuidesExtension();
		if (jEdit.getProperty("plugin.columnruler.ColumnRulerPlugin.activate").equals("startup")) {
			View[] views = jEdit.getViews();
			for (int i = 0; i < views.length; i++) {
				EditPane[] panes = views[i].getEditPanes();
				for(int j = 0; j < panes.length; j++) {
					addColumnRulerToTextArea(panes[j].getTextArea());
				}
			}
		}
		addNotify();
	}

	public void stop() {
		removeNotify();
		Enumeration keys = rulerMap.keys();
		while (keys.hasMoreElements()) {
			JEditTextArea textArea = (JEditTextArea) keys.nextElement();
			ColumnRuler ruler = getColumnRulerForTextArea(textArea);
			if (ruler != null) {
				ruler.removeAllMarks();
			}
			removeColumnRulerFromTextArea(textArea);
		}
	}
	//}}}

	/**
	 * Handles a message sent on the EditBus.
	 */
	public void handleMessage(EBMessage message) {
		if (jEdit.getProperty("plugin.columnruler.ColumnRulerPlugin.activate").equals("startup")) {
			if (message instanceof ViewUpdate) {
				ViewUpdate vu = (ViewUpdate) message;
				if (vu.getWhat().equals(ViewUpdate.CREATED)) {
					addColumnRulerToTextArea(vu.getView().getTextArea());
				}
			}
			if (message instanceof EditPaneUpdate) {
				EditPaneUpdate epu = (EditPaneUpdate) message;
				if (epu.getWhat().equals(EditPaneUpdate.CREATED)) {
					addColumnRulerToTextArea(epu.getEditPane().getTextArea());
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

