package com.addictedtor.infonode.perspective.importer;

import java.util.Vector;

import net.infonode.docking.DockingWindow;

import org.w3c.dom.Node;

/**
 * importer for the &lt;View&gt; node
 */
public abstract class ViewImporter extends DefaultImporter implements Importer {

	/**
	 * the title of the view
	 */
	protected String title;

	/**
	 * Constructor for the View Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public ViewImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
		this.title = getAttribute("title");
	}


	/**
	 * Does nothing, as a View has no childs
	 */
	@Override
	public void processChilds(DockingWindow window, Vector<DockingWindow> childs) {}

	/**
	 * Indicates that the View has no childs
	 * 
	 * @return false
	 */
	@Override
	public boolean hasChilds() {
		return false;
	}

}
