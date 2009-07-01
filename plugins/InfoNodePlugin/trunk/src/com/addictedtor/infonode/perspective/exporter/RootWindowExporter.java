package com.addictedtor.infonode.perspective.exporter;

import net.infonode.docking.RootWindow;

/**
 * Exporter for RootWindow. At the moment, this class is a placeholder as it
 * does not do more than the DefaultExporter
 */
public class RootWindowExporter extends DefaultExporter implements Exporter {

	/**
	 * Constructor for the RootWindowExporter
	 * 
	 * @param window
	 *            The RootWindow to stream to XML
	 */
	public RootWindowExporter(RootWindow window) {
		super(window);
	}
}
