package com.addictedtor.infonode;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.infonode.docking.View;

/** 
 * A docking window view that represents a jedit view 
 * 
 * @author Romain Francois <francoisromain@free.fr>
 *
 */
@SuppressWarnings("serial")
public class JEditViewView extends View {

	private org.gjt.sp.jedit.View view; 
	
	public JEditViewView(org.gjt.sp.jedit.View view){
		this( view, new JLabel( "waiting for the component" )) ;
 	}
	
	public JEditViewView( org.gjt.sp.jedit.View view, JComponent component ){
		super( "view", null, component ) ;
		this.view = view;
	}
	
	public org.gjt.sp.jedit.View getView() {
		return view ;
	}
	
}
