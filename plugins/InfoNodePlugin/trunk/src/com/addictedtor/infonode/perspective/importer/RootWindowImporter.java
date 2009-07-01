package com.addictedtor.infonode.perspective.importer;

import java.util.Vector;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.WindowBar;

import org.w3c.dom.Node;

/**
 * importer for &lt;RootWindow&gt; nodes
 */
public class RootWindowImporter extends DefaultImporter implements Importer {

	/**
	 * Constructor for the RootWindow Importer
	 * 
	 * @param node
	 *            the node to parse
	 * @param snapshot
	 *            the snapshot of the current perspective
	 */
	public RootWindowImporter(Node node, Snapshot snapshot) {
		super(node, snapshot);
	}

	/**
	 * Creates the root window for the &lt;RootWindow&gt; node
	 * 
	 * @return the root window for the &lt;RootWindow&gt; node
	 */
	@Override
	public DockingWindow createWindow() {
		return snapshot.getRootWindow();
	}

	/**
	 * Adds the child docking windows as tabs of the window
	 */
	@Override
	public void processChilds(DockingWindow window, Vector<DockingWindow> childs) {
		RootWindow root = (RootWindow) window;
		for (int i = 0; i < childs.size(); i++) {
			DockingWindow dw = childs.get(i);
			// TODO: do something with the window bars
			if (!(dw instanceof WindowBar) && !(dw instanceof FloatingWindow)) {
				root.setWindow(dw);
			}
		}
	}

	/**
	 * Flush the unused views to their preferred window bar
	 */
	@Override
	public void finish(DockingWindow window) {
		// for( View v : snapshot.getViews() ){
		// try{
		// if( v != null && v.isMinimizable() ){
		// v.minimize( );
		// }
		// } catch( Exception e){ }
		// }
	}

}
