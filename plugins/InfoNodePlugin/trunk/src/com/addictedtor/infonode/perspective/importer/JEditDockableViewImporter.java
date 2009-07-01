package com.addictedtor.infonode.perspective.importer;

import net.infonode.docking.DockingWindow;

import org.w3c.dom.Node;

import com.addictedtor.infonode.JEditDockableView;

/**
 * importer for the &lt;JEditDockableView&gt; node
 */
public class JEditDockableViewImporter extends ViewImporter implements Importer {

	/**
	 * the name of the dockable window, taken from the name attribute of the
	 * node
	 */
	private String title;

	/**
	 * Constructor for the JEditDockableView Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public JEditDockableViewImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
		this.title = getAttribute("title");
	}

	@Override
	public DockingWindow createWindow() {
		JEditDockableView w = snapshot.getView(title); 
		if( w == null){
			w = new JEditDockableView(snapshot.getWm(), title) ;
		}
		return w; 
	}
	
}
