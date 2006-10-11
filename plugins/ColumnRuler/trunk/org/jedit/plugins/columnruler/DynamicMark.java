package org.jedit.plugins.columnruler;

import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.TextArea;

public abstract class DynamicMark extends Mark implements EBComponent {
	protected String property;
	/** Mapping of the position of the mark on each ruler */
	protected Map<ColumnRuler, Integer> positionMap;
	
	public DynamicMark(String name, String property) {
		super(name);
		this.property = property;
		positionMap = new HashMap<ColumnRuler,Integer>();
		setVisible(jEdit.getBooleanProperty(property + ".visible", true));
		setGuideVisible(jEdit.getBooleanProperty(property + ".guide", false));
		
	}
	
	/**
	 * Returns the jEdit property prefix used to store information about the state of this Mark.
	 */
	public final String getPropertyPrefix() {
		return property;
	}
	
	public void activate(TextArea textArea) {
	}
	
	public void deactivate(TextArea textArea) {
	}

	/**
	 * Any final cleanup that a DynamicMark needs to do before the plugin is disabled can be done here.
	 */
	public void shutdown() {
	}
	
	public void handleMessage(EBMessage msg) {
	}
	
	public final int getColumn() {
		throw new UnsupportedOperationException();
	}
	
	public int getPositionOn(ColumnRuler ruler) {
		if (positionMap.containsKey(ruler)) {
			return positionMap.get(ruler);
		} else {
			return -1;
		}
	}
	
	public void setPositionOn(ColumnRuler ruler, int col) {
		positionMap.put(ruler, col);
	}
	
	public void removePositionOn(ColumnRuler ruler) {
		positionMap.remove(ruler);
	}
	
}
