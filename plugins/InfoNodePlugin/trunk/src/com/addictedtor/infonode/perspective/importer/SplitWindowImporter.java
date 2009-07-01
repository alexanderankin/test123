package com.addictedtor.infonode.perspective.importer;

import java.util.Vector;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;

import org.w3c.dom.Node;

/**
 * importer for &lt;SplitWindow&gt; nodes
 */
public class SplitWindowImporter extends DefaultImporter implements Importer {

	/**
	 * Constructor for the SplitWindow Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public SplitWindowImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
	}

	/**
	 * Creates the empty SplitWindow for the current node
	 * 
	 * @return the split window for the current node
	 */
	@Override
	public DockingWindow createWindow() {
		SplitWindow window = new SplitWindow(true);

		String hor = getAttribute("horizontal");
		boolean horizontal = (hor == null || hor.equals("true"));
		window.setHorizontal(horizontal);

		String dividerLocation = getAttribute("dividerLocation");
		try {
			window.setDividerLocation(Float.valueOf(dividerLocation)
					.floatValue());
		} catch (Exception e) {
		}
		return window;
	}

	/**
	 * Sets the child windows (assuming childs has two elements)
	 */
	@Override
	public void processChilds(DockingWindow window, Vector<DockingWindow> childs) {
		SplitWindow sw = (SplitWindow) window;
		sw.setWindows(childs.get(0), childs.get(1));
	}

}
