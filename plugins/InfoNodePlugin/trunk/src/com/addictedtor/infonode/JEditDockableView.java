package com.addictedtor.infonode;

import javax.swing.JComponent;

import net.infonode.docking.View;

@SuppressWarnings("serial")
public class JEditDockableView extends View {
	
    private String name;
    
	public JEditDockableView( JComponent dockable, String name, String title ){
		super( title, null, dockable ) ;
		this.name = name;
	}
	
	public JEditDockableView( InfoNodeDockingWindowManager wm , String name ){
		this( wm.makeDockable(name), name, wm.getDockableTitle(name) ) ;
		wm.put(name, this ) ;
	}

	public static void close(String name) {}
	
	public String getName()
	{
	    return name;
	}
	
}
