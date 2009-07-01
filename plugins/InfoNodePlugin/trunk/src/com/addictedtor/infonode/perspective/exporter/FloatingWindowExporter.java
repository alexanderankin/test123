package com.addictedtor.infonode.perspective.exporter;

import net.infonode.docking.FloatingWindow;

/**
 * Exporter for FloatingWindow. At the moment, this class is a placeholder as it
 * does not do more than the DefaultExporter
 */
public class FloatingWindowExporter extends DefaultExporter implements Exporter {

	/**
	 * Constructor for the FloatingWindowExporter
	 * 
	 * @param window
	 *            The FloatingWindow to stream to XML
	 */
	public FloatingWindowExporter(FloatingWindow window) {
		super(window);
	}
}
