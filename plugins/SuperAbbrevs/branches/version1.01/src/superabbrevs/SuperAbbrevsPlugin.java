package superabbrevs;

import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.guice.GuiceConfiguration;
import superabbrevs.installation.Installation;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SuperAbbrevsPlugin extends EditPlugin {
    
    public final static String NAME = "SuperAbbrevs";
    private static Map<Buffer,InputHandler> inputHandlers = 
    	new HashMap<Buffer, InputHandler>();
    
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
    	
    	InputHandler inputHandler = getInputHandler(view, textArea, buffer);
    	
    	switch (action) {
		case Esc:              inputHandler.esc();              break;
		case ShiftTab:         inputHandler.shiftTab();	        break;
		case ShowOptionPane:   inputHandler.showOptionsPane();  break;
		case ShowSearchDialog: inputHandler.showSearchDialog();	break;
		case Tab:              inputHandler.tab();              break;
		default: break;
		}
    }

	private static InputHandler getInputHandler(View view,
			JEditTextArea textArea, Buffer buffer) {
		InputHandler inputHandler = inputHandlers.get(buffer);
    	
    	if (inputHandler == null) {
    		JEditInterface jedit = new JEditInterfaceImpl(view, textArea, buffer);
        	Injector injector = Guice.createInjector(new GuiceConfiguration(jedit));
        	inputHandler = injector.getInstance(InputHandler.class);
        	inputHandlers.put(buffer, inputHandler);
		}
		return inputHandler;
	}
    
    public boolean usePluginHome() {
        return true;
    }
}
