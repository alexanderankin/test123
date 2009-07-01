package com.addictedtor.infonode;

import javax.swing.JComponent;

import net.infonode.docking.View;

@SuppressWarnings("serial")
public class JEditDockableView extends View {
	
	public JEditDockableView( JComponent dockable, String name ){
		super( name, null, dockable ) ;
	}
	
	public JEditDockableView( InfoNodeDockingWindowManager wm , String name ){
		this( wm.makeDockable(name), name ) ;
		wm.put(name, this ) ;
	}

	public static void close(String name) {}
	
	
}
