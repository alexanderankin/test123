package com.addictedtor.infonode.perspective.importer;

import net.infonode.docking.DockingWindow;

import org.w3c.dom.Node;

/**
 * Dummy importer, does nothing with the node and returns null as the docking
 * window
 */
public class DummyImporter implements Importer {

	/**
	 * Constructor for the Dummy Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public DummyImporter(Node node, Snapshot snapshot) {
	}

	/**
	 * dummy parse the xml node, returns null
	 * 
	 * @param node
	 *            the XML node to parse
	 * @return null
	 */
	public DockingWindow parse(Node node) {
		return null;
	}

}
