package superabbrevs;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.installation.Installation;

public class SuperAbbrevsPlugin extends EditPlugin {
    
    public final static String NAME = "SuperAbbrevs";
    
    @Override
    public void start() {
        super.start();
        Installation.install();
    }
    
    @Override
    public void stop() {
        super.stop();
    }
    
    public static void handleAction(Actions action, View view, JEditTextArea textArea, 
            Buffer buffer) {
    	JEditInterface jEdit = new JEditInterface(view, textArea, buffer);
    	InputHandler inputHandler = new InputHandler(jEdit);
    	switch (action) {
		case Esc:              inputHandler.esc();              break;
		case ShiftTab:         inputHandler.shiftTab();	        break;
		case ShowOptionPane:   inputHandler.showOptionsPane();  break;
		case ShowSearchDialog: inputHandler.showSearchDialog();	break;
		case Tab:              inputHandler.tab();              break;
		default: break;
		}
    }
    
    public boolean usePluginHome() {
        return true;
    }
}
