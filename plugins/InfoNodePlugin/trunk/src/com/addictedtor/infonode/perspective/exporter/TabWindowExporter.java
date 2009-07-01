package com.addictedtor.infonode.perspective.exporter;

import net.infonode.docking.TabWindow;

/**
 * Exporter for TabWindow. Handles tab area orientation and direction and stores
 * the index of the selected tab
 */
public class TabWindowExporter extends DefaultExporter implements Exporter {

	/**
	 * Constructor for the TabWindowExporter
	 * 
	 * @param window
	 *            The TabWindow to stream to XML
	 */
	public TabWindowExporter(TabWindow window) {
		super(window);
		setAttribute("tabAreaOrientation", window.getTabWindowProperties()
				.getTabbedPanelProperties().getTabAreaOrientation().toString());
		setAttribute("direction", window.getTabWindowProperties()
				.getTabProperties().getTitledTabProperties()
				.getNormalProperties().getDirection().toString());
		setAttribute("selected", ""
				+ window.getChildWindowIndex(window.getSelectedWindow()));
	}

}
