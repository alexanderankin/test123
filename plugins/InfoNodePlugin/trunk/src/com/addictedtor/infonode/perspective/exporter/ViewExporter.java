package com.addictedtor.infonode.perspective.exporter;

import net.infonode.docking.View;

/**
 * Exporter for View. Handles the JComponent that lives in the view
 */
public class ViewExporter extends DefaultExporter implements Exporter {

	
	/**
	 * Constructor for the ViewExporter
	 * 
	 * @param window
	 *            The View to stream to XML
	 */
	public ViewExporter(View window) {
		super(window);
		setAttribute("title", window.getTitle());
	}


}
