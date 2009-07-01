package com.addictedtor.infonode.perspective.importer;

import net.infonode.docking.DockingWindow;

import org.w3c.dom.Node;

import com.addictedtor.infonode.JEditViewView;

/**
 * importer for the &lt;JEditView&gt; node
 */
public class JEditViewViewImporter extends ViewImporter implements Importer {

	/**
	 * Constructor for the JEditViewView Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public JEditViewViewImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
	}
	
	/**
	 * Directly get the view view from the snapshot since there is
	 * one and only one
	 */
	@Override
	public DockingWindow createWindow() {
		JEditViewView vv = snapshot.getWm().getViewView() ;
		return vv; 
	}
	
}
