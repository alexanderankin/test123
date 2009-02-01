package nested ;

import org.gjt.sp.jedit.* ;
import org.gjt.sp.jedit.textarea.* ;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log ;

import nested.manager.NestedTableModel ; 
import java.io.File ;

public class Plugin extends EBPlugin {
	
	public static final String NAME = "Nested";
	private static NestedTableModel model  ;
	private static File home ;
	
	public void start() { 
		model = new NestedTableModel( ) ;
		home = getPluginHome( ) ;
	}
	public void stop() { }
	
	public static File getHome( ){
		return home ; 
	}
	
	public void handleMessage(EBMessage message){
		if( message instanceof EditPaneUpdate ){
			EditPaneUpdate message_ = (EditPaneUpdate)message ;
			if( message_.getWhat() != EditPaneUpdate.CREATED ) return ;
			
			JEditTextArea textArea = message_.getEditPane().getTextArea() ;
			textArea.getPainter().addExtension( TextAreaPainter.BACKGROUND_LAYER , 
				new NestedTextAreaExtension( textArea ) ) ;
		}
	}

	public static NestedTableModel getModel( ){
		return model ;
	}
	
}

