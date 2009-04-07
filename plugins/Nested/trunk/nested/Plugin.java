package nested ;

import nested.manager.NestedTableModel;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

public class Plugin extends EBPlugin {
	
	public static final String NAME = "Nested";
	private static NestedTableModel model = null ; 
	
	public Plugin( ){
		super( ) ;
		model = new NestedTableModel( ) ;
	}
	
	public void start() { }
	
	public void stop() { 
		getModel( ).saveMap( ) ;
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
		if( model == null ) model = new NestedTableModel( ) ; 
		return model ;
	}
	
	protected void finalize() throws Throwable {
    try {
      getModel().saveMap( ) ;
    } finally {
      super.finalize();
    }
	}
	
}

