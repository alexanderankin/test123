package org.jedit.plugins.columnruler;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

/**
 * Core class of ColumnRuler plugin.
 *
 * @author     mace
 * @version    $Revision: 1.5 $ $Date: 2006-10-11 16:36:03 $ by $Author: k_satoda $
 */
public class ColumnRulerPlugin extends EBPlugin {
	private static Map<TextArea,ColumnRuler> rulerMap = new HashMap<TextArea,ColumnRuler>();
	public final static String NAME = "columnruler";
	public final static String OPTION_PREFIX = "options.columnruler.";
	public final static String PROPERTY_PREFIX = "plugin.columnruler.";

	/**
	 * Returns the ColumnRuler for the given text area, or null.
	 */
	public static ColumnRuler getColumnRulerForTextArea(TextArea textArea) {
		return (textArea != null) ? (ColumnRuler) rulerMap.get(textArea) : null;
	}

	public static void toggleColumnRulerForTextArea(TextArea textArea) {
		boolean vis = !(rulerMap.get(textArea) != null);
		if (vis) {
			addColumnRulerToTextArea(textArea);
		} else {
			removeColumnRulerFromTextArea(textArea);
		}
	}

	private static void addColumnRulerToTextArea(TextArea textArea) {
		if (rulerMap.containsKey(textArea)) {
			Log.log(Log.DEBUG, textArea, "Redundant call for addColumnRulerToTextArea()");
			return;
		}
		ColumnRuler columnRuler = new ColumnRuler(textArea);
		rulerMap.put(textArea, columnRuler);
		textArea.addTopComponent(columnRuler);
		MarkManager.getInstance().addMarkManagerListener(columnRuler);
		for (DynamicMark mark : getDynamicMarks()) {	
			mark.activate(textArea);
		}
	}

	private static void removeColumnRulerFromTextArea(TextArea textArea) {
		ColumnRuler columnRuler = rulerMap.get(textArea);
		if (columnRuler == null) {
			return;
		}
		for (DynamicMark mark : getDynamicMarks()) {
			mark.removePositionOn(columnRuler);
			mark.deactivate(textArea);
		}
		MarkManager.getInstance().removeMarkManagerListener(columnRuler);
		textArea.removeTopComponent(columnRuler);
		rulerMap.remove(textArea);
	}

	private static boolean isActiveByDefault() {
		return jEdit.getProperty("plugin.org.jedit.plugins.columnruler.ColumnRulerPlugin.activate").equals("startup");
	}

	//{{{ start/stop
	public void start() {
		for (DynamicMark mark : getDynamicMarks()) {
			EditBus.addToBus(mark);
		}
		if (isActiveByDefault()) {
			for (View view : jEdit.getViews()) {
				for (EditPane editPane : view.getEditPanes()) {
					addColumnRulerToTextArea(editPane.getTextArea());
				}
			}
		}
	}

	public void stop() {
		MarkManager.getInstance().save();
		
		TextArea[] keys = rulerMap.keySet().toArray(new TextArea[0]);
		for (TextArea textArea : keys) {
			removeColumnRulerFromTextArea(textArea);
		}
		
		for (DynamicMark mark : getDynamicMarks()) {
			EditBus.removeFromBus(mark);
			mark.shutdown();
		}
		
	}
	//}}}

	//{{{ handleMessage
	/**
	 * Handles a message sent on the EditBus.
	 */
	public void handleMessage(EBMessage message) {
		if (message instanceof EditPaneUpdate) {
			EditPaneUpdate epu = (EditPaneUpdate) message;
			EditPane editPane = epu.getEditPane();
			if (epu.getWhat().equals(EditPaneUpdate.CREATED) && isActiveByDefault()) {
				addColumnRulerToTextArea(editPane.getTextArea());
			}
			if (epu.getWhat().equals(EditPaneUpdate.DESTROYED)) {
				removeColumnRulerFromTextArea(editPane.getTextArea());
			}
		}
	} //}}}

	public static List<DynamicMark> getDynamicMarks() {
		List<DynamicMark> marks = new ArrayList<DynamicMark>();
		String[] services = ServiceManager.getServiceNames("org.jedit.plugins.columnruler.DynamicMark");
		for (String service : services) {
			DynamicMark mark = (DynamicMark) ServiceManager.getService("org.jedit.plugins.columnruler.DynamicMark", service);
			marks.add(mark);
		}
		
		return marks;
	}
	
}

