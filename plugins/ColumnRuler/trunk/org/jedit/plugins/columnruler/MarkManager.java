package org.jedit.plugins.columnruler;

import java.awt.Color;
import java.util.*;

import org.gjt.sp.jedit.*;

import org.jedit.plugins.columnruler.event.*;

public class MarkManager {
	private static MarkManager instance;
	
	private List<StaticMark> marks;
	private List<MarkManagerListener> listeners;
	
	private MarkManager() {
		listeners = new ArrayList<MarkManagerListener>();
		loadMarks();
	}
	
	public void addMarkManagerListener(MarkManagerListener l) {
		listeners.add(l);
	}
	
	public void removeMarkManagerListener(MarkManagerListener l) {
		listeners.remove(l);
	}
	
	public void loadMarks() {
		marks = new ArrayList<StaticMark>();

		int i = 0;
		String name = jEdit.getProperty("options.columnruler.marks." + i + ".name");
		while (name != null) {
			StaticMark m = new StaticMark(name);
			m.setColumn(jEdit.getIntegerProperty("options.columnruler.marks." + i + ".column", 0));
			m.setColor(jEdit.getColorProperty("options.columnruler.marks." + i + ".color", Color.WHITE));
			m.setSize(jEdit.getIntegerProperty("options.columnruler.marks." + i + ".size", 1));
			marks.add(m);
			i++;
			name = jEdit.getProperty("options.columnruler.marks." + i + ".name");
		}

	}
	
	public void save() {
		int i = 0;
		for (i = 0; i < marks.size(); i++) {
			StaticMark mark = marks.get(i);
			jEdit.setProperty("options.columnruler.marks." + i + ".name", mark.getName());
			jEdit.setIntegerProperty("options.columnruler.marks." + i + ".column", mark.getColumn());
			jEdit.setColorProperty("options.columnruler.marks." + i + ".color", mark.getColor());
			jEdit.setBooleanProperty("options.columnruler.marks." + i + ".guide", mark.isGuideVisible());
		}
		jEdit.unsetProperty("options.columnruler.marks." + i + ".name");
	}
	
	public static MarkManager getInstance() {
		if (instance == null) {
			instance = new MarkManager();
		}
		
		return instance;
	}
	
	public void addMark(StaticMark m) {
		addMark(m, true);
	}
	
	public void addMark(StaticMark m, boolean notify) {
		marks.add(m);
		if (notify) {
			fireMarkAdded(m);
		}
	}
	
	public void removeMark(StaticMark m) {
		marks.remove(m);
		fireMarkRemoved(m);
	}
	
	public void removeAll() {
		marks.clear();
		fireMarksUpdated();
	}
	
	public boolean containsMark(StaticMark m) {
		return marks.contains(m);
	}
	
	public List<StaticMark> getMarks() {
		return marks;
	}
	
	public int getMarkCount() {
		return marks.size();
	}
	
	protected void fireMarkAdded(StaticMark m) {
		for (MarkManagerListener l : listeners) {
			l.markAdded(m);
		}
	}
	
	protected void fireMarkRemoved(StaticMark m) {
		for (MarkManagerListener l : listeners) {
			l.markRemoved(m);
		}
	}

	protected void fireMarksUpdated() {
		for (MarkManagerListener l : listeners) {
			l.marksUpdated();
		}
	}
	
}
