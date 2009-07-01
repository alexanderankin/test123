package com.addictedtor.infonode;

import net.infonode.docking.RootWindow;
import net.infonode.docking.WindowBar;
import net.infonode.util.Direction;

import org.gjt.sp.jedit.gui.DockableWindowManager.DockingArea;

public class InfoNodeDockingArea implements DockingArea {

	private WindowBar bar ;
	private InfoNodeTools tools ; 
	private InfoNodeDockingWindowManager wm ;
	private Direction direction; 
	RootWindow root ; 
	
	public InfoNodeDockingArea(Direction direction, InfoNodeDockingWindowManager wm ){
		this.wm = wm; 
		this.direction = direction ; 
		root = null; 
	}
	
	@Override
	public String getCurrent() {
		// TODO get the currently focused dockable that is a child of the area
		return null;
	}

	@Override
	public String[] getDockables() {
		if( root == null){
			init() ; 
		}
		return tools.getDockablesAsArray() ;
	}

	@Override
	public void show(String name) {
		// layout.getWm(). ;
	}

	@Override
	public void showMostRecent() {
	}

	public WindowBar getBar() {
		if( root == null){
			init(); 
		}
		return bar; 
	}
	
	public void init(){
		root = wm.getRootWindow() ;
		if( root != null){
			bar = root.getWindowBar(direction) ;
			bar.setEnabled(true) ;
			tools = new InfoNodeTools( bar ) ;
		}
	}
	
}
