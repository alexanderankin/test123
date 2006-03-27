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
 * @version    $Revision: 1.3 $ $Date: 2006-03-27 16:21:28 $ by $Author: bemace $
 */
public class ColumnRulerPlugin extends EBPlugin {
	private static Map<JEditTextArea,ColumnRuler> rulerMap = new HashMap<JEditTextArea,ColumnRuler>();
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
		if (rulerMap.containsKey(textArea)) {
			return;
		}
		ColumnRuler columnRuler = new ColumnRuler(textArea);
		textArea.addTopComponent(columnRuler);
		rulerMap.put(textArea, columnRuler);
		MarkManager.getInstance().addMarkManagerListener(columnRuler);
	}

	private static void removeColumnRulerFromTextArea(JEditTextArea textArea) {
		ColumnRuler columnRuler = rulerMap.get(textArea);
		MarkManager.getInstance().removeMarkManagerListener(columnRuler);
		textArea.removeTopComponent(columnRuler);
		rulerMap.remove(textArea);
	}

	//{{{ start/stop
	public void start() {
		for (DynamicMark mark : getDynamicMarks()) {
			EditBus.addToBus(mark);
			//Log.log(Log.DEBUG, this, "Adding "+mark.getName()+" to EditBus");
		}
		if (jEdit.getProperty("plugin.org.jedit.plugins.columnruler.ColumnRulerPlugin.activate").equals("startup")) {
			for (View view : jEdit.getViews()) {
				for (EditPane editPane : view.getEditPanes()) {
					addColumnRulerToTextArea(editPane.getTextArea());
				}
			}
			for (View view : jEdit.getViews()) {
				for (EditPane editPane : view.getEditPanes()) {
					for (DynamicMark mark : getDynamicMarks()) {	
						mark.activate(editPane);
					}
				}
			}
		}
		addNotify();
	}

	public void stop() {
		removeNotify();
		MarkManager.getInstance().save();
		JEditTextArea[] keys = rulerMap.keySet().toArray(new JEditTextArea[0]);
		for (JEditTextArea textArea : keys) {
			ColumnRuler ruler = getColumnRulerForTextArea(textArea);
			if (ruler != null) {
				textArea.getPainter().removeExtension(ruler.guideExtension);
			}
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
		if (jEdit.getProperty("plugin.org.jedit.plugins.columnruler.ColumnRulerPlugin.activate").equals("startup")) {
			if (message instanceof ViewUpdate) {
				ViewUpdate vu = (ViewUpdate) message;
				if (vu.getWhat().equals(ViewUpdate.CREATED)) {
					addColumnRulerToTextArea(vu.getView().getTextArea());
				}
				if (vu.getWhat().equals(ViewUpdate.CLOSED)) {
					//removeColumnRulerFromTextArea(vu.getView().getTextArea());
				}
			}
			if (message instanceof EditPaneUpdate) {
				EditPaneUpdate epu = (EditPaneUpdate) message;
				if (epu.getWhat().equals(EditPaneUpdate.CREATED)) {
					addColumnRulerToTextArea(epu.getEditPane().getTextArea());
				}
				if (epu.getWhat().equals(EditPaneUpdate.DESTROYED)) {
					//removeColumnRulerFromTextArea(epu.getEditPane().getTextArea());
				}
			}
			if (message instanceof EditorExitRequested) {
				MarkManager.getInstance().save();
				for (View view : jEdit.getViews()) {
					for (EditPane editPane : view.getEditPanes()) {
						for (DynamicMark mark : getDynamicMarks()) {
							mark.deactivate(editPane);
						}
					}
				}
			}
		}
	} //}}}
	
	public void addNotify() {
		EditBus.addToBus(this);
	}

	public void removeNotify() {
		EditBus.removeFromBus(this);
	}

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

