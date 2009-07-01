package com.addictedtor.infonode;

import java.util.Vector;

import net.infonode.docking.DockingWindow;

public class InfoNodeTools {

	private Vector<String> dockables ; 
	
	private DockingWindow window; 
	
	public InfoNodeTools(DockingWindow window){
		this.window = window ;
		dockables = null; 
	}
	
	public Vector<String> getDockables(){
		dockables = new Vector<String>(); 
		navigate( window ) ;
		return dockables ; 
	}
	
	public String[] getDockablesAsArray(){
		getDockables() ;
		if( dockables == null | dockables.size() == 0){
			return null; 
		}
		String[] out = new String[dockables.size() ] ;
		dockables.toArray(out); 
		return out; 
	}
	
	private void navigate(DockingWindow window){
		if( window instanceof JEditDockableView){
			dockables.add( window.getTitle() ) ;
		} else{
			int count = window.getChildWindowCount() ; 
			if( count > 0){
				for( int i=0; i<count; i++){
					navigate( window.getChildWindow(i) ) ;
				}
			}
		}
	}
	
	public DockingWindow search( DockingWindow window, Class<? extends DockingWindow> clazz ){
		if( clazz.isAssignableFrom(window.getClass()) ){
			return window ; 
		} else{
			int count = window.getChildWindowCount(); 
			if( count > 0){
				for( int i=0; i<count; i++){
					DockingWindow w = search( window.getChildWindow(i), clazz ) ;
					if( w != null ){
						return w ;
					}
				}
			}
		}
		return null; 
	}
	
}
