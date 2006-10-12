package org.jedit.plugins.columnruler.event;

import org.jedit.plugins.columnruler.*;

public interface MarkManagerListener {
	
	public void markAdded(StaticMark m);
	
	public void markRemoved(StaticMark m);
	
	public void marksUpdated();
	
	public void guidesUpdated();
	
}
