package com.addictedtor.infonode.perspective.importer;

import net.infonode.docking.DockingWindow;

import org.w3c.dom.Node;

/**
 * Interface used to parse an XML representation of the perspective and create
 * the docking window
 */
public interface Importer {

	/**
	 * Creates a DockingWindow from the xml node
	 * 
	 * @param node
	 *            the xml node to parse
	 * @return the docking window representing the node
	 */
	public abstract DockingWindow parse(Node node);

}
