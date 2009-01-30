package nested ;

import org.gjt.sp.jedit.* ;
import org.gjt.sp.jedit.textarea.* ;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log ;

public class Plugin extends EBPlugin {
	
	public static final String NAME = "Nested";
	
	public void start() { }
	public void stop() { }
	
	public void handleMessage(EBMessage message){
		if( message instanceof EditPaneUpdate ){
			EditPaneUpdate message_ = (EditPaneUpdate)message ;
			if( message_.getWhat() != EditPaneUpdate.CREATED ) return ;
			
			JEditTextArea textArea = message_.getEditPane().getTextArea() ;
			textArea.getPainter().addExtension( TextAreaPainter.BACKGROUND_LAYER , 
				new NestedTextAreaExtension( textArea ) ) ;
		}
	}

}

