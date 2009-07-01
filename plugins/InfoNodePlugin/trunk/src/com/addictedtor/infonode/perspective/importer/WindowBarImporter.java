package com.addictedtor.infonode.perspective.importer;

import java.util.Vector;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.WindowBar;

import org.w3c.dom.Node;

/**
 * importer for &lt;WindowBar&gt; nodes
 */
public class WindowBarImporter extends DefaultImporter implements Importer {

	/**
	 * Constructor for the WindowBar Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public WindowBarImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
	}

	/**
	 * Grabs the window bar from the current root window This implementation
	 * circumvents the missingness of public constructors for WindowBar
	 * 
	 * @return the window bar for the &lt;WindowBar&gt; node
	 */
	@Override
	public DockingWindow createWindow() {
		return snapshot.getWm().getRootWindow().getWindowBar(
				getDirection(getAttribute("direction")));
	}

	/**
	 * Indicates if the window bar has childs
	 * 
	 * @return false
	 */
	@Override
	public boolean hasChilds() {
		return true ;
	}

	/**
	 * Adds the child docking windows as tabs of the window
	 */
	@Override
	public void processChilds(DockingWindow window, Vector<DockingWindow> childs) {
		WindowBar wb = (WindowBar) window;
		for (int i = 0; i < childs.size(); i++) {
			DockingWindow dw = childs.get(i);
			wb.addTab(dw, i);
		}
	}

}
