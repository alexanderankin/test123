package com.addictedtor.infonode.perspective.importer;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.w3c.dom.Node;

/**
 * importer for the &lt;FloatingWindow&gt; node
 */
public class FloatingWindowImporter extends DefaultImporter implements Importer {

	/**
	 * Constructor for the FloatingWindow Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public FloatingWindowImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
	}

	/**
	 * Creates the floating window a dummy Docking window which will change in
	 * the processChilds method
	 * 
	 * @return the floating window for the &lt;FloatingWindow&gt; node
	 */
	@Override
	public DockingWindow createWindow() {
		RootWindow root = snapshot.getRootWindow();

		FloatingWindow window = root.createFloatingWindow(new Point(0, 0),
				new Dimension(400, 400), new View("", null, null));
		return window;
	}

	/**
	 * Adds the child docking windows as tabs of the window
	 */
	@Override
	public void processChilds(DockingWindow window, Vector<DockingWindow> childs) {
		FloatingWindow fw = (FloatingWindow) window;
		fw.setWindow(childs.get(0));
	}

}
