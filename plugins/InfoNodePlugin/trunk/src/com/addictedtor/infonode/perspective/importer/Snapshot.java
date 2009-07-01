package com.addictedtor.infonode.perspective.importer;

import java.util.HashMap;

import org.gjt.sp.util.Log;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import com.addictedtor.infonode.InfoNodeDockingWindowManager;
import com.addictedtor.infonode.JEditDockableView;
import com.addictedtor.infonode.JEditViewView;

/**
 * Stores the Views of the root window
 */
public class Snapshot {

	/**
	 * Stores the existing views 
	 */
	private HashMap<String, JEditDockableView> views;

	/**
	 * The RootWindow
	 */
	private RootWindow root;
	
	private InfoNodeDockingWindowManager wm; 
	
	/**
	 * Constructor for the views snapshot, search recursively the root window
	 * for views
	 * 
	 * @param root
	 *            the root window to make a snapshot from
	 */
	public Snapshot(InfoNodeDockingWindowManager wm) {
		this.wm = wm; 
		this.root = wm.getRootWindow() ;
		views = new HashMap<String, JEditDockableView>();
		search(root);
	}

	/**
	 * Search recursively a docking window
	 * 
	 * @param window
	 *            a docking window from which we want to store the views
	 */
	public void search(DockingWindow window) {
		
		if( window instanceof JEditViewView ){
		} else if (window instanceof JEditDockableView) {
			String title = window.getTitle(); 
			views.put( title, (JEditDockableView)window ) ;
			Log.log( Log.ERROR, this, window ) ;
		} else if( window instanceof View ){
			// should not happen
		} else { 
			for (int i = 0; i < window.getChildWindowCount(); i++) {
				search(window.getChildWindow(i));
			}
		}
	}

	/**
	 * Retrieves a view of the given title and removes it from the snapshot
	 * 
	 * @param title
	 *            the title of the view
	 * @return the view having the given title
	 */
	public JEditDockableView getView(String title) {
		if (views.size() == 0 || !views.containsKey(title)){
			return null;
		}
		return views.get(title) ; 
	}

	/**
	 * The root window
	 * 
	 * @return the root window
	 */
	public RootWindow getRootWindow() {
		return root;
	}
	
	public InfoNodeDockingWindowManager getWm(){
		return wm; 
	}

}
