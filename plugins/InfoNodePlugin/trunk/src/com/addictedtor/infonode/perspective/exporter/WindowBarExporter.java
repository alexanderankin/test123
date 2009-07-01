package com.addictedtor.infonode.perspective.exporter;

import net.infonode.docking.WindowBar;

/**
 * Exporter for WindowBar. Handles the direction
 */
public class WindowBarExporter extends DefaultExporter implements Exporter {

	/**
	 * Constructor for the WindowBarExporter
	 * 
	 * @param window
	 *            The WindowBar to stream to XML
	 */
	public WindowBarExporter(WindowBar window) {
		super(window);
		setAttribute("direction", window.getDirection().toString());
	}
}
