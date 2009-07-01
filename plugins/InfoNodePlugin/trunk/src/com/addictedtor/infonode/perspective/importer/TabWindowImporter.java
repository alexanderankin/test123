package com.addictedtor.infonode.perspective.importer;

import java.util.Vector;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.TabWindow;

import org.w3c.dom.Node;

/**
 * importer for &lt;TabWindow&gt; nodes
 */
public class TabWindowImporter extends DefaultImporter implements Importer {

	/**
	 * Constructor for the TabWindow Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public TabWindowImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
	}

	/**
	 * Creates the (possibly empty) docking window for the current node
	 * 
	 * @return the docking window for the current node
	 */
	@Override
	public DockingWindow createWindow() {
		TabWindow window = new TabWindow();
		String direction = getAttribute("direction");

		if (direction != null) {
			window.getTabWindowProperties().getTabProperties()
					.getTitledTabProperties().getNormalProperties()
					.setDirection(getDirection(direction));
		}

		String tabAreaOrientation = getAttribute("tabAreaOrientation");
		if (tabAreaOrientation != null) {
			window.getTabWindowProperties().getTabbedPanelProperties()
					.setTabAreaOrientation(getDirection(tabAreaOrientation));
		}

		return window;
	}

	/**
	 * Adds the child docking windows as tabs of the window
	 */
	@Override
	public void processChilds(DockingWindow window, Vector<DockingWindow> childs) {
		TabWindow tw = (TabWindow) window;
		for (int i = 0; i < childs.size(); i++) {
			DockingWindow dw = childs.get(i);
			tw.addTab(dw, i);
		}
	}

	/**
	 * sets the selected docking window
	 */
	@Override
	public void finish(DockingWindow window) {
		TabWindow tw = (TabWindow) window;
		int selected;
		try {
			Integer sel = Integer.parseInt(getAttribute("selected"));
			selected = sel.intValue();
		} catch (Exception e) {
			selected = 0;
		}
		tw.setSelectedTab(selected);

	}

}
