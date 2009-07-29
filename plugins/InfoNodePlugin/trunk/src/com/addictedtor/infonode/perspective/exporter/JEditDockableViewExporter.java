package com.addictedtor.infonode.perspective.exporter;

import com.addictedtor.infonode.JEditDockableView;

/**
 * Exporter for View. Handles the JComponent that lives in the view
 */
public class JEditDockableViewExporter extends DefaultExporter implements
		Exporter {

	/**
	 * Constructor for the ViewExporter
	 * 
	 * @param window
	 *            The View to stream to XML
	 */
	public JEditDockableViewExporter(JEditDockableView window) {
		super(window);
		setAttribute("title", window.getName());
	}

}
